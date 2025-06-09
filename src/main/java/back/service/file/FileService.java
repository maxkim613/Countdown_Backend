package back.service.file;

import java.util.Map;

import back.model.common.PostFile;


public interface FileService {
	public PostFile getFileByFileId(PostFile file);
    
	 public Map<String, Object> insertBoardFiles(PostFile file);
	 
	 
}
