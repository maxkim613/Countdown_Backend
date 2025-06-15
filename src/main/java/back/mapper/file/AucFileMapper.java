package back.mapper.file;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import back.model.common.AucPostFile;
import back.model.common.PostFile;

@Mapper
public interface AucFileMapper {
	public int insertFile(AucPostFile file);
    
    // 게시글 ID와 파일 ID로 첨부된 파일 조회
    public AucPostFile getFileByFileId(AucPostFile file);
    
    // 게시글 ID로 첨부된 파일 목록 조회
    public List<AucPostFile> getFilesByBoardId(String aucId);

    public int deleteFile(AucPostFile file);
	
}

