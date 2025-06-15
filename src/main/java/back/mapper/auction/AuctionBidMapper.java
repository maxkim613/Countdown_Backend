package back.mapper.auction;

import org.apache.ibatis.annotations.Mapper;
import back.model.auction.AuctionBid;

@Mapper
public interface AuctionBidMapper {
    int insertBid(AuctionBid bid);
}
