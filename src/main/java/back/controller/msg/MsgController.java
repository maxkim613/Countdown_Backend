package back.controller.msg;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;
import back.exception.HException;
import back.model.msg.Msg;
import back.model.msg.MsgSearch;
import back.service.msg.MsgService;
import back.util.ApiResponse;

@RestController
@RequestMapping("/api/msg")
@RequiredArgsConstructor
public class MsgController {

    private final MsgService msgService;

    /**
     * 내 쪽지 목록 조회
     */
    @GetMapping("")
    public ResponseEntity<ApiResponse<List<Msg>>> getMyMsg(@ModelAttribute MsgSearch search, Authentication authentication) {
        // 1. 로그인 여부 확인
        if (authentication == null) {
            throw new HException("로그인이 필요합니다.", HttpStatus.UNAUTHORIZED);
        }
        // 2. 실제 로그인한 사용자의 ID를 가져옵니다.
        String currentUserId = authentication.getName(); // Spring Security는 기본적으로 ID를 name으로 반환
        
        search.setUserId(currentUserId);
        List<Msg> list = msgService.getMsgList(search);
        return ResponseEntity.ok(new ApiResponse<>(true, "목록 조회가 완료되었습니다.", list));
    }

    /**
     * 쪽지 상세 조회
     */
    @GetMapping("/{msgId}")
    public ResponseEntity<ApiResponse<Msg>> getMsgDetail(@PathVariable("msgId") int msgId, Authentication authentication) {
        if (authentication == null) {
            throw new HException("로그인이 필요합니다.", HttpStatus.UNAUTHORIZED);
        }
        String currentUserId = authentication.getName();
        
        Msg msg = msgService.getMsgDetail(msgId, currentUserId);
        return ResponseEntity.ok(new ApiResponse<>(true, "상세 조회가 완료되었습니다.", msg));
    }

    /**
     * 쪽지 보내기
     */
    @PostMapping("")
    public ResponseEntity<ApiResponse<Void>> sendMsg(@RequestBody Msg msg, Authentication authentication) {
        if (authentication == null) {
            throw new HException("로그인이 필요합니다.", HttpStatus.UNAUTHORIZED);
        }
        String senderId = authentication.getName();
        
        msgService.createMsg(msg, senderId);
        return ResponseEntity.ok(new ApiResponse<>(true, "쪽지가 성공적으로 발송되었습니다.", null));
    }
}