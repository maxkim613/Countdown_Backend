package back.service.user;

import back.model.user.UserImg;

public interface UserImgService {
	
    UserImg getUserImgByUserId(String userId);
    
    void uploadUserImg(UserImg userImg);
    
    void updateUserImg(UserImg userImg);
    
    void deleteUserImg(Long userImgId);
}
