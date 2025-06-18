package back.mapper.code;

import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import back.model.code.GroupCode;

@Mapper
public interface GroupCodeMapper {

	List<GroupCode> selectAllGroupCodes();
	
}