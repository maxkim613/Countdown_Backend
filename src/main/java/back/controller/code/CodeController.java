package back.controller.code;

import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import back.model.code.Code;
import back.model.code.GroupCode;
import back.service.code.CodeService;
import back.service.code.GroupCodeService;
import back.util.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/codes")
@RequiredArgsConstructor
public class CodeController {
	
	private final CodeService codeService;
    // GroupCodeService는 현재 이 컨트롤러에서 사용되지 않으므로 제거하거나 그대로 둘 수 있습니다.

    /**
     * Group ID에 해당하는 코드 목록을 반환합니다.
     * @param groupCode URL 경로에서 가져온 그룹 코드
     * @return 코드 목록
     */
    @GetMapping("/{groupCode}") // 수정: @GetMapping 경로 및 @RequestParam -> @PathVariable
    public ResponseEntity<ApiResponse<List<Code>>> getCodesByGroupCode(@PathVariable("groupCode") String groupCode) {
    	
    	// 컨트롤러가 요청을 제대로 받았는지 로그로 확인
        log.info("====== CodeController - API 요청 수신: /api/codes/{} ======", groupCode);
        
        List<Code> codes = codeService.getCodesByGroupCode(groupCode);
        
        // 서비스로부터 받은 결과 건수를 로그로 확인
        log.info("====== CodeController - 서비스 결과: {} 건 반환 ======", codes.size());
        
        return ResponseEntity.ok(new ApiResponse<>(true, "코드 조회가 완료되었습니다.", codes));
    }
    
}