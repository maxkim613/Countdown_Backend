package back.mapper.auction;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import back.model.auction.Auction;
import back.model.auction.AuctionBid;

@Mapper	
public interface AuctionMapper {
	 	public List<Auction> getAuctionBoardList(Auction auction);
	 	
	 	public List<Auction> getMyAuctionBoardList(Auction auction);

	    public Auction getAuctionById(String auctionId);

	    public int aucCreate(Auction auction);

	    public int aucUpdate(Auction auction);

	    public int aucDelete(Auction auction);
	    
	    public int aucBuyNowUpdate(Auction auction);
	    
	    public int aucBidUpdate(Auction auction);
	    
	    String getLikeStatus(@Param("aucId") String aucId,
                @Param("userId") String userId);

	    int insertLike(@Param("aucId") String aucId,
	               @Param("userId") String userId,
	               @Param("toggleYn") String toggleYn);

		int updateLike(@Param("aucId") String aucId,
		               @Param("userId") String userId,
		               @Param("toggleYn") String toggleYn);
		    
	    public List<AuctionBid> getBidList(String aucId);
	    
	    public List<Auction> getInProgressByBuyer(String userId);
	    
	    public List<Auction> getInProgressByCreator(String userId);
	    
	    public List<Auction> getCompletedByCreator(String userId);
	    
	    public List<Auction> getWaitingAuctionList(Auction auction);
	    
	    public List<Auction> getCompletedByMe(Auction auction);
	    
	    public int updateStatusToAuctioning(String aucId);
	     
	    public List<Auction> getAuctionsToStart();
	    
	    public int updateStatusToInProgress(Auction auction);
	    
	    public int updateAuctionPermitYn(Auction auction);

	    public List<Auction> getAuctionsToCloseNoBid();
	    
	    public int closeAuction(String aucId);

	    
	    public int closeTodayAuctions();
	    
	    public int updateAuctionsToClosed(); 
	    
	    public int updateAuctionsInactiveForAnHour();
	    
	    public List<Auction> getLikedAuctions(String userId);   

}
