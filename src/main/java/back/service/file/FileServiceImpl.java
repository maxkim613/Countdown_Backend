package back.service.file;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import back.mapper.file.FileMapper;
import back.model.common.PostFile;
import back.service.user.UserServiceImpl;
import back.util.FileUploadUtil;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class FileServiceImpl implements FileService{
	
	private FileMapper fileMapper;
	
	public PostFile getFileByFileId(PostFile file) { 
    	PostFile PostFile = fileMapper.getFileByFileId(file);
		return PostFile;
	}
	
	@Transactional
	public Map<String, Object> insertBoardFiles(PostFile file) { 
		Map<String, Object> resultMap = new HashMap<String, Object>();
		try {
		int boardId = file.getBoardId();
		String userId = file.getCreateId();
		String basePath = file.getBasePath();
		
		List<MultipartFile> files = file.getFiles();
		
		if(files == null || files.isEmpty()) {
			resultMap.put("result",false);
			resultMap.put("message","파일이 존재하지 않습니다.");
			return resultMap;
		}
		
		List<PostFile> uploadedFiles = FileUploadUtil.uploadFiles(files, basePath, boardId, userId);
		
			for (PostFile postFile : uploadedFiles) {
				fileMapper.insertFile(postFile);
			}
			resultMap.put("result",false);
			
			if(uploadedFiles != null && uploadedFiles.size() > 0) {
				resultMap.put("fileId", uploadedFiles.get(0).getFileId());
			}
			return resultMap;
			
    	} catch (Exception e) {
    		 
		}
		return resultMap;
	}
}
