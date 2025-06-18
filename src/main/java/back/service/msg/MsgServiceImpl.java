package back.service.msg;

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
import back.model.msg.Msg;
import back.model.msg.MsgSearch;
import back.model.vo.MsgVO;
import lombok.RequiredArgsConstructor;

/**
 * 쪽지 도메인 서비스 구현체
 */
@Service
@RequiredArgsConstructor
public class MsgServiceImpl implements MsgService {

	private final MsgMapper msgMapper;
	private final UserMapper userMapper;

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
    
}