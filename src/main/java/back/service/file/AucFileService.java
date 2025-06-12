package back.service.file;

import java.util.Map;

import back.model.common.AucPostFile;


public interface AucFileService {
	public AucPostFile getFileByFileId(AucPostFile file);
    
	 public Map<String, Object> insertBoardFiles(AucPostFile file);
	 
	 
}
