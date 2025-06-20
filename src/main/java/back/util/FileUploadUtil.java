package back.util;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import back.model.common.AucPostFile;
import back.model.common.PostFile;

public class FileUploadUtil {
    private static final String UPLOAD_DIR = "uploads"; // 최상위 업로드 디렉토리

    /**
     * 다중 파일 업로드 처리
     */
    public static List<PostFile> uploadFiles(List<MultipartFile> multipartFiles, String basePath, int boardId, String userId) throws IOException {
        List<PostFile> uploadedFiles = new ArrayList<>();
        
        // yyyy-MM-dd 형식으로 단일 날짜 폴더 구성
        String dateFolder = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        String uploadPath = getUploadPath(basePath, dateFolder);

        File uploadDir = new File(uploadPath);
        if (!uploadDir.exists()) {
            uploadDir.mkdirs(); // 폴더 없으면 생성
        }

        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmssSSS");

        for (MultipartFile file : multipartFiles) {
            String originalFileName = Paths.get(file.getOriginalFilename()).getFileName().toString();

            if (!originalFileName.isEmpty()) {
                String safeFileName = originalFileName.replaceAll("[^a-zA-Z0-9._-]", "_");

                String fileExtension = "";
                int dotIndex = safeFileName.lastIndexOf(".");
                if (dotIndex > 0) {
                    fileExtension = safeFileName.substring(dotIndex);
                    safeFileName = safeFileName.substring(0, dotIndex);
                }

                String timestamp = sdf.format(new Date()) + "_" + System.nanoTime();
                String newFileName = timestamp + "_" + safeFileName + fileExtension;

                String filePath = uploadPath + File.separator + newFileName;

                file.transferTo(new File(filePath));

                PostFile postFile = new PostFile();
                postFile.setBoardId(boardId);
                postFile.setCreateId(userId);
                postFile.setUpdateId(userId);
                postFile.setFileName(originalFileName);
                postFile.setFilePath(filePath); // 실제 파일 전체 경로
                postFile.setDelYn("N");

                uploadedFiles.add(postFile);
            }
        }

        return uploadedFiles;
    }
    public static List<AucPostFile> aucuploadFiles(
            List<MultipartFile> multipartFiles,
            String basePath,
            int aucId,
            String userId,
            int mainImageIndex
    ) throws IOException {
        List<AucPostFile> uploadedFiles = new ArrayList<>();

        // 날짜 기반 폴더명 생성
        String dateFolder = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        String uploadPath = getUploadPath(basePath, dateFolder);

        // 실제 폴더 생성
        File uploadDir = new File(uploadPath);
        if (!uploadDir.exists()) {
            uploadDir.mkdirs();
        }

        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmssSSS");

        for (int i = 0; i < multipartFiles.size(); i++) {
            MultipartFile file = multipartFiles.get(i);
            String originalFileName = Paths.get(file.getOriginalFilename()).getFileName().toString();

            if (!originalFileName.isEmpty()) { 
                String safeFileName = originalFileName.replaceAll("[^a-zA-Z0-9._-]", "_");

                String fileExtension = "";
                int dotIndex = safeFileName.lastIndexOf(".");
                if (dotIndex > 0) {
                    fileExtension = safeFileName.substring(dotIndex);
                    safeFileName = safeFileName.substring(0, dotIndex);
                }

                String timestamp = sdf.format(new Date()) + "_" + System.nanoTime();
                String newFileName = timestamp + "_" + safeFileName + fileExtension;

                String filePath = uploadPath + File.separator + newFileName;
                file.transferTo(new File(filePath));

                AucPostFile postFile = new AucPostFile();
                postFile.setAucId(aucId);
                postFile.setCreateId(userId);
                postFile.setUpdateId(userId);
                postFile.setFileName(originalFileName);
                postFile.setFilePath(filePath); // 실제 저장된 전체 경로 (PostFile 방식과 동일)
                postFile.setIsMain(i == mainImageIndex ? "Y" : "N");
                postFile.setDelYn("N");
                

                uploadedFiles.add(postFile);
            }
        }

        return uploadedFiles;
    }


    /**
     * 업로드 경로 반환 (날짜 단일 폴더 포함)
     */
    public static String getUploadPath(String basePath, String dateFolder) {
    	return Paths.get(UPLOAD_DIR, basePath, dateFolder).toString();
    }
}
