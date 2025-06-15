package back.dto;

import lombok.Data;

@Data
public class AuctionBidRequest {
    private String aucId;
    private String userId;
    private int bidPrice;
}
