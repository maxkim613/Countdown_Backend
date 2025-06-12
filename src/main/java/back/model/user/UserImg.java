package back.model.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserImg {
    private Long userImgId;       // 이미지 ID (PK)
    private String userId;        // 사용자 ID (FK)
    private String userImgName;   // 원본 이미지 파일명
    private String userImgPath;   // 저장된 경로 (또는 서버 파일명)
    private String createId;      // 등록자 ID
    private String updateId;      // 수정자 ID
    private String createDt;      // 등록일
    private String updateDt;      // 수정일
    private String delYn;         // 삭제 여부
}
