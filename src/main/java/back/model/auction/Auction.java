package back.model.auction;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import back.model.Model;
import back.model.board.Comment;
import back.model.common.AucPostFile;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;



@Data
@NoArgsConstructor // 기본 생성자 자동 생성
@AllArgsConstructor // 모든 필드를 매개변수로 하는 생성자 자동 생성
@EqualsAndHashCode(callSuper = true)
public class Auction extends Model {


	
	private String aucId;   // 게시글 고유 식별자 (ID)
	private String aucTitle;     // 게시글 제목
	private String aucDescription;   // 게시글 내용
	private String aucCategory; // 게시글 조회수
	private String aucSprice; // 게시글 조회수
	private String aucCprice; // 게시글 조회수
	private String aucBprice; // 게시글 조회수
	private String aucLocation; // 게시글 조회수
	private String aucLikecnt; // 게시글 조회수
	private String aucMsgcnt; // 게시글 조회수
	private String aucDeadline; // 게시글 조회수
	private String aucStartdate; // 게시글 조회수
	private String aucStatus; // 게시글 조회수
	private String fileId;
	private int aucBidCount;
	private String aucBuyerId;    // 즉시 구매자 아이디
	private String aucBuyTime;    // 즉시 구매 시간
	private String userId;
 
	
	private String sortField = "CREATE_DT";
    private String sortOrder = "DESC";

	private String searchText; // 게시글 조회수
	private String startDate; // 게시글 조회수
	private String endDate; // 게시글 조회수

	private int rn;           // 게시글 순번(Row Number, DB 조회 시 사용)


	
	//<>는 제네릭(Generic)을 나타내는 기호로, 리스트(List)가 어떤 타입의 객체를 저장할지 지정하는 역할
	private List<AucPostFile> postFiles; // 게시글에 첨부된 파일들의 목록을 담는 리스트
	private List<AuctionBid> bidList;
	private List<MultipartFile> files;
	private String remainingFileIds;

}	