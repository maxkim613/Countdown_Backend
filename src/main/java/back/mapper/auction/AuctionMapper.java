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
	    
	    public List<Auction> getInProgressByBuyer(Auction auction);
	    
	    public List<Auction> getInProgressByCreator(Auction auction);
	    
	    public List<Auction> getCompletedByCreator(Auction auction);
	    
	    public List<Auction> getWaitingAuctionList(Auction auction);
	    
	    public List<Auction> getCompletedByMe(Auction auction);
	    
	    public int updateStatusToAuctioning(String aucId);
	     
	    public List<Auction> getAuctionsToStart();
	    
	    List<Auction> getAuctionsToCloseNoBid();
	    
	    int closeAuction(String aucId);
	    

}
