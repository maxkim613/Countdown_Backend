package back.mapper.file;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import back.model.common.PostFile;

@Mapper
public interface FileMapper {
	public int insertFile(PostFile file);
    
    // 게시글 ID와 파일 ID로 첨부된 파일 조회
    public PostFile getFileByFileId(PostFile file);
    
    // 게시글 ID로 첨부된 파일 목록 조회
    public List<PostFile> getFilesByBoardId(String boardId);

    public int deleteFile(PostFile file);
	
}

