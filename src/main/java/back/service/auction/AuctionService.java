package back.service.auction;

import java.io.IOException;
import java.util.List;

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
    
   
    
}
