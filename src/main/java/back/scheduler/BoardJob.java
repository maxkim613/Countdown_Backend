package back.scheduler;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import back.model.board.Board;
import back.service.board.BoardService;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class BoardJob {

	 
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
    private BoardService boardService;

    @Scheduled(cron = "0 0/3 * * * *")
    public void runBatchJob() {
        log.info("배치 시작: " + LocalDateTime.now());

        try {
            boardService.getBoardList(new Board());
        } catch (Exception e) {
            log.error("배치 작업 중 오류 발생", e);
        }

        log.info("배치 종료: " + LocalDateTime.now());
    }
}
