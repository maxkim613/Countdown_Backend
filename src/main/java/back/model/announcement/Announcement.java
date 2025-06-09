package back.model.announcement;

import back.model.Model;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;



@Data
@NoArgsConstructor // 기본 생성자 자동 생성
@AllArgsConstructor // 모든 필드를 매개변수로 하는 생성자 자동 생성
@EqualsAndHashCode(callSuper = true)
public class Announcement extends Model {


	
	private String annId;   // 게시글 고유 식별자 (ID)
	private String anntitle;     // 게시글 제목
	private String anncontent;   // 게시글 내용
	private String sortField = "CREATE_DT";
    private String sortOrder = "DESC";


	private int rn;           // 게시글 순번(Row Number, DB 조회 시 사용)
	private int startRow;     // 페이지 내의 시작 행 번호 (페이징 처리용)
	private int endRow;       // 페이지 내의 끝 행 번호 (페이징 처리용)


	private String remainingFileIds;

}	