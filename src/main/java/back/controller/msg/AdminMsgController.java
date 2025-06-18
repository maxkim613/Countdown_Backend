package back.controller.msg;

import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import back.model.msg.Msg;
import back.model.msg.MsgSearch;
import back.service.msg.MsgService;
import back.util.ApiResponse;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/admin/msg")
@RequiredArgsConstructor
public class AdminMsgController {

    private final MsgService msgService;

    @GetMapping("/inquiries")
    public ResponseEntity<ApiResponse<List<Msg>>> getInquiries(@ModelAttribute MsgSearch search) {
        List<Msg> list = msgService.getInquiryList(search);
        return ResponseEntity.ok(new ApiResponse<>(true, "문의 목록 조회가 완료되었습니다.", list));
    }

    @GetMapping("/inquiries/{msgId}")
    public ResponseEntity<ApiResponse<Msg>> getInquiryDetail(@PathVariable int msgId) {
        // TODO: SecurityContext에서 현재 로그인한 관리자 ID 가져오기
        String adminId = "admin";
        Msg msg = msgService.getMsgDetail(msgId, adminId);
        return ResponseEntity.ok(new ApiResponse<>(true, "문의 상세 조회가 완료되었습니다.", msg));
    }

    @PostMapping("/reply")
    public ResponseEntity<ApiResponse<Void>> replyToInquiry(@RequestBody Msg reply) {
        // TODO: SecurityContext에서 현재 로그인한 관리자 ID 가져오기
        String adminId = "admin";
        msgService.createAdminReply(reply, adminId);
        return ResponseEntity.ok(new ApiResponse<>(true, "답변이 성공적으로 등록되었습니다.", null));
    }
}