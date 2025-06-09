package back.mapper.auction;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import back.model.auction.Auction;

@Mapper	
public interface AuctionMapper {
	 	public List<Auction> getAuctionBoardList(Auction auction);
	 	
	 	public List<Auction> getMyAuctionBoardList(Auction auction);

	    public Auction getAuctionById(String auctionId);

	    public int aucCreate(Auction auction);

	    public int aucUpdate(Auction auction);

	    public int aucDelete(Auction auction);

}
