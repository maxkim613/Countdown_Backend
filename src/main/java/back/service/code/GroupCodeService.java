package back.service.code;

import java.util.List;
import org.springframework.stereotype.Service;
import back.model.code.GroupCode;


@Service
public interface GroupCodeService {
	
    List<GroupCode> getAllGroups();
    
}