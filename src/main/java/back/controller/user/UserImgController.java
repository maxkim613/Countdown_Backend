package back.controller.user;

import back.model.user.UserImg;
import back.service.user.UserImgService;
import back.util.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/Img")
public class UserImgController {

    private final String UPLOAD_ROOT_PATH = "/uploads/userImg/";

    @Autowired
    private UserImgService userImgService;

    // 1. 이미지 리스트 조회
    @GetMapping("/findByUserImg")
    public ResponseEntity<ApiResponse<List<UserImg>>> findByUserId(@RequestParam("userId") String userId) {
        List<UserImg> imgs = userImgService.getUserImgs(userId);
        return ResponseEntity.ok(new ApiResponse<>(true, "프로필 이미지 조회 성공", imgs));
    }

    // 2. 이미지 업로드
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<String>> uploadUserImg(
            @RequestParam("userId") String userId,
            @RequestParam("file") MultipartFile file) throws IOException {

        if (file == null || file.isEmpty()) {
            return ResponseEntity.badRequest().body(new ApiResponse<>(false, "파일이 없습니다.", null));
        }

        String fileName = generateUniqueFileName(file.getOriginalFilename());
        String uploadPath = UPLOAD_ROOT_PATH + userId;
        makeDirectoryIfNotExist(uploadPath);

        File uploadedFile = new File(uploadPath, fileName);
        file.transferTo(uploadedFile);

        String filePath = "/uploads/userImg/" + userId + "/" + fileName;

        UserImg userImg = new UserImg();
        userImg.setUserId(userId);
        userImg.setUserImgName(fileName);
        userImg.setUserImgPath(filePath);
        userImg.setCreateId(userId);
        userImg.setDelYn("N");

        int result = userImgService.insertUserImg(userImg);

        return result > 0
                ? ResponseEntity.ok(new ApiResponse<>(true, "이미지 등록 성공", filePath))
                : ResponseEntity.status(500).body(new ApiResponse<>(false, "이미지 등록 실패", null));
    }

    // 3. 이미지 수정
    @PostMapping(value = "/update", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<String>> updateUserImg(
            @RequestParam("userImgId") Long userImgId,
            @RequestParam("userId") String userId,
            @RequestParam("file") MultipartFile file) throws IOException {

        if (file == null || file.isEmpty()) {
            return ResponseEntity.badRequest().body(new ApiResponse<>(false, "파일이 없습니다.", null));
        }

        String fileName = file.getOriginalFilename();

        // OS 기반 경로 분기
        String os = System.getProperty("os.name").toLowerCase();
        String baseUploadPath;

        if (os.contains("win")) {
            baseUploadPath = "C:\\upload\\userImg\\" + userId;
        } else {
            baseUploadPath = "/uploads/userImg/" + userId;
        }

        File dir = new File(baseUploadPath);
        if (!dir.exists()) {
            boolean created = dir.mkdirs(); // 디렉토리 생성
            if (!created) {
                return ResponseEntity.status(500).body(new ApiResponse<>(false, "디렉토리 생성 실패", null));
            }
        }

        File uploadedFile = new File(dir, fileName);
        file.transferTo(uploadedFile); // 예외 발생 가능 부분

        String filePath = "/uploads/userImg/" + userId + "/" + fileName;

        UserImg userImg = new UserImg();
        userImg.setUserImgId(userImgId);
        userImg.setUserId(userId);
        userImg.setUserImgName(fileName);
        userImg.setUserImgPath(filePath);
        userImg.setUpdateId(userId);

        int result = userImgService.updateUserImg(userImg);

        if (result > 0) {
            return ResponseEntity.ok(new ApiResponse<>(true, "이미지 수정 성공", filePath));
        } else {
            return ResponseEntity.status(500).body(new ApiResponse<>(false, "이미지 수정 실패", null));
        }
    }


    // ====== 내부 유틸 메서드 ======

    private void makeDirectoryIfNotExist(String path) {
        File dir = new File(path);
        if (!dir.exists()) dir.mkdirs();
    }

    private String generateUniqueFileName(String originalFileName) {
        String ext = originalFileName.substring(originalFileName.lastIndexOf("."));
        return UUID.randomUUID().toString().replaceAll("-", "") + ext;
    }
}
