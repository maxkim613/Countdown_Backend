package back.service.code;

import java.util.List;
import org.springframework.stereotype.Service;
import back.mapper.code.GroupCodeMapper;
import back.model.code.GroupCode;

@Service
public class GroupCodeServiceImpl implements GroupCodeService {
	
    private final GroupCodeMapper mapper;

    public GroupCodeServiceImpl(GroupCodeMapper mapper) {
    	
        this.mapper = mapper;
        
    }

    @Override
    public List<GroupCode> getAllGroups() {
    	
        return mapper.selectAllGroupCodes();
        
    }
}