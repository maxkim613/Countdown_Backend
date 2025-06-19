package back.service.auction;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import back.dto.AuctionBidRequest;
import back.exception.HException;
import back.mapper.auction.AuctionBidMapper;
import back.mapper.auction.AuctionMapper;
import back.mapper.file.AucFileMapper;
import back.model.auction.Auction;
import back.model.auction.AuctionBid;
import back.model.common.AucPostFile;
import back.util.FileUploadUtil;
import lombok.extern.slf4j.Slf4j; 

@Service
@Slf4j
public class AuctionServiceImpl implements AuctionService {
    @Autowired
	private AuctionMapper auctionMapper;
    @Autowired
    private AucFileMapper fileMapper;
    
    @Autowired
    private AuctionBidMapper auctionBidMapper;

    
    public List<Auction> getAuctionBoardList(Auction auction) {
 
    	List list = auctionMapper.getAuctionBoardList(auction);
    	return list;
    }
    
    
    @Override
    public Auction getAuctionById(String aucId) {
        try {
            log.info("경매 상세 조회 - auctionId: {}", aucId);

            Auction auction = auctionMapper.getAuctionById(aucId); // 게시글 기본 정보 조회

            if (auction != null) {
                List<AuctionBid> bidList = auctionMapper.getBidList(aucId);
                auction.setBidList(bidList);  // 반드시 이렇게 세팅해줘야 함
            }
            // 파일 목록 조회
            List<AucPostFile> files = fileMapper.getFilesByBoardId(aucId);
            auction.setPostFiles(files);

            // 대표 이미지 URL 설정
            for (AucPostFile file : files) {
                if ("Y".equalsIgnoreCase(file.getIsMain())) {
                    auction.setFileId(String.valueOf(file.getFileId())); // filePath만 사용
                    break;
                }
            }

            return auction;
        } catch (Exception e) {
            log.error("게시글 조회 실패", e);
            throw new HException("게시글 조회 실패", e);
        }
    }
    
    @Override
    public List<AuctionBid> getBidList(String aucId) {
        return auctionMapper.getBidList(aucId);
    }
    // 새 게시글 생성
    @Override
    @Transactional
    public boolean createaucBoard(Auction auction) throws NumberFormatException, IOException {
       
        boolean result = auctionMapper.aucCreate(auction) >0; // 게시글 생성
        List<MultipartFile> files = auction.getFiles();
        if (result && files != null) {
        	 List<AucPostFile> fileList = FileUploadUtil.aucuploadFiles(files,"auction",
                     Integer.parseInt(auction.getAucId()), auction.getCreateId(),0);
        	 for (AucPostFile postFile : fileList) {
             	boolean insertResult = fileMapper.insertFile(postFile) > 0;
             	log.info(postFile.toString());
             	if (!insertResult) throw new HException("파일 추가 실패");
             }
        }

        return result;
        
    }
    
 // 기존 게시글 수정
    @Transactional
    public boolean updateaucBoard(Auction auction) throws NumberFormatException, IOException {

            boolean result = auctionMapper.aucUpdate(auction) >0; // 게시글 수정
            
//            if(result) {
//            	List<MultipartFile> files = auction.getFiles();
//                String remainingFileIds = auction.getRemainingFileIds();
//                
//                List<PostFile> existingFiles = fileMapper.getFilesByBoardId(auction.getAucId());
//
//                    for (PostFile existing : existingFiles) {
//                    	if (!remainingFileIds.contains(String.valueOf(existing.getFileId()))) {
//                    		existing.setUpdateId(auction.getUpdateId());
//                    		boolean deleteResult = fileMapper.deleteFile(existing) >0;
//                    		if(!deleteResult) throw new HException("파일 삭제 실패");
//                    	}
//                    }   
//                    if(files != null) {
//                    	 List<PostFile> uploadedFiles = FileUploadUtil.uploadFiles(files, "board",
//                    			 Integer.parseInt(auction.getAucId()),auction.getUpdateId());
//                    	 for (PostFile postFile : uploadedFiles) {
//                    		 boolean inserResult = fileMapper.insertFile(postFile) > 0;
//                    		 if(!inserResult) throw new HException("파일 추가 실패");
//                    	 }
//                    }
//                }
            return result;

    }
    
    @Override
	@Transactional
    public boolean deleteaucBoard(Auction auction) {
    	
    	return auctionMapper.aucDelete(auction) >0;
    
    }
	@Override
	public List getMyAuctionBoardList(Auction auction) {
		List list = auctionMapper.getMyAuctionBoardList(auction);
    	return list;
	}
    
	@Transactional
    public boolean placeBid(AuctionBidRequest bidRequest) {
        String aucId = bidRequest.getAucId();
        int bidPrice = bidRequest.getBidPrice();
        String userId = bidRequest.getUserId();

        // 1. 경매 정보 조회
        Auction auction = auctionMapper.getAuctionById(aucId);
        if (auction == null) {
            throw new HException("존재하지 않는 경매입니다.");
        }
        if ("판매완료".equalsIgnoreCase(auction.getAucStatus())) {
            throw new HException("종료된 경매입니다.");
        }
        if (auction.getAucDeadline().compareTo(LocalDateTime.now().toString()) < 0) {
            throw new HException("경매 기간이 종료되었습니다.");
        }

        // 2. 입찰 가격 체크
        int currentPrice = Integer.parseInt(auction.getAucCprice());
        if (bidPrice <= currentPrice) {
            throw new HException("입찰 가격은 현재가보다 높아야 합니다.");
        }

        // 3. 입찰 내역 저장
        AuctionBid bid = new AuctionBid();
        bid.setAucId(aucId);
        bid.setUserId(userId);
        bid.setBidPrice(bidPrice);
        bid.setBidTime(LocalDateTime.now());

        boolean bidSaved = auctionBidMapper.insertBid(bid) > 0;

        if (!bidSaved) {
            throw new HException("입찰 등록 실패");
        }

        // 4. 현재가 업데이트 및 입찰 횟수 증가
        auction.setAucCprice(String.valueOf(bidPrice));
        auction.setAucBidCount(auction.getAucBidCount() + 1);

        boolean auctionUpdated = auctionMapper.aucBidUpdate(auction) > 0;

        if (!auctionUpdated) {
            throw new HException("현재가 업데이트 실패");
        }

        return true;
    }
	
	@Transactional
	public boolean buyNow(String aucId, String userId) {
	    Auction auction = auctionMapper.getAuctionById(aucId);
	    if (auction == null) {
	        throw new HException("존재하지 않는 경매입니다.");
	    }
	    if ("경매종료".equalsIgnoreCase(auction.getAucStatus())) {
	        throw new HException("이미 경매가 종료된 상품입니다.");
	    }
	    if (auction.getAucDeadline().compareTo(LocalDateTime.now().toString()) < 0) {
	        throw new HException("경매 기간이 종료되었습니다.");
	    }

	    int buyNowPrice = Integer.parseInt(auction.getAucBprice());
	    if (buyNowPrice <= 0) {
	        throw new HException("즉시 구매 가격이 설정되어 있지 않습니다.");
	    }

	    auction.setAucStatus("경매종료");
	    auction.setAucBuyerId(userId);
	    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
	    String formattedNow = LocalDateTime.now().format(formatter);
	    auction.setAucBuyTime(formattedNow);
	    auction.setAucCprice(String.valueOf(buyNowPrice));

	    boolean updated = auctionMapper.aucBuyNowUpdate(auction) > 0;
	    if (!updated) {
	        throw new HException("즉시 구매 처리 실패");
	    }
	    return true;
	}
	
	 @Override
	    public String getLikeStatus(String aucId, String userId) {
	        String status = auctionMapper.getLikeStatus(aucId, userId);
	        return status != null ? status : "N";  // 없으면 'N'으로 처리
	    }

	    // 좋아요 토글
	    @Override
	    @Transactional
	    public boolean toggleLike(String aucId, String userId) {
	        String currentStatus = auctionMapper.getLikeStatus(aucId, userId);
	        if (currentStatus == null) {
	            // 좋아요 기록 없으면 새로 insert
	            return auctionMapper.insertLike(aucId, userId, "Y") > 0;
	        } else if ("Y".equals(currentStatus)) {
	            // 좋아요 되어 있으면 취소로 변경
	            return auctionMapper.updateLike(aucId, userId, "N") > 0;
	        } else {
	            // 좋아요 취소 상태면 다시 좋아요로 변경
	            return auctionMapper.updateLike(aucId, userId, "Y") > 0;
	        }
	    }
	    
	    @Override
	    public List<Auction> getInProgressByBuyer(String userId) {
	    	 
	    	List list = auctionMapper.getInProgressByBuyer(userId);
	    	return list;
	    }
	    
	    @Override
	    public List<Auction> getInProgressByCreator(String userId) {
	    	 
	    	List list = auctionMapper.getInProgressByCreator(userId);
	    	return list;
	    }
	    
	    @Override
	    public List<Auction> getCompletedByCreator(String userId) {
	    	
	        return auctionMapper.getCompletedByCreator(userId);
	    }
	    
	    @Override
	    public List<Auction> getWaitingAuctionList(Auction auction) {
	    	 
	    	List list = auctionMapper.getWaitingAuctionList(auction);
	    	return list;
	    }
	    
	    @Override	    
	    public List<Auction> getCompletedByMe(Auction auction) {
	    	 
	    	List list = auctionMapper.getCompletedByMe(auction);
	    	return list;
	    }
	    
	    @Override
		public List<Auction> getLikedAuctions(String userId) {
			
			return auctionMapper.getLikedAuctions(userId);
		}
	    
	    @Override
	    public void startScheduledAuctions() {
	        List<Auction> list = auctionMapper.getAuctionsToStart();
	        for (Auction auction : list) {
	            auctionMapper.updateStatusToAuctioning(auction.getAucId());
	            log.info("경매 시작됨: " + auction.getAucId());
	        }
	    }
	    
	    @Override
		public int updateStatusToInProgress(Auction auction) {
			auction.setAucPermitYn("Y"); // aucPermitYn을 'Y'로 강제 설정
	        auction.setAucStatus("경매중"); // aucStatus를 '경매중'으로 강제 설정
			return auctionMapper.updateStatusToInProgress(auction);
		}
	    
	    @Transactional
	    @Override
	    public void closeAuctionsWithoutBids() {
	        List<Auction> list = auctionMapper.getAuctionsToCloseNoBid();
	        for (Auction auction : list) {
	            auctionMapper.closeAuction(auction.getAucId());
	            log.info("입찰 없음 자동 종료 처리됨: {}", auction.getAucId());
	        }
	    }
	    
	    @Transactional
	    public void closeAuctionsEndedToday() {
	        auctionMapper.updateAuctionsToClosed();
	    }


		@Override
		public int updateAuctionPermitYn(Auction auction) {
			return auctionMapper.updateAuctionPermitYn(auction);
		}


		@Override
		public List<Auction> getWaitingMyAuctionList(String userId) {
			// TODO Auto-generated method stub
			return null;
		}


		@Override
		public List<Auction> getCompletedByCreator(Auction autcion) {
			// TODO Auto-generated method stub
			return null;
		}

}
