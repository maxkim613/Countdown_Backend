package back.service.msg;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import back.exception.HException;
import back.mapper.msg.MsgMapper;
import back.mapper.user.UserMapper;
import back.model.auction.Auction;
import back.model.msg.Msg;
import back.model.msg.MsgSearch;
import back.model.vo.MsgVO;
import back.service.auction.AuctionService;
import jakarta.annotation.Resource;
import lombok.RequiredArgsConstructor;

/**
 * 쪽지 도메인 서비스 구현체
 */
@Service
@RequiredArgsConstructor
public class MsgServiceImpl implements MsgService {

	@Resource
	private final MsgMapper msgMapper;
	@Resource
	private final UserMapper userMapper;
	@Resource
    private AuctionService auctionService;
	
	private static final DateTimeFormatter DATE_FMT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    public List<Msg> getMsgList(MsgSearch search) {
        // 정렬 순서 기본값 설정
        if (!StringUtils.hasText(search.getSortOrder()) || !search.getSortOrder().equalsIgnoreCase("ASC")) {
            search.setSortOrder("DESC");
        }
        return msgMapper.selectMsgList(search);
    }

    @Override
    @Transactional
    public Msg getMsgDetail(int msgId, String userId) {
        Msg msg = msgMapper.selectMsgDetail(msgId);
        if (msg == null) {
            throw new HException("쪽지를 찾을 수 없습니다.", HttpStatus.NOT_FOUND);
        }

        boolean isReceiver = Objects.equals(userId, msg.getReceiverId());
        boolean isSender = Objects.equals(userId, msg.getSenderId());
        boolean isAdmin = "admin".equals(userId);

        if (!isReceiver && !isSender && !isAdmin) {
            throw new HException("권한이 없습니다.", HttpStatus.FORBIDDEN);
        }
        if (isReceiver && "N".equals(msg.getReadYn())) {
            msgMapper.updateReadStatus(Map.of("msgId", msgId, "userId", userId));
        }
        return msg;
    }

    @Override
    @Transactional
    public void createMsg(Msg msg, String senderId) {
    	// 받는 사람 닉네임으로 실제 ID를 조회
    	String receiverNickname = msg.getReceiverNickname();
    	
    	if (!StringUtils.hasText(receiverNickname) || !StringUtils.hasText(msg.getMsgTitle())) {
            throw new HException("받는 사람 닉네임과 제목은 필수입니다.");
        }
        
        // 닉네임으로 사용자 ID를 찾는 로직
        String receiverId = userMapper.findIdByNickname(receiverNickname);
        if (receiverId == null) {
            throw new HException("존재하지 않는 닉네임입니다.", HttpStatus.NOT_FOUND);
        }

        msg.setReceiverId(receiverId); // 조회한 ID를 DTO에 설정

        // 문의하기의 경우 수신자를 'admin'으로 고정
        if ("I".equals(msg.getMsgType())) {
            msg.setReceiverId("admin");
        }
        
        msgMapper.insertMsg(toMsgVO(msg, senderId));
    }

    @Override
    public List<Msg> getInquiryList(MsgSearch search) {
        return msgMapper.selectInquiryList(search);
    }

    @Override
    public void createAdminReply(Msg reply, String adminId) {
        if (!StringUtils.hasText(reply.getReceiverId())) {
            throw new HException("답변을 받을 사용자 ID는 필수입니다.");
        }
        reply.setMsgType("I");
        msgMapper.insertMsg(toMsgVO(reply, adminId));
    }

    private MsgVO toMsgVO(Msg msg, String senderId) {
        MsgVO msgVO = new MsgVO();
        msgVO.setSenderId(senderId);
        msgVO.setReceiverId(msg.getReceiverId());
        msgVO.setAucId(msg.getAucId());
        msgVO.setMsgTitle(msg.getMsgTitle());
        msgVO.setMsgContent(msg.getMsgContent());
        msgVO.setMsgType(msg.getMsgType());
        msgVO.setCreateId(senderId);
        return msgVO;
    }
    
    @Override
    public void markPushed(String systemId, Integer msgId) {
        // systemId 파라미터는 무시하고, 항상 'SYSTEM'으로 처리
        if (msgId != null) {
            msgMapper.updatePushStatus(msgId);
        }
    }
    
 // 자동 메시지 하드코딩 템플릿 메서드
    @Transactional
    public void sendAuctionApprovedMessage(String aucId) {
        Auction auction = auctionService.getAuctionById(aucId);
        String sellerId = auction.getUserId();
        String auctionTitle = auction.getAucTitle();
        if (sellerId == null) return;
        String senderId = "system";
        String title = "경매가 승인되었습니다";
        String content = String.format("회원님의 경매 [%s]가 승인되었습니다.", auctionTitle);
        MsgVO vo = new MsgVO();
        vo.setSenderId(senderId);
        vo.setReceiverId(sellerId);
        vo.setAucId(aucId);
        vo.setMsgType("A");
        vo.setMsgTitle(title);
        vo.setMsgContent(content);
        vo.setStatus("UNREAD");
        vo.setReadYn("N");
        vo.setPushYn("N");
        vo.setCreateId(senderId);
        vo.setCreateDt(LocalDateTime.now().format(DATE_FMT));
        vo.setUpdateId(senderId);
        vo.setUpdateDt(LocalDateTime.now().format(DATE_FMT));
        vo.setDelYn("N");
        msgMapper.insertMsg(vo);
    }

    @Transactional
    public void sendAuctionFinishedMessage(String aucId) {
        Auction auction = auctionService.getAuctionById(aucId);
        String sellerId = auction.getUserId();
        String winnerId = auction.getWinnerId();
        String auctionTitle = auction.getAucTitle();
        String priceStr = auction.getAucBprice();
        if (sellerId == null || winnerId == null) return;
        String senderId = "system";
        // 판매자
        {
            String title = "경매가 낙찰되었습니다";
            String content = String.format("[%s] 경매가 낙찰되었습니다. 낙찰가: %s원. 낙찰자와 거래를 진행하세요.", auctionTitle, priceStr);
            MsgVO vo = new MsgVO();
            vo.setSenderId(senderId);
            vo.setReceiverId(sellerId);
            vo.setAucId(aucId);
            vo.setMsgType("A");
            vo.setMsgTitle(title);
            vo.setMsgContent(content);
            vo.setStatus("UNREAD");
            vo.setReadYn("N");
            vo.setPushYn("N");
            vo.setCreateId(senderId);
            vo.setCreateDt(LocalDateTime.now().format(DATE_FMT));
            vo.setUpdateId(senderId);
            vo.setUpdateDt(LocalDateTime.now().format(DATE_FMT));
            vo.setDelYn("N");
            msgMapper.insertMsg(vo);
        }
        // 낙찰자
        {
            String title = "상품이 낙찰되었습니다";
            String content = String.format("회원님께서 참여하신 [%s] 경매가 종료되었습니다. 낙찰가: %s원. 판매자와 거래 진행해주세요.", auctionTitle, priceStr);
            MsgVO vo = new MsgVO();
            vo.setSenderId(senderId);
            vo.setReceiverId(winnerId);
            vo.setAucId(aucId);
            vo.setMsgType("A");
            vo.setMsgTitle(title);
            vo.setMsgContent(content);
            vo.setStatus("UNREAD");
            vo.setReadYn("N");
            vo.setPushYn("N");
            vo.setCreateId(senderId);
            vo.setCreateDt(LocalDateTime.now().format(DATE_FMT));
            vo.setUpdateId(senderId);
            vo.setUpdateDt(LocalDateTime.now().format(DATE_FMT));
            vo.setDelYn("N");
            msgMapper.insertMsg(vo);
        }
    }

    @Transactional
    public void sendAuctionFailedMessage(String aucId) {
    	Auction auction = auctionService.getAuctionById(aucId);
        if (aucId != null) {
        	String receiverId = auction.getUserId();
        	String senderId = "system";
            String title = "경매가 유찰되었습니다";
            String content = String.format("회원님의 경매 [%s]가 유찰되었습니다.", auction.getAucTitle());
        	MsgVO vo = new MsgVO();
            vo.setReceiverId(receiverId);
            vo.setSenderId(senderId);
            vo.setAucId(aucId);
            vo.setMsgType("A");
            vo.setMsgTitle(title);
            vo.setMsgContent(content);
            vo.setStatus("UNREAD");
            vo.setReadYn("N");
            vo.setPushYn("N");
            vo.setCreateId(senderId);
            vo.setCreateDt(LocalDateTime.now().format(DATE_FMT));
            vo.setUpdateId(senderId);
            vo.setUpdateDt(LocalDateTime.now().format(DATE_FMT));
            vo.setDelYn("N");
            msgMapper.insertMsg(vo);
        }
    }

}