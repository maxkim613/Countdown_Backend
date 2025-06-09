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
import back.mapper.file.FileMapper;
import back.model.announcement.Announcement;
import back.model.board.Board;
import back.model.board.Comment;
import back.model.common.PostFile;
import back.util.FileUploadUtil;
import lombok.extern.slf4j.Slf4j; 

@Service
@Slf4j
public class AnnouncementServiceImpl implements AnnouncementService {
    @Autowired
	private AnnouncementMapper announcementMapper;

    
    public List<Announcement> getAnnouncementBoardList(Announcement announcement) {
    	
    	List list = announcementMapper.getAnnouncementBoardList(announcement);
    	return list;
    }
    
    @Override
    public Announcement getAnnouncementById(String announcementId) {
        try {
        	Announcement announcement = announcementMapper.getAnnouncementById(announcementId); // 게시글 기본 정보 조회
            return announcement;
        } catch (Exception e) {
        	log.error("공지사항 조회 실패",e);
        	throw new HException("공지사항 조회 실패",e);
        } 
    }
    
    // 새 게시글 생성
    @Override
    @Transactional
    public boolean createannBoard(Announcement announcement) throws NumberFormatException, IOException {
       
        boolean result = announcementMapper.annCreate(announcement) >0; // 게시글 생성
       
        return result;
        
    }
    
 // 기존 게시글 수정
    @Transactional
    public boolean updateannBoard(Announcement announcement) throws NumberFormatException, IOException {

            boolean result = announcementMapper.annUpdate(announcement) >0; // 게시글 수정
            
            return result;

    }
    
    @Override
	@Transactional
    public boolean deleteannBoard(Announcement announcement) {
    	
    	return announcementMapper.annDelete(announcement) >0;
    
    }
    

//    @Override
//	@Transactional
//	public boolean createComment(Comment comment) {
//    	return boardMapper.insertComment(comment) > 0;
//	}
//
//	@Override
//	@Transactional
//	public boolean updateComment(Comment comment) {
//    
//        return boardMapper.updateComment(comment) > 0;
//	}
//
//	@Override
//	@Transactional
//	public boolean deleteComment(Comment comment) {
//    	
//		return	boardMapper.deleteComment(comment) > 0;
//    	
//	}

}
