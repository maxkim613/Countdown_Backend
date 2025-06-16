package back.controller.auction;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import back.dto.AuctionBidRequest;
import back.exception.HException;
import back.model.auction.Auction;
import back.model.auction.AuctionBid;
import back.model.common.CustomUserDetails;
import back.service.auction.AuctionService;
import back.util.ApiResponse;
import back.util.SecurityUtil;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/auc")
@Slf4j

// @RestController: Spring에서 웹 API를 만들 때 사용하는 특수한 컨트롤러 어노테이션
// RestController: 데이터(JSON)를 바로 응답으로 보내줌
//@RequestMapping: 어떤 경로(주소) 로 들어온 요청을 어떤 메서드 가 처리할지 정해줌
// 예를 들어 /api/board/list.do를 실행하면 getboardlist실행


public class AuctionController {
	
	@Autowired
	private AuctionService auctionService;
	//@RequestBody 클라이언트가 보낸 JSON 데이터를 자바 객체로 자동 매핑
	//@RestController @Controller + @ResponseBody의 기능을 합쳐놓은 거
	
	
	@PostMapping("/auclist.do")
	public ResponseEntity<?> getAuctionBoardList(@RequestBody Auction autcion) {
		//ResponseEntity: HTTP 상태 코드와 데이터를 같이 보내는 데 쓰는 객체
		//@RequestBody : **HTTP 요청 본문(Body)**에 담아서 보내는 JSON 데이터를 자바 객체로 자동 변환
		log.info(autcion.toString());
		List<Auction> auctionList = auctionService.getAuctionBoardList(autcion);
		Map dataMap = new HashMap();
		dataMap.put("list",auctionList);
		dataMap.put("autcion",autcion);
		return ResponseEntity.ok(new ApiResponse<>(true,"목록조회성공",dataMap));
	}
	
	@PostMapping("/aucmylist.do")
	public ResponseEntity<?> getMyAuctionBoardList(@RequestBody Auction autcion) {
		//ResponseEntity: HTTP 상태 코드와 데이터를 같이 보내는 데 쓰는 객체
		//@RequestBody : **HTTP 요청 본문(Body)**에 담아서 보내는 JSON 데이터를 자바 객체로 자동 변환
		log.info(autcion.toString());
		List<Auction> auctionList = auctionService.getMyAuctionBoardList(autcion);
		Map dataMap = new HashMap();
		dataMap.put("list",auctionList);
		dataMap.put("autcion",autcion);
		return ResponseEntity.ok(new ApiResponse<>(true,"목록조회성공",dataMap));
	}
	
	@PostMapping("/aucview.do")
	public ResponseEntity<?> getBoard(@RequestBody Auction autcion) {
		Auction selectAuction = auctionService.getAuctionById(autcion.getAucId());
		List<AuctionBid> bidList = auctionService.getBidList(autcion.getAucId());
		selectAuction.setBidList(bidList);
		return ResponseEntity.ok(new ApiResponse<>(true,"조회성공",selectAuction));
	}
	//파일은 foam통신으로 해야한다.
	//@ModelAttribute foam통신을 할때 데이터를 받는 방식
	//
	@PostMapping(value = "/auccreate.do", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<?> createBoard(
			@ModelAttribute Auction auction,
			@RequestPart (value = "files", required = false) List<MultipartFile> files
	) throws NumberFormatException, IOException {
		CustomUserDetails userDetails = (CustomUserDetails) SecurityContextHolder.getContext()
				.getAuthentication().getPrincipal();
		SecurityUtil.checkAuthorization(userDetails);
		auction.setCreateId(userDetails.getUsername());
		auction.setFiles(files);
		boolean isCreated = auctionService.createaucBoard(auction); 
		return ResponseEntity.ok(new ApiResponse<>(isCreated, isCreated ? "게시글 등록 성공":"게시글 등록 실패",null));
	}
	@PostMapping(value = "/aucupdate.do", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)

    public ResponseEntity<?> updateBoard(

            @ModelAttribute Auction auction

//            @RequestPart(value = "files", required = false) List<MultipartFile> files
    ) throws NumberFormatException, IOException {

        CustomUserDetails userDetails = (CustomUserDetails) SecurityContextHolder.getContext()

                .getAuthentication().getPrincipal();

        SecurityUtil.checkAuthorization(userDetails);

        auction.setUpdateId(userDetails.getUsername());

//        auction.setFiles(files);

        boolean isUpdated = auctionService.updateaucBoard(auction);

        return ResponseEntity.ok(new ApiResponse<>(isUpdated, isUpdated ? "게시글 수정 성공" : "게시글 수정 실패", null));
    }
	@PostMapping("/aucdelete.do")
	
	public ResponseEntity<?> deleteBoard(@RequestBody Auction auction) {
	
	    CustomUserDetails userDetails = (CustomUserDetails) SecurityContextHolder.getContext()
	
	            .getAuthentication().getPrincipal();
	
	    SecurityUtil.checkAuthorization(userDetails);
	
	    auction.setUpdateId(userDetails.getUsername());
	
	    boolean isDeleted = auctionService.deleteaucBoard(auction);
	
	    return ResponseEntity.ok(new ApiResponse<>(isDeleted, isDeleted ? "게시글 삭제 성공" : "게시글 삭제 실패", null));
	}
	
	@PostMapping("/aucbid.do")
	public ResponseEntity<?> placeBid(@RequestBody AuctionBidRequest bidRequest) {
	    try {
	        boolean result = auctionService.placeBid(bidRequest);
	        return ResponseEntity.ok(new ApiResponse<>(result, result ? "입찰 성공" : "입찰 실패"));
	    } catch (HException e) {
	        return ResponseEntity.badRequest().body(new ApiResponse<>(false, e.getMessage()));
	    }
	}
	
	@PostMapping("/aucbuynow.do")
	public ResponseEntity<?> buyNow(@RequestBody Map<String, String> request) {
	    String aucId = request.get("aucId");
	    String userId = request.get("userId");
	    
	    try {
	        boolean success = auctionService.buyNow(aucId, userId);
	        return ResponseEntity.ok(new ApiResponse<>(success, success ? "즉시 구매 성공" : "즉시 구매 실패"));
	    } catch (HException e) {
	        return ResponseEntity.status(e.getStatus()).body(new ApiResponse<>(false, e.getMessage()));
	    }
	}
	// 좋아요 상태 조회
	@GetMapping("/auclike/status")
	public ResponseEntity<?> getLikeStatus(@RequestParam String aucId, @RequestParam String userId) {
	    try {
	        String status = auctionService.getLikeStatus(aucId, userId); // 'Y' or 'N' or null
	        return ResponseEntity.ok(new ApiResponse<>(true, "좋아요 상태 조회 성공", status));
	    } catch (Exception e) {
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
	            .body(new ApiResponse<>(false, "좋아요 상태 조회 실패"));
	    }
	}

	// 좋아요 토글 (좋아요/취소)
	@PostMapping("/auclike/toggle")
	public ResponseEntity<?> toggleLike(@RequestBody Map<String, String> request) {
	    String aucId = request.get("aucId");
	    String userId = request.get("userId");
	    
	    try {
	        boolean result = auctionService.toggleLike(aucId, userId);
	        return ResponseEntity.ok(new ApiResponse<>(result, result ? "좋아요 상태 변경 성공" : "좋아요 상태 변경 실패"));
	    } catch (Exception e) {
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
	            .body(new ApiResponse<>(false, "좋아요 상태 변경 실패"));
	    }
	}
	
	@PostMapping("/aucmybidlist.do")
	public ResponseEntity<?> getInProgressByBuyer(@RequestBody Auction autcion) {
		//ResponseEntity: HTTP 상태 코드와 데이터를 같이 보내는 데 쓰는 객체
		//@RequestBody : **HTTP 요청 본문(Body)**에 담아서 보내는 JSON 데이터를 자바 객체로 자동 변환
		log.info(autcion.toString());
		List<Auction> auctionmybidList = auctionService.getInProgressByBuyer(autcion);
		Map dataMap = new HashMap();
		dataMap.put("list",auctionmybidList);
		dataMap.put("autcion",autcion);
		return ResponseEntity.ok(new ApiResponse<>(true,"나의 입찰 목록조회 성공",dataMap));
	}
	
	@PostMapping("/aucmyselllist.do")
	public ResponseEntity<?> getInProgressByCreator(@RequestBody Auction autcion) {
		//ResponseEntity: HTTP 상태 코드와 데이터를 같이 보내는 데 쓰는 객체
		//@RequestBody : **HTTP 요청 본문(Body)**에 담아서 보내는 JSON 데이터를 자바 객체로 자동 변환
		log.info(autcion.toString());
		List<Auction> auctionmysellList = auctionService.getInProgressByCreator(autcion);
		Map dataMap = new HashMap();
		dataMap.put("list",auctionmysellList);
		dataMap.put("autcion",autcion);
		return ResponseEntity.ok(new ApiResponse<>(true,"나의 경매 물품 목록조회성공",dataMap));
	}
	
	@PostMapping("/aucmysellcompletelist.do")
	public ResponseEntity<?> getCompletedByCreator(@RequestBody Auction autcion) {
		//ResponseEntity: HTTP 상태 코드와 데이터를 같이 보내는 데 쓰는 객체
		//@RequestBody : **HTTP 요청 본문(Body)**에 담아서 보내는 JSON 데이터를 자바 객체로 자동 변환
		log.info(autcion.toString());
		List<Auction> auctionmycompleteList = auctionService.getCompletedByCreator(autcion);
		Map dataMap = new HashMap();
		dataMap.put("list",auctionmycompleteList);
		dataMap.put("autcion",autcion);
		return ResponseEntity.ok(new ApiResponse<>(true,"나의 경매완료 목록조회성공",dataMap));
	}
	
	@PostMapping("/aucmybuycompletelist.do")
	public ResponseEntity<?> getCompletedByMe(@RequestBody Auction autcion) {
		//ResponseEntity: HTTP 상태 코드와 데이터를 같이 보내는 데 쓰는 객체
		//@RequestBody : **HTTP 요청 본문(Body)**에 담아서 보내는 JSON 데이터를 자바 객체로 자동 변환
		log.info(autcion.toString());
		List<Auction> auctionmybuycompleteList = auctionService.getCompletedByMe(autcion);
		Map dataMap = new HashMap();
		dataMap.put("list",auctionmybuycompleteList);
		dataMap.put("autcion",autcion);
		return ResponseEntity.ok(new ApiResponse<>(true,"나의 구매완료 목록조회성공",dataMap));
	}
	
	@PostMapping("/aucwaitinglist.do")
	public ResponseEntity<?> getWaitingAuctionList(@RequestBody Auction autcion) {
		//ResponseEntity: HTTP 상태 코드와 데이터를 같이 보내는 데 쓰는 객체
		//@RequestBody : **HTTP 요청 본문(Body)**에 담아서 보내는 JSON 데이터를 자바 객체로 자동 변환
		log.info(autcion.toString());
		List<Auction> auctionwaitingList = auctionService.getWaitingAuctionList(autcion);
		Map dataMap = new HashMap();
		dataMap.put("list",auctionwaitingList);
		dataMap.put("autcion",autcion);
		return ResponseEntity.ok(new ApiResponse<>(true,"경매대기 목록 조회성공",dataMap));
	}
	
	
	
	
}
