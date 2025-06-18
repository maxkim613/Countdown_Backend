package back.mapper.batch;

import org.apache.ibatis.annotations.Mapper;
import back.model.vo.MsgBatchVO;

/**
 * 배치 작업 관련 데이터베이스 처리를 위한 MyBatis Mapper 인터페이스.
 * BatchMapper.xml 파일과 매핑됩니다.
 */
@Mapper
public interface MsgBatchMapper {
	
	public int insertBatchLog(MsgBatchVO logVO);
	
	public int updateBatchLog(MsgBatchVO logVO);
	
}