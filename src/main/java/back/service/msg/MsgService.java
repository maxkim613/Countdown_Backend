package back.service.msg; // 기본 패키지 com.cd 활용

import back.model.msg.Msg;
import back.model.msg.MsgSearch;
import java.util.List;

/**W
 * 쪽지 서비스 인터페이스
 */
public interface MsgService {
	
	public List<Msg> getMsgList(MsgSearch search);
	
	public Msg getMsgDetail(int msgId, String userId);
    
	public void createMsg(Msg msg, String senderId);
    
	public List<Msg> getInquiryList(MsgSearch search);
    
	public void createAdminReply(Msg reply, String adminId);
    
	/**
     * 푸시 전송 후 상태 업데이트
     */
	public void markPushed(String systemId, Integer msgId);
	
	// 경매 상품 승인 시 쪽지 자동 발송
	public void sendAuctionApprovedMessage(String aucId);
    
	// 낙찰 시 쪽지 자동 전송
	public void sendAuctionFinishedMessage(String aucId);
	
	// 유찰 시 쪽지 자동 전송
	public void sendAuctionFailedMessage(String aucId);
}