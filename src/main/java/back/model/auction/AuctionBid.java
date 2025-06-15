package back.model.auction;

import java.time.LocalDateTime;
import back.model.Model;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class AuctionBid extends Model {
    private int bidId;
    private String aucId;
    private String userId;
    private int bidPrice;
    private LocalDateTime bidTime;
}
