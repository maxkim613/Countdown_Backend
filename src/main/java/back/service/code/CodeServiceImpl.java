package back.service.code;

import java.util.List;
import org.springframework.stereotype.Service;
import back.mapper.code.CodeMapper;
import back.model.code.Code;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class CodeServiceImpl implements CodeService {

    private final CodeMapper codeMapper;

    @Override
    public List<Code> getCodesByGroupCode(String groupCode) {
    	
    	// 서비스가 파라미터를 제대로 받았는지 로그로 확인
        log.info("====== CodeService - DB 조회를 위해 받은 groupCode: {} ======", groupCode);
        
        List<Code> codes = codeMapper.selectCodesByGroupCode(groupCode);
        
        // DB 조회 결과 실제 건수를 로그로 확인
        log.info("====== CodeService - DB 조회 결과: {} 건 ======", codes.size());
        
        return codes;

    }
}