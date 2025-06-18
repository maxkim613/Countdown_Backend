package back.mapper.msg;

import java.util.List;
import java.util.Map;
import org.apache.ibatis.annotations.Mapper;
import back.model.msg.Msg;
import back.model.msg.MsgSearch;
import back.model.vo.MsgVO;

@Mapper
public interface MsgMapper {

	public List<Msg> selectMsgList(MsgSearch search);
	
	public List<Msg> selectInquiryList(MsgSearch search);
    
	public Msg selectMsgDetail(int msgId);
    
	public int insertMsg(MsgVO msgVO);
    
	public int updateReadStatus(Map<String, Object> params);
    
	public List<MsgVO> selectPushTargets();
    
	public int updatePushStatus(int msgId);
    
}