package back.service.code;

import java.util.List;
import org.springframework.stereotype.Service;
import back.model.code.Code;

@Service
public interface CodeService {
	
	List<Code> getCodesByGroupCode(String groupId);
    
}