package back.service.user;

import back.model.user.UserImg;

import java.util.List;

public interface UserImgService {
	
    List<UserImg> getUserImgs(String userId);
    
    int insertUserImg(UserImg userImg);
    
    int updateUserImg(UserImg userImg);
    
    int deleteUserImg(Long userImgId);
}
