package back.model.board;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import back.model.Model;
import back.model.common.PostFile;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;



@Data
@NoArgsConstructor // 기본 생성자 자동 생성
@AllArgsConstructor // 모든 필드를 매개변수로 하는 생성자 자동 생성
@EqualsAndHashCode(callSuper = true)
public class Board extends Model {


	
	private String boardId;   // 게시글 고유 식별자 (ID)
	private String title;     // 게시글 제목
	private String content;   // 게시글 내용
	private String viewCount; // 게시글 조회수
	private String sortField = "CREATE_DT";
    private String sortOrder = "DESC";

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
	
	//<>는 제네릭(Generic)을 나타내는 기호로, 리스트(List)가 어떤 타입의 객체를 저장할지 지정하는 역할
	private List<PostFile> postFiles; // 게시글에 첨부된 파일들의 목록을 담는 리스트
	private List<Comment> comments; // 게시글에 작성된 댓글 목록을 담는 리스트
	
	private List<MultipartFile> files;
	private String remainingFileIds;

}	