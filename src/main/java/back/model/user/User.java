package back.model.user;

import back.model.Model;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor // 기본 생성자 자동 생성
@AllArgsConstructor // 모든 필드를 매개변수로 하는 생성자 자동 생성
@EqualsAndHashCode(callSuper = true)
public class User extends Model {
    private String userId;    // 사용자 ID (Primary Key)
    private String username;  // 사용자 이름
    private String password;
    private String nickname;// 비밀번호 (암호화 저장됨)
    private String email; 
    private String userTel; 
    private String postCode;
    private String addr;
    private String addrD; 
    
    private String createId;  // 생성자 ID
    private String createDt;  // 생성일시
    private String updateId;  // 수정자 ID
    private String updateDt;  // 수정일시
    private String delYn;     // 삭제 여부 (Y/N)
    
    private String searchText;
	private String startDate;
	private String endDate;
	
	private int rn;  // 행 번호 (데이터베이스에서 조회 시 사용)
	private int startRow;   // 페이징 처리: 시작 행
	private int endRow; // 페이징 처리: 끝 행	
	private int page = 1;   // 현재 페이지 번호
	private int size = 10;   // 한 페이지에 표시할 게시글 개수	
	private int totalCount;  // 총 게시글 개수
	private int totalPages;  // 총 페이지 수
	private String sortField = "CREATE_DT";
	private String sortOrder = "DESC";   
    
}