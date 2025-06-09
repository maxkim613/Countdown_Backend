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
	
	private String searchText; // 게시글 조회수
	private String startDate; // 게시글 조회수
	private String endDate; // 게시글 조회수 
	private int rn;           // 게시글 순번(Row Number, DB 조회 시 사용)
	private int startRow;     // 페이지 내의 시작 행 번호 (페이징 처리용)
	private int endRow;       // 페이지 내의 끝 행 번호 (페이징 처리용) 
	private int page = 1;         // 현재 페이지 번호
	private int size = 10;         // 한 페이지에 보여줄 게시글 개수 
	private int totalCount;   // 전체 게시글 개수
	private int totalPages;   // 전체 페이지 수 
	private String delYn;
	
    private String userId;    // 사용자 ID (Primary Key)
    private String username;  // 사용자 이름
    private String password;  // 비밀번호 (암호화 저장됨)
    private String email;     // 이메일 
    private String sortField = "CREATE_DT";
    private String sortOrder = "DESC";
}