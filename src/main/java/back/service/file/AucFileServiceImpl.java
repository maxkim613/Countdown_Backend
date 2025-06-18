package back.service.file;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import back.mapper.file.AucFileMapper;
import back.model.common.AucPostFile;
import back.util.FileUploadUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class AucFileServiceImpl implements AucFileService{
	
	private final  AucFileMapper fileMapper;
	
	public AucPostFile getFileByFileId(AucPostFile file) { 
		AucPostFile PostFile = fileMapper.getFileByFileId(file);
		return PostFile;
	}
	
	@Transactional
	@Override
	public Map<String, Object> insertBoardFiles(AucPostFile file) { 
		Map<String, Object> resultMap = new HashMap<String, Object>();
		try {
			int aucId = file.getAucId();
			String userId = file.getCreateId();
			String basePath = file.getBasePath();
		
			List<MultipartFile> files = file.getFiles();
		
			if(files == null || files.isEmpty()) {
				resultMap.put("result",false);
				resultMap.put("message","파일이 존재하지 않습니다.");
				return resultMap;
			}
				
			int mainImageIndex = 0; // 예: 첫 번째 이미지를 대표 이미지로
			List<AucPostFile> uploadedFiles = FileUploadUtil.aucuploadFiles(files, basePath, aucId, userId, mainImageIndex);

	            for (AucPostFile postFile : uploadedFiles) {
	                fileMapper.insertFile(postFile);
	            }

	            resultMap.put("result", true); // ← 성공 플래그
	            if (!uploadedFiles.isEmpty()) {
	                resultMap.put("fileId", uploadedFiles.get(0).getFileId());
	            }
	
		        } catch (Exception e) {
		            log.error("파일 업로드 중 오류 발생", e);
		            resultMap.put("result", false);
		            resultMap.put("message", "파일 업로드 중 오류가 발생했습니다.");
		        }
				return resultMap;
			}
		}
