package back.mapper.auction;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

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
	    
	    String getLikeStatus(String aucId, String userId);

	    int insertLike(String aucId, String userId, String toggleYn);

	    int updateLike(String aucId, String userId, String toggleYn);
	    
	    public List<AuctionBid> getBidList(String aucId);
	    
	    public List<Auction> getInProgressByBuyer(Auction auction);
	    
	    public List<Auction> getInProgressByCreator(Auction auction);
	    
	    public List<Auction> getCompletedByCreator(Auction auction);
	    
	    public List<Auction> getWaitingAuctionList(Auction auction);
	    
	    public List<Auction> getCompletedByMe(Auction auction);
	    
	    public int updateStatusToInProgress(Auction auction);
	    
	    
	    

}
