package back.mapper.msg;

import java.util.List;
import java.util.Map;
import org.apache.ibatis.annotations.Mapper;
import back.model.msg.Msg;
import back.model.msg.MsgSearch;
import back.model.vo.MsgVO;

@Mapper
public interface MsgMapper {

	List<Msg> selectMsgList(MsgSearch search);
	
    List<Msg> selectInquiryList(MsgSearch search);
    
    Msg selectMsgDetail(int msgId);
    
    int insertMsg(MsgVO msgVO);
    
    int updateReadStatus(Map<String, Object> params);
    
    List<MsgVO> selectPushTargets();
    
    int updatePushStatus(int msgId);
    
}