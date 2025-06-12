package back.service.announcement;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import back.exception.HException;
import back.mapper.announcement.AnnouncementMapper;
import back.mapper.board.BoardMapper;
import back.model.announcement.Announcement;
import back.model.board.Board;
import lombok.extern.slf4j.Slf4j; 

@Service
@Slf4j
public class AnnouncementServiceImpl implements AnnouncementService {
	
	@Autowired
    private AnnouncementMapper announcementMapper;

    @Override
    public Announcement getAnnouncementById(long annId) {
        try {
        	Announcement announcement=announcementMapper.getAnnouncementById(annId);
            return announcement;
        } catch (Exception e) {
            log.error("공지사항 조회 실패", e);
            throw new HException("공지사항 조회 실패", e);
        }
    }

    @Override
    @Transactional
    public List<Announcement> getAnnouncementList(Announcement announcement) {
        int page = announcement.getPage();
        int size = announcement.getSize();

        int totalCount = announcementMapper.getTotalAnnouncementCount(announcement);
        int totalPages = (int) Math.ceil((double) totalCount / size);

        int startRow = (page - 1) * size + 1;
        int endRow = page * size;

        announcement.setTotalCount(totalCount);
        announcement.setTotalPages(totalPages);
        announcement.setStartRow(startRow);
        announcement.setEndRow(endRow);

        return announcementMapper.getAnnouncementList(announcement);
    }

    @Override
    @Transactional
    public boolean createAnnouncement(Announcement announcement) {
        try {
        	boolean result = announcementMapper.create(announcement) > 0;
            return result;
        } catch (Exception e) {
            log.error("공지사항 등록 실패", e);
            throw new HException("공지사항 등록 실패", e);
        }
    }

    @Override
    @Transactional
    public boolean updateAnnouncement(Announcement announcement) {
        try {
            return announcementMapper.update(announcement) > 0;
        } catch (Exception e) {
            log.error("공지사항 수정 실패", e);
            throw new HException("공지사항 수정 실패", e);
        }
    }

    @Override
    @Transactional
    public boolean deleteAnnouncement(Announcement announcement) {
        try {
            return announcementMapper.delete(announcement) > 0;
        } catch (Exception e) {
            log.error("공지사항 삭제 실패", e);
            throw new HException("공지사항 삭제 실패", e);
        }
    }

}
