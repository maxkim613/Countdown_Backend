package back.model.batch;

import back.model.Model;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * 배치 작업에서 사용할 데이터 모델
 */
@Data
@Builder
@NoArgsConstructor // 기본 생성자 자동 생성
@AllArgsConstructor // 모든 필드를 매개변수로 하는 생성자 자동 생성
@EqualsAndHashCode(callSuper = true)
public class MsgBatch extends Model {
	
	private Integer msgJobId;
	
    private String userId;
    
    private Integer msgId;
    
    private Integer aucId;
    
    private String msgJobName;
    
    private String status;
    
    private String startTime;
    
    private String endTime;
    
    private String exitCode;
    
    private String exitMsg;
    
    private String delYn;

}