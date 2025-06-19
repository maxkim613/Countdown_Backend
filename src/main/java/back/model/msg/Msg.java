package back.model.msg;

import back.model.Model;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * API 요청 및 응답을 위한 통합 DTO 클래스.
 * 일반 쪽지, 문의, 경매 관련 메시지에 모두 사용됩니다.
 */
@Data
@NoArgsConstructor // 기본 생성자 자동 생성
@AllArgsConstructor // 모든 필드를 매개변수로 하는 생성자 자동 생성
@EqualsAndHashCode(callSuper = true)
public class Msg extends Model {
	
	//====== 고유 핵심 필드 ======//
    private Integer msgId;
    
    private String msgTitle;
    
    private String msgContent;
    
    private String msgType;         // 쪽지 타입 (A:경매, N:일반, I:문의)

    //====== 발신/수신 정보 필드 ======//
    private String senderId;
    
    private String receiverId;
    
    //====== UI 표시를 위해 JOIN하여 가져오는 필드 ======//
    private String senderNickname;
    
    private String receiverNickname;

    //====== 경매 관련 필드 (Nullable) ======//
    private String aucId;
    
    private String productName;     // 경매 이름 (UI 표시용)

    //====== 상태 정보 필드 ======//
    private String status;          // 쪽지 상태 (UNREAD, READ)
    
    private String readYn;          // 읽음 여부 (Y/N)

}