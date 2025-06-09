package back.controller.file;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import back.model.common.PostFile;
import back.service.file.FileService;
import back.util.ApiResponse;
import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;



@RestController
@RequestMapping("/api/file")
@Slf4j
public class FileController {
 
	@Value("${myapp.apiBaseUrl}")
	private String apiBaseUrl;
	//application.properties에 있는 값을 apiBaseUrl에 넣어준다고 보면됨
	
	@Autowired
	private FileService fileService;
	
	@Autowired
	private ServletContext servletContext;

	  @GetMapping("/down.do")
	  public void downloadFile(@RequestParam("filedId")String fileId, HttpServletResponse response) {
		  try {
			  
			  PostFile file = new PostFile();
			  file.setFileId(Integer.parseInt(fileId));
			  
			  PostFile selectFile = fileService.getFileByFileId(file);
			  
			  if(selectFile == null) {
				  response.getWriter().write("파일을 찾을 수 없습니다.");
				  return;
			  }
			  
			  //파일 경로 및 파일 이름 설정
			  String filePath = selectFile.getFilePath();
			  File downloadFile = new File(filePath);
			  if(!downloadFile.exists()) {
				  response.getWriter().write("파일이 존재하지 않습니다.");
				  return;
			  }
			  
			  String fileName = selectFile.getFileName();
			  
			  response.setContentType("application/octet.stream");
			  response.setContentLength((int) downloadFile.length());
			  
			  String downloadHeader = "attachment; filename=" + URLEncoder.encode(fileName, "UTF-8");
			  response.setHeader("Content-Disposition", downloadHeader);
			  
			  try(FileInputStream fileInputStream = new FileInputStream(downloadFile);
					  OutputStream out = response.getOutputStream()) {
				  byte[] buffer = new byte[4096];
				  int bytesRead;
				  
				  while((bytesRead = fileInputStream.read(buffer)) != -1) {
					  out.write(buffer,0,bytesRead);
				  }
			  } 
		  } catch (IOException e) {
			  log.info("파일 다운로드 중 오류가 발생했습니다.");
		  }
	  } 
	  
	  @GetMapping("/imgDown.do")
	  public void downloadImage(@RequestParam("filedId")String fileId, HttpServletResponse response) {
		  try {
			  PostFile file = new PostFile();
			  file.setFileId(Integer.parseInt(fileId));
			  PostFile selectFile = fileService.getFileByFileId(file);
			  
			  if(selectFile == null) {
				  response.getWriter().write("파일을 찾을 수 없습니다.");
				  return;
			  }
			  
			  File downloadFile = new File(selectFile.getFilePath());
			  if(!downloadFile.exists()) {
				  response.getWriter().write("파일이 존재하지 않습니다.");
				  return;
			  }
			  
			  String mimeType = servletContext.getMimeType(selectFile.getFilePath());
			  if (mimeType == null) mimeType = "application/octet-stream";
			  
			  response.setContentType(mimeType);
			  response.setContentLength((int) downloadFile.length());
			  response.setHeader("Content-Disposition", 
					  "inline; filename=" + URLEncoder.encode(selectFile.getFileName(), "UTF-8"));
		
			  try(
				  FileInputStream fis = new FileInputStream(downloadFile);
				  OutputStream out = response.getOutputStream()
				) {
				  byte[] buffer = new byte[4096];
				  int bytesRead;
				  
				  while((bytesRead = fis.read(buffer)) != -1) {
					  out.write(buffer,0,bytesRead);
				  }
			  }
		  } catch (Exception e) {
			  log.info("이미지를 다운로드 중 오류가 발생했습니다.");
	      } 
	}
	    	 
	
	 @PostMapping(value = "/imgUpload.do, consumes = MediaType.MULTIPART_FORM_DATA_VALUE")
	 public ResponseEntity<?> uploadImage(
			 @ModelAttribute PostFile postFile,
			 @RequestPart(value = "files", required = false) List<MultipartFile> files) {
		 log.info("이미지 파일 업로드 요청");
		 
		 HashMap<String, Object> responseMap = new HashMap<>();
		 postFile.setFiles(files);
		 boolean isUploadFile = false;
		 
		 try { 
			 postFile.setBasePath("img");
			 postFile.setCreateId("SYSTEM");
			 
			 HashMap resultMap = (HashMap) fileService.insertBoardFiles(postFile);
			 isUploadFile = (boolean) resultMap.get("result");
			  
			 if(isUploadFile) {
				 responseMap.put("url",apiBaseUrl+"/api/file/imgDown.do?fileId=" + resultMap.get("fileId")); 
			 }
		 } catch (Exception e) {
			 log.error("이미지 파일 업로드 중 오류",e);
		 }
		 
		 return ResponseEntity.ok(new ApiResponse<>(isUploadFile,
				 isUploadFile ? "이미지 파일 업로드 성공" : "이미지 파일 업로드 실패", responseMap));
	 }

}
