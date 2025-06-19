package back.scheduler;

import back.mapper.batch.MsgBatchMapper;
import back.mapper.msg.MsgMapper;
import back.model.vo.MsgBatchVO;
import back.model.vo.MsgVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class PushNotificationScheduler {
    // 이전과 동일한 코드
    private final MsgMapper msgMapper;
    private final MsgBatchMapper msgBatchMapper;
    
    private static final DateTimeFormatter BATCH_TIMESTAMP_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    
    @Scheduled(cron = "0 * * * * *")
    public void processPushNotifications() {
        log.info("푸시 알림 배치 작업 시작");
        List<MsgVO> targets = msgMapper.selectPushTargets();

        if (targets.isEmpty()) {
            log.info("새로운 푸시 알림 대상이 없습니다. 배치 작업을 종료합니다.");
            return;
        }
        MsgBatchVO batchLog = startBatchLog("PushNotificationJob", targets.size());
        int successCount = 0;
        try {
            for (MsgVO msg : targets) {
                try {
                    log.info("푸시 알림 발송 시도: receiver={}, msgId={}", msg.getReceiverId(), msg.getMsgId());
                    updatePushStatus(msg.getMsgId());
                    successCount++;
                } catch (Exception e) {
                    log.error("푸시 알림 발송 실패: msgId={}, error={}", msg.getMsgId(), e.getMessage());
                }
            }
            finishBatchLog(batchLog, "COMPLETED", successCount + "건 처리 완료");
        } catch (Exception e) {
            log.error("푸시 알림 배치 작업 중 심각한 오류 발생", e);
            finishBatchLog(batchLog, "FAILED", e.getMessage());
        }
    }
    
    @Transactional
    public void updatePushStatus(int msgId) { msgMapper.updatePushStatus(msgId); }
    
    private MsgBatchVO startBatchLog(String jobName, int total) {
        MsgBatchVO logVO = new MsgBatchVO();
        logVO.setMsgJobName(jobName + " (" + total + "건)");
        logVO.setStatus("RUNNING");
        logVO.setStartTime(LocalDateTime.now().format(BATCH_TIMESTAMP_FORMATTER));
        logVO.setCreateId("SYSTEM");
        logVO.setUpdateId("SYSTEM");
        msgBatchMapper.insertBatchLog(logVO);
        return logVO;
    }
    
    private void finishBatchLog(MsgBatchVO logVO, String status, String message) {
        logVO.setStatus(status);
        logVO.setEndTime(LocalDateTime.now().format(BATCH_TIMESTAMP_FORMATTER));
        logVO.setExitCode(status.equals("COMPLETED") ? "0" : "-1");
        logVO.setExitMsg(message.length() > 4000 ? message.substring(0, 4000) : message);
        msgBatchMapper.updateBatchLog(logVO);
    }
}