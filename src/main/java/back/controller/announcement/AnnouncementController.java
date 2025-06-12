package back.controller.announcement;

import org.springframework.http.MediaType;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import back.model.announcement.Announcement;
import back.model.board.Board;
import back.model.board.Comment;
import back.model.common.CustomUserDetails;
import back.service.announcement.AnnouncementService;
import back.service.board.BoardService;
import back.util.ApiResponse;
import back.util.SecurityUtil;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/ann")
@Slf4j

// @RestController: Spring에서 웹 API를 만들 때 사용하는 특수한 컨트롤러 어노테이션
// RestController: 데이터(JSON)를 바로 응답으로 보내줌
//@RequestMapping: 어떤 경로(주소) 로 들어온 요청을 어떤 메서드 가 처리할지 정해줌
// 예를 들어 /api/board/list.do를 실행하면 getboardlist실행


public class AnnouncementController {
	
	@Autowired
	private AnnouncementService announcementService;
	//@RequestBody 클라이언트가 보낸 JSON 데이터를 자바 객체로 자동 매핑
	//@RestController @Controller + @ResponseBody의 기능을 합쳐놓은 거
	
	@PostMapping("/annlist.do")
	public ResponseEntity<?> getAnnouncementList(@RequestBody Announcement announcement) {
        log.info(announcement.toString());
        List<Announcement> announcementList = announcementService.getAnnouncementList(announcement);
        Map dataMap = new HashMap();
        dataMap.put("list", announcementList);
        dataMap.put("announcement", announcement);
        return ResponseEntity.ok(new ApiResponse<>(true, "공지사항 목록 조회 성공", dataMap));
    }
	
	@PostMapping("/annview.do")
	public ResponseEntity<?> getannouncement(@RequestBody Announcement announcement) {
		Announcement selectannAnnouncement = announcementService.getAnnouncementById(announcement.getAnnId());
		return ResponseEntity.ok(new ApiResponse<>(true,"조회성공",selectannAnnouncement));
	}
	//파일은 foam통신으로 해야한다.
	//@ModelAttribute foam통신을 할때 데이터를 받는 방식
	//
	@PostMapping("/anncreate.do")
	public ResponseEntity<?> createAnnouncement(@RequestBody Announcement announcement) throws NumberFormatException, IOException {
    	String createId = SecurityContextHolder.getContext().getAuthentication().getName();
    	announcement.setCreateId(createId);
        boolean result = announcementService.createAnnouncement(announcement);
        if (result) {
        	return ResponseEntity.ok(Map.of("success", true));
        } else {
        	return ResponseEntity.badRequest().body(Map.of("success", false, "message", "등록 실패"));
        }
    }
	
	@PostMapping("/annupdate.do")
	public ResponseEntity<?> updateAnnouncement(@RequestBody Announcement announcement) throws NumberFormatException, IOException {
        CustomUserDetails userDetails = (CustomUserDetails) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();
        SecurityUtil.checkAuthorization(userDetails);
        if (announcement.getAnnTitle() == null || announcement.getAnnTitle().trim().isEmpty()) {
            return ResponseEntity.badRequest().body(new ApiResponse<>(false, "제목은 필수입니다.", null));
        }
        if (announcement.getAnnContent() == null || announcement.getAnnContent().trim().isEmpty()) {
            return ResponseEntity.badRequest().body(new ApiResponse<>(false, "내용은 필수입니다.", null));
        }
        announcement.setUpdateId(userDetails.getUsername());
        boolean isUpdated = announcementService.updateAnnouncement(announcement);
        return ResponseEntity.ok(new ApiResponse<>(isUpdated, isUpdated ? "공지사항 수정 성공" : "공지사항 수정 실패", null));
    }
	@PostMapping("/anndelete.do")
	public ResponseEntity<?> deleteNotice(@RequestBody Announcement announcement) {
        CustomUserDetails userDetails = (CustomUserDetails) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();
        SecurityUtil.checkAuthorization(userDetails);
        announcement.setUpdateId(userDetails.getUsername());
        boolean isDeleted = announcementService.deleteAnnouncement(announcement);
        return ResponseEntity.ok(new ApiResponse<>(isDeleted, isDeleted ? "공지사항 삭제 성공" : "공지사항 삭제 실패", null));
    }
}
