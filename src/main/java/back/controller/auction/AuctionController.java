package back.controller.auction;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jakarta.servlet.ServletContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
import back.model.common.AucPostFile;
import back.model.common.CustomUserDetails;
import back.service.auction.AuctionService;
import back.service.file.AucFileService;
import back.util.ApiResponse;
import back.util.SecurityUtil;
import jakarta.servlet.http.HttpServletResponse;
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
	private ServletContext servletContext;
	
	
	@Value("${myapp.apiBaseUrl}")
	private String apiBaseUrl;
	//application.properties에 있는 값을 apiBaseUrl에 넣어준다고 보면됨
	
	@Autowired
	private AuctionService auctionService;
	//@RequestBody 클라이언트가 보낸 JSON 데이터를 자바 객체로 자동 매핑
	//@RestController @Controller + @ResponseBody의 기능을 합쳐놓은 거
	
	@Autowired
	private AucFileService fileService; 
	
	
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
	public ResponseEntity<?> getLikeStatus(
	    @RequestParam(name = "aucId") String aucId,
	    @RequestParam(name = "userId") String userId
	) {
	    try {
	        String status = auctionService.getLikeStatus(aucId, userId);
	        return ResponseEntity.ok(new ApiResponse<>(true, "좋아요 상태 조회 성공", status));
	    } catch (Exception e) {
	        e.printStackTrace();
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
	    	 e.printStackTrace();
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

	@PostMapping("/auc/statusupdate.do")
	public ResponseEntity<?> updateStatusToInProgress(@RequestBody Auction auction) {
		log.info("경매 상태 변경 요청 수신. Auc ID: {}", auction.getAucId());
		auction.setAucStatus("경매중");
		int result = auctionService.updateStatusToInProgress(auction);

	    if (result > 0) {
	    	log.info("경매 상태가 '경매중'으로 변경 성공. Auc ID: {}", auction.getAucId());
	        return ResponseEntity.ok(new ApiResponse<>(true, "경매 상태가 '경매중'으로 변경되었습니다.", null));
	    } else {
	    	log.warn("경매 상태 변경 실패. Auc ID: {}", auction.getAucId());
	        return ResponseEntity
	            .status(HttpStatus.BAD_REQUEST)
	            .body(new ApiResponse<>(false, "변경 실패: 유효하지 않은 AUC_ID이거나 이미 승인됨", null));
	    }
	}

	@PostMapping(value = "/imgUpload.do, consumes = MediaType.MULTIPART_FORM_DATA_VALUE")
	 public ResponseEntity<?> uploadImage(
			 @ModelAttribute AucPostFile postFile,
			 @RequestPart(value = "files", required = false) List<MultipartFile> files) {
		 log.info("이미지 파일 업로드 요청");
		 
		 HashMap<String, Object> responseMap = new HashMap<>();
		 postFile.setFiles(files);
		 boolean isUploadFile = false;
		 
		 try { 
			 postFile.setBasePath("img");
			 postFile.setCreateId("SYSTEM");
			 
			 HashMap resultMap = (HashMap) fileService.insertBoardFiles(postFile);
			 isUploadFile = (boolean) resultMap.get("result");
			  
			 if(isUploadFile) {
				 responseMap.put("url",apiBaseUrl+"/api/file/imgDown.do?fileId=" + resultMap.get("fileId")); 
			 }
		 } catch (Exception e) {
			 log.error("이미지 파일 업로드 중 오류",e);
		 }
		 
		 return ResponseEntity.ok(new ApiResponse<>(isUploadFile,
				 isUploadFile ? "이미지 파일 업로드 성공" : "이미지 파일 업로드 실패", responseMap));
	 }
	
	
	@GetMapping("/imgDown.do")
	  public void downloadImage(@RequestParam("fileId")String fileId, HttpServletResponse response) {
		  try {
			  AucPostFile file = new AucPostFile();
			  file.setFileId(Integer.parseInt(fileId));
			  AucPostFile selectFile = fileService.getFileByFileId(file);
			  
			  if(selectFile == null) {
				  response.getWriter().write("파일을 찾을 수 없습니다.");
				  return;
			  }
			  
			  File downloadFile = new File("/"+selectFile.getFilePath());
			  if(!downloadFile.exists()) {
				  response.getWriter().write("파일이 존재하지 않습니다.");
				  return;
			  }
			  
			  String mimeType = servletContext.getMimeType(selectFile.getFilePath());
			  if (mimeType == null) mimeType = "application/octet-stream";
			  
			  response.setContentType(mimeType);
			  response.setContentLength((int) downloadFile.length());
			  response.setHeader("Content-Disposition", 
					  "inline; filename=" + URLEncoder.encode(selectFile.getFileName(), "UTF-8"));
		
			  try(
				  FileInputStream fis = new FileInputStream(downloadFile);
				  OutputStream out = response.getOutputStream()
				) {
				  byte[] buffer = new byte[4096];
				  int bytesRead;
				  
				  while((bytesRead = fis.read(buffer)) != -1) {
					  out.write(buffer,0,bytesRead);
				  }
			  }
		  } catch (Exception e) {
			  e.printStackTrace();
			  log.info("이미지를 다운로드 중 오류가 발생했습니다.");
	      } 
	}
 
	
	@PostMapping("/approve.do")
    public ResponseEntity<?> approveAuction(@RequestBody Auction auction) {
        CustomUserDetails userDetails = null;
        try {
            userDetails = (CustomUserDetails) SecurityContextHolder.getContext()
                    .getAuthentication().getPrincipal();
        } catch (Exception e) {
            log.error("인증된 사용자 정보를 가져오는 데 실패했습니다.", e);
            return ResponseEntity.status(401).body(new ApiResponse<>(false, "로그인이 필요합니다.", null));
        }
        log.info(userDetails.toString());
        if (!"Y".equals(userDetails.getAdminYn())) {
            log.warn("관리자 권한 확인 - user: {}, adminYn: {}", userDetails.getUsername(), userDetails.getAdminYn());
            log.warn("권한 없는 사용자가 경매 승인을 시도했습니다. User ID: {}", userDetails.getUsername());
            return ResponseEntity.status(403).body(new ApiResponse<>(false, "관리자만 경매 상품을 승인할 수 있습니다.", null));
        }
String adminId = userDetails.getUsername(); // 승인한 관리자 ID

        // Map에서 aucId 추출
        // requestBody.get("aucId")는 기본적으로 Object로 반환되므로, Integer로 형변환 필요
        String aucId = auction.getAucId();
        if (aucId == null || aucId.isEmpty() || "null".equals(aucId)) { // null 또는 "null" 문자열 체크
            return ResponseEntity.badRequest().body(new ApiResponse<>(false, "경매 ID(aucId)가 누락되었습니다.", null));
        }
        log.info(aucId);
        Auction auctionToUpdate = new Auction();
        auctionToUpdate.setAucId(aucId); // String 타입인 aucId를 그대로 사용
        auctionToUpdate.setAucPermitYn("Y");
        auctionToUpdate.setAucStatus("경매중");
        auctionToUpdate.setUpdateId(adminId);

        // 여기서는 기존 updateAuctionPermitYn 메서드를 사용한다고 가정합니다.
        int updatedRows = auctionService.updateAuctionPermitYn(auctionToUpdate);

        if (updatedRows > 0) {
            log.info("경매 상품 승인 성공. Auc ID: {}, Admin ID: {}", aucId, adminId);
            return ResponseEntity.ok(new ApiResponse<>(true, "경매 상품이 성공적으로 승인되었습니다.", null));
        } else {
            log.warn("경매 상품 승인 실패. Auc ID: {}, Admin ID: {}", aucId, adminId);
            return ResponseEntity.badRequest().body(new ApiResponse<>(false, "경매 상품 승인에 실패했습니다. (상품을 찾을 수 없거나 이미 승인됨)", null));
        }
    }
	
	
 
	
}
