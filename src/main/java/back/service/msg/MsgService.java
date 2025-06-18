package back.service.msg; // 기본 패키지 com.cd 활용

import back.model.msg.Msg;
import back.model.msg.MsgSearch;
import java.util.List;

/**W
 * 쪽지 서비스 인터페이스
 */
public interface MsgService {
	
	List<Msg> getMsgList(MsgSearch search);
	
    Msg getMsgDetail(int msgId, String userId);
    
    void createMsg(Msg msg, String senderId);
    
    List<Msg> getInquiryList(MsgSearch search);
    
    void createAdminReply(Msg reply, String adminId);
    
}