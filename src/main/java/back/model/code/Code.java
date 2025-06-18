package back.model.code;

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
public class Code extends Model {
	
	private String codeId;
	
    private String groupId;
    
    private String codeName;
    
    private String codeDesc1;
    
    private String codeDesc2;
    
    private String codeDesc3;

}