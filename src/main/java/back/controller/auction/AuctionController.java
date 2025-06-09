package back.controller.auction;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import back.model.auction.Auction;
import back.model.common.CustomUserDetails;
import back.service.auction.AuctionService;
import back.util.ApiResponse;
import back.util.SecurityUtil;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/auc")
@Slf4j

// @RestController: Spring에서 웹 API를 만들 때 사용하는 특수한 컨트롤러 어노테이션
// RestController: 데이터(JSON)를 바로 응답으로 보내줌
//@RequestMapping: 어떤 경로(주소) 로 들어온 요청을 어떤 메서드 가 처리할지 정해줌
// 예를 들어 /api/board/list.do를 실행하면 getboardlist실행


public class AuctionController {
	
	@Autowired
	private AuctionService auctionSerice;
	//@RequestBody 클라이언트가 보낸 JSON 데이터를 자바 객체로 자동 매핑
	//@RestController @Controller + @ResponseBody의 기능을 합쳐놓은 거
	
	@PostMapping("/auclist.do")
	public ResponseEntity<?> getAuctionBoardList(@RequestBody Auction autcion) {
		//ResponseEntity: HTTP 상태 코드와 데이터를 같이 보내는 데 쓰는 객체
		//@RequestBody : **HTTP 요청 본문(Body)**에 담아서 보내는 JSON 데이터를 자바 객체로 자동 변환
		log.info(autcion.toString());
		List<Auction> auctionList = auctionSerice.getAuctionBoardList(autcion);
		Map dataMap = new HashMap();
		dataMap.put("list",auctionList);
		dataMap.put("autcion",autcion);
		return ResponseEntity.ok(new ApiResponse<>(true,"목록조회성공",dataMap));
	}
	
	@PostMapping("/aucmylist.do")
	public ResponseEntity<?> getMyAuctionBoardList(@RequestBody Auction autcion) {
		//ResponseEntity: HTTP 상태 코드와 데이터를 같이 보내는 데 쓰는 객체
		//@RequestBody : **HTTP 요청 본문(Body)**에 담아서 보내는 JSON 데이터를 자바 객체로 자동 변환
		log.info(autcion.toString());
		List<Auction> auctionList = auctionSerice.getMyAuctionBoardList(autcion);
		Map dataMap = new HashMap();
		dataMap.put("list",auctionList);
		dataMap.put("board",autcion);
		return ResponseEntity.ok(new ApiResponse<>(true,"목록조회성공",dataMap));
	}
	
	@PostMapping("/aucview.do")
	public ResponseEntity<?> getBoard(@RequestBody Auction autcion) {
		Auction selectAuction = auctionSerice.getAuctionById(autcion.getAuctionId());
		return ResponseEntity.ok(new ApiResponse<>(true,"조회성공",selectAuction));
	}
	//파일은 foam통신으로 해야한다.
	//@ModelAttribute foam통신을 할때 데이터를 받는 방식
	//
	@PostMapping(value = "/auccreate.do", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<?> createBoard(
			@ModelAttribute Auction auction,
			@RequestPart (value = "files", required = false) List<MultipartFile> files
	) throws NumberFormatException, IOException {
		CustomUserDetails userDetails = (CustomUserDetails) SecurityContextHolder.getContext()
				.getAuthentication().getPrincipal();
		SecurityUtil.checkAuthorization(userDetails);
		auction.setCreateId(userDetails.getUsername());
		auction.setFiles(files);
		boolean isCreated = auctionSerice.createaucBoard(auction); 
		return ResponseEntity.ok(new ApiResponse<>(isCreated, isCreated ? "게시글 등록 성공":"게시글 등록 실패",null));
	}
	@PostMapping(value = "/aucupdate.do", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)

    public ResponseEntity<?> updateBoard(

            @ModelAttribute Auction auction,

            @RequestPart(value = "files", required = false) List<MultipartFile> files
    ) throws NumberFormatException, IOException {

        CustomUserDetails userDetails = (CustomUserDetails) SecurityContextHolder.getContext()

                .getAuthentication().getPrincipal();

        SecurityUtil.checkAuthorization(userDetails);

        auction.setUpdateId(userDetails.getUsername());

        auction.setFiles(files);

        boolean isUpdated = auctionSerice.updateaucBoard(auction);

        return ResponseEntity.ok(new ApiResponse<>(isUpdated, isUpdated ? "게시글 수정 성공" : "게시글 수정 실패", null));
    }
	@PostMapping("/aucdelete.do")
	
	public ResponseEntity<?> deleteBoard(@RequestBody Auction auction) {
	
	    CustomUserDetails userDetails = (CustomUserDetails) SecurityContextHolder.getContext()
	
	            .getAuthentication().getPrincipal();
	
	    SecurityUtil.checkAuthorization(userDetails);
	
	    auction.setUpdateId(userDetails.getUsername());
	
	    boolean isDeleted = auctionSerice.deleteaucBoard(auction);
	
	    return ResponseEntity.ok(new ApiResponse<>(isDeleted, isDeleted ? "게시글 삭제 성공" : "게시글 삭제 실패", null));
	}
//	@PostMapping("/comment/auccreate.do")
//	public ResponseEntity<?> createComment(@RequestBody Comment comment) {
//		CustomUserDetails userDetails = (CustomUserDetails) SecurityContextHolder.getContext()
//		.getAuthentication().getPrincipal();
//		SecurityUtil.checkAuthorization(userDetails);
//		comment.setCreateId(userDetails.getUsername());
//		boolean isCreated = auctionSerice.createComment(comment);
//		return ResponseEntity.ok(new ApiResponse<>(isCreated, isCreated ? "댓글 등록 성공":"댓글 등록 실패",null));
//	}
//	@PostMapping("/comment/update.do")
//	public ResponseEntity<?> updateComment(@RequestBody Comment comment) {
//		CustomUserDetails userDetails = (CustomUserDetails) SecurityContextHolder.getContext()
//		.getAuthentication().getPrincipal();
//		SecurityUtil.checkAuthorization(userDetails);
//		comment.setUpdateId(userDetails.getUsername());
//		boolean isUpdated = auctionSerice.updateComment(comment);
//		return ResponseEntity.ok(new ApiResponse<>(isUpdated, isUpdated ? "댓글 수정 성공":"댓글 수정 실패",null));
//	}
//	@PostMapping("/comment/delete.do")
//	public ResponseEntity<?> deleteComment(@RequestBody Comment comment) {
//		CustomUserDetails userDetails = (CustomUserDetails) SecurityContextHolder.getContext()
//		.getAuthentication().getPrincipal();
//		SecurityUtil.checkAuthorization(userDetails);
//		comment.setUpdateId(userDetails.getUsername());
//		boolean isDeleted = auctionSerice.deleteComment(comment);
//		return ResponseEntity.ok(new ApiResponse<>(isDeleted, isDeleted ? "댓글 삭제 성공":"댓글 삭제 실패",null));
//	}

}
