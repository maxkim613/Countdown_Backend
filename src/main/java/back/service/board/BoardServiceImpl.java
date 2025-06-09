package back.service.board;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import back.exception.HException;
import back.mapper.board.BoardMapper;
import back.mapper.file.FileMapper;
import back.model.board.Board;
import back.model.board.Comment;
import back.model.common.PostFile;
import back.util.FileUploadUtil;
import lombok.extern.slf4j.Slf4j; 

@Service
@Slf4j
public class BoardServiceImpl implements BoardService {
    @Autowired
	private BoardMapper boardMapper;
    @Autowired
    private FileMapper fileMapper;

    
    public List<Board> getBoardList(Board board) {
 
    	
    	int page = board.getPage();
    	int size = board.getSize();
    	
    	int totalCount = boardMapper.getTotalBoardCount(board);
    	int totalPages = (int) Math.ceil((double) totalCount/size);
    	
    	int startRow = (page - 1)*size + 1;
    	int endRow = page*size;
    	
    	board.setTotalCount(totalCount);
    	board.setTotalPages(totalPages);
    	board.setStartRow(startRow);
    	board.setEndRow(endRow);
    	
    	List list = boardMapper.getBoardList(board);
    	return list;
    }
    @Override
    public Board getBoardById(String boardId) {
        try {
        	Board board = boardMapper.getBoardById(boardId); // 게시글 기본 정보 조회
            board.setPostFiles(fileMapper.getFilesByBoardId(boardId)); 
            board.setComments(boardMapper.getCommentsByBoardId(boardId));
            return board;
        } catch (Exception e) {
        	log.error("게시글 조회 실패",e);
        	throw new HException("게시글 조회 실패",e);
        } 
    }
    
    // 새 게시글 생성
    @Override
    @Transactional
    public boolean createBoard(Board board) throws NumberFormatException, IOException {
       
        boolean result = boardMapper.create(board) >0; // 게시글 생성
        List<MultipartFile> files = board.getFiles();
        if (result && files != null) {
        	 List<PostFile> fileList = FileUploadUtil.uploadFiles(files,"board",
                     Integer.parseInt(board.getBoardId()), board.getCreateId());
        	 for (PostFile postFile : fileList) {
             	boolean insertResult = fileMapper.insertFile(postFile) > 0;
             	if (!insertResult) throw new HException("파일 추가 실패");
             }
        }

        return result;
        
    }
    
 // 기존 게시글 수정
    @Transactional
    public boolean updateBoard(Board board) throws NumberFormatException, IOException {

            boolean result = boardMapper.update(board) >0; // 게시글 수정
            
            if(result) {
            	List<MultipartFile> files = board.getFiles();
                String remainingFileIds = board.getRemainingFileIds();
                
                List<PostFile> existingFiles = fileMapper.getFilesByBoardId(board.getBoardId());

                    for (PostFile existing : existingFiles) {
                    	if (!remainingFileIds.contains(String.valueOf(existing.getFileId()))) {
                    		existing.setUpdateId(board.getUpdateId());
                    		boolean deleteResult = fileMapper.deleteFile(existing) >0;
                    		if(!deleteResult) throw new HException("파일 삭제 실패");
                    	}
                    }   
                    if(files != null) {
                    	 List<PostFile> uploadedFiles = FileUploadUtil.uploadFiles(files, "board",
                    			 Integer.parseInt(board.getBoardId()),board.getUpdateId());
                    	 for (PostFile postFile : uploadedFiles) {
                    		 boolean inserResult = fileMapper.insertFile(postFile) > 0;
                    		 if(!inserResult) throw new HException("파일 추가 실패");
                    	 }
                    }
                }
            return result;

    }
    
    @Override
	@Transactional
    public boolean deleteBoard(Board board) {
    	
    	return boardMapper.delete(board) >0;
    
    }
    

    @Override
	@Transactional
	public boolean createComment(Comment comment) {
    	return boardMapper.insertComment(comment) > 0;
	}

	@Override
	@Transactional
	public boolean updateComment(Comment comment) {
    
        return boardMapper.updateComment(comment) > 0;
	}

	@Override
	@Transactional
	public boolean deleteComment(Comment comment) {
    	
		return	boardMapper.deleteComment(comment) > 0;
    	
	}

}
