package back.service.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import back.mapper.user.UserImgMapper;
import back.model.user.UserImg;

@Service
public class UserImgServiceImpl implements UserImgService {

    @Autowired
    private UserImgMapper userImgMapper;

    @Override
    public UserImg getUserImgByUserId(String userId) {
        return userImgMapper.findByUserId(userId);
    }

    @Override
    public void uploadUserImg(UserImg userImg) {
        userImgMapper.insert(userImg);
    }

    @Override
    public void updateUserImg(UserImg userImg) {
        userImgMapper.update(userImg);
    }

    @Override
    public void deleteUserImg(Long userImgId) {
        userImgMapper.deleteById(userImgId);
    }
}
