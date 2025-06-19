package back.service.auction;

import java.io.IOException;
import java.util.List;

import back.dto.AuctionBidRequest;
import back.model.auction.Auction;
import back.model.board.Board;
import back.model.board.Comment;

public interface AuctionService {
	public List getAuctionBoardList(Auction auction);
	
	public List getMyAuctionBoardList(Auction auction);
	
    public Auction getAuctionById(String auctionId);
    
    public boolean createaucBoard(Auction auction)throws NumberFormatException, IOException;
    
    public boolean updateaucBoard(Auction auction)throws NumberFormatException, IOException;
    
    public boolean deleteaucBoard(Auction auction);
    
    boolean placeBid(AuctionBidRequest bidRequest);
    
    public boolean buyNow(String aucId, String userId);
    
    public String getLikeStatus(String aucId, String userId);

    public boolean toggleLike(String aucId, String userId);
    
    public List getBidList(String aucId);
    
    public List getInProgressByBuyer(Auction auction);
    
    public List getInProgressByCreator(Auction auction);
    
    public List getCompletedByCreator(Auction auction);
    
    public List getWaitingAuctionList(Auction auction);
    
    public int updateStatusToInProgress(Auction auction);
     
    
    public List getCompletedByMe(Auction auction);
    
    public void startScheduledAuctions();
    
    public void closeAuctionsWithoutBids();

    public void closeAuctionsEndedToday();
    
    public int updateAuctionPermitYn(Auction auction);
    
    
}
