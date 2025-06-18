package back.model.msg;

import back.model.Model;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * 쪽지 모델 클래스.
 * MESSENGER 테이블의 정보를 담습니다.
 */
@Data
@NoArgsConstructor // 기본 생성자 자동 생성
@AllArgsConstructor // 모든 필드를 매개변수로 하는 생성자 자동 생성
@EqualsAndHashCode(callSuper = true)
public class MsgSearch extends Model {
	
	private String userId;          // 현재 사용자 ID
	
    private String msgBox;          // 조회할 편지함 (received, sent)
    
    private String msgType;         // 쪽지 분류 (ALL, A, N, I)
    
    private String searchCondition; // 검색 조건 (nickname, title)
    
    private String searchKeyword;   // 검색 키워드
    
    private String sortOrder;       // 정렬 순서 (DESC, ASC)

}