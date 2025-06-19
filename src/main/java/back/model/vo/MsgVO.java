package back.model.vo;

import back.model.Model;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * MESSENGER 테이블과 1:1로 매핑되는 VO
 */
@Data
@NoArgsConstructor // 기본 생성자 자동 생성
@AllArgsConstructor // 모든 필드를 매개변수로 하는 생성자 자동 생성
@EqualsAndHashCode(callSuper = true)
public class MsgVO extends Model {
	
	private Integer msgId;
	
    private String senderId;
    
    private String receiverId;
    
    private String aucId;
    
    private String msgTitle;
    
    private String msgContent;
    
    private String msgType;
    
    private String status;
    
    private String readYn;
    
    private String pushYn;
    
    private String delYn;

}