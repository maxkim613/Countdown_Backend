package back.scheduler;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import back.mapper.auction.AuctionMapper;
import back.model.auction.Auction;
import back.model.auction.AuctionBid;
import back.model.board.Board;
import back.service.auction.AuctionService;
import back.service.board.BoardService;
import back.service.msg.MsgService;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class AuctionScheduler {

	 
	// ┌──────────── 초 (0-59)
	// │ ┌────────── 분 (0-59)
	// │ │ ┌──────── 시 (0-23)
	// │ │ │ ┌────── 일 (1-31)
	// │ │ │ │ ┌──── 월 (1-12)
	// │ │ │ │ │ ┌── 요일 (0-7) (0 또는 7 = 일요일)
	// │ │ │ │ │ │
	// 매 분 3초마다 실행
	// "3 * * * * *"
	// 매 10초마다 실행
	// "*/10 * * * * *"
	// 매 시간 0분 0초 (정시)에 실행
	// "0 0 * * * *"
	// 매일 자정 0시 0분 0초에 실행
	// "0 0 0 * * *"
	// 매일 오전 9시 30분에 실행
	// "0 30 9 * * *"
	// 매주 월요일 오전 10시에 실행
	// "0 0 10 * * MON"
	// 평일(월~금) 오전 9시 ~ 오후 6시 사이 매 시 정각 실행
	// "0 0 9-18 * * MON-FRI"
	// 매달 1일 자정에 실행
	// "0 0 0 1 * *"
	// 매년 1월 1일 자정에 실행
	// "0 0 0 1 1 *"
 
    
    @Autowired
    private AuctionMapper auctionMapper;
    
    @Autowired
    private AuctionService auctionService;
    
    @Autowired
    private MsgService msgService;

    
    @Scheduled(cron = "0 51 14 * * *") // 매일 10시에 실행
    public void startScheduledAuctions() {
        List<Auction> list = auctionMapper.getAuctionsToStart();
        for (Auction auction : list) {
            auctionMapper.updateStatusToAuctioning(auction.getAucId());
            log.info("경매 시작됨: {}", auction.getAucId());
        }
    }
    
    //유찰
    @Scheduled(cron = "0/20 * * * * *") 
    public void closeAuctionsWithoutBids() {
        List<Auction> list = auctionMapper.getAuctionsToCloseNoBid();
        for (Auction auction : list) {
            auctionMapper.closeAuction(auction.getAucId());
            String aucId = auction.getAucId();
            msgService.sendAuctionApprovedMessage(aucId);
            log.info("입찰 없는 경매 자동 종료: {}", auction.getAucId());
        }
    }
    
    //낙찰 시간 완전히 끝났을 떄(1시간마다 입찰이 계속 된 상태)
    @Scheduled(cron = "30 51 14 * * *") // 매일 오후 3시 10분에 실행
    public void completeAuctions() {
        List<Auction> list = auctionMapper.getAuctionsToCloseByDeadline(); // 마감된 경매 목록

        for (Auction auction : list) {
            String aucId = auction.getAucId();

            // 최고 입찰자 조회
            AuctionBid topBid = auctionMapper.getTopBid(aucId); // AuctionBid 전체 받기 (이 쿼리 필요)

            if (topBid != null) {
                Map<String, Object> param = new HashMap<>();
                param.put("aucId", aucId);
                param.put("winnerId", topBid.getUserId());
                auctionMapper.updateStatusToCompleted(param);
                msgService.sendAuctionFinishedMessage(aucId);
                log.info("경매 낙찰 완료: {}, 낙찰자: {}", aucId, topBid.getUserId());
            } else {
                auctionMapper.closeAuction(aucId);
                log.info("입찰 없음 - 경매 유찰 처리: {}", aucId);
            }
        }
    }

    
    //1시간 경매없는거 삭제
    @Scheduled(cron = "0 0 15 * * *")
    public void closeInactiveAuctions() {
        int closedCount = auctionMapper.updateAuctionsInactiveForAnHour();
        log.info("1시간 이상 입찰 없는 경매 종료: {}건", closedCount);
    }
    
    
}
