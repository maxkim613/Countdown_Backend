package back.mapper.code;

import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import back.model.code.Code;

@Mapper
public interface CodeMapper {

	/**
     * 그룹 코드를 기준으로 하위 코드 목록을 조회합니다.
     * @param groupCode 조회할 그룹 코드
     * @return 코드 목록
     */
    List<Code> selectCodesByGroupCode(String groupCode);
	
}