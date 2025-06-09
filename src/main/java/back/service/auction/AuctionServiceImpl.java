package back.service.auction;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import back.exception.HException;
import back.mapper.auction.AuctionMapper;
import back.mapper.file.FileMapper;
import back.model.auction.Auction;
import back.model.common.PostFile;
import back.util.FileUploadUtil;
import lombok.extern.slf4j.Slf4j; 

@Service
@Slf4j
public class AuctionServiceImpl implements AuctionService {
    @Autowired
	private AuctionMapper auctionMapper;
    @Autowired
    private FileMapper fileMapper;

    
    public List<Auction> getAuctionBoardList(Auction auction) {
 
    	List list = auctionMapper.getAuctionBoardList(auction);
    	return list;
    }
    @Override
    public Auction getAuctionById(String auctionId) {
        try {
        	Auction auction = auctionMapper.getAuctionById(auctionId); // 게시글 기본 정보 조회
        	auction.setPostFiles(fileMapper.getFilesByBoardId(auctionId)); 
            return auction;
        } catch (Exception e) {
        	log.error("게시글 조회 실패",e);
        	throw new HException("게시글 조회 실패",e);
        } 
    }
    
    // 새 게시글 생성
    @Override
    @Transactional
    public boolean createaucBoard(Auction auction) throws NumberFormatException, IOException {
       
        boolean result = auctionMapper.aucCreate(auction) >0; // 게시글 생성
        List<MultipartFile> files = auction.getFiles();
        if (result && files != null) {
        	 List<PostFile> fileList = FileUploadUtil.uploadFiles(files,"board",
                     Integer.parseInt(auction.getAuctionId()), auction.getCreateId());
        	 for (PostFile postFile : fileList) {
             	boolean insertResult = fileMapper.insertFile(postFile) > 0;
             	if (!insertResult) throw new HException("파일 추가 실패");
             }
        }

        return result;
        
    }
    
 // 기존 게시글 수정
    @Transactional
    public boolean updateaucBoard(Auction auction) throws NumberFormatException, IOException {

            boolean result = auctionMapper.aucUpdate(auction) >0; // 게시글 수정
            
            if(result) {
            	List<MultipartFile> files = auction.getFiles();
                String remainingFileIds = auction.getRemainingFileIds();
                
                List<PostFile> existingFiles = fileMapper.getFilesByBoardId(auction.getAuctionId());

                    for (PostFile existing : existingFiles) {
                    	if (!remainingFileIds.contains(String.valueOf(existing.getFileId()))) {
                    		existing.setUpdateId(auction.getUpdateId());
                    		boolean deleteResult = fileMapper.deleteFile(existing) >0;
                    		if(!deleteResult) throw new HException("파일 삭제 실패");
                    	}
                    }   
                    if(files != null) {
                    	 List<PostFile> uploadedFiles = FileUploadUtil.uploadFiles(files, "board",
                    			 Integer.parseInt(auction.getAuctionId()),auction.getUpdateId());
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
    public boolean deleteaucBoard(Auction auction) {
    	
    	return auctionMapper.aucDelete(auction) >0;
    
    }
	@Override
	public List getMyAuctionBoardList(Auction auction) {
		List list = auctionMapper.getMyAuctionBoardList(auction);
    	return list;
	}
    

 

}
