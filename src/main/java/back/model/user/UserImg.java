package back.model.user;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserImg {
    private Long userImgId;
    private String userId;
    private String userImgName;
    private String userImgPath;
    private String createId;
    private String updateId;
    private Date createDt;
    private Date updateDt;
    private String delYn;
}
