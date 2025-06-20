package back.service.user;

import back.mapper.user.UserImgMapper;
import back.model.user.UserImg;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserImgServiceImpl implements UserImgService {

    @Autowired
    private UserImgMapper userImgMapper;

    @Override
    public List<UserImg> getUserImgs(String userId) {
        return userImgMapper.getUserImgs(userId);
    }

    @Override
    public int insertUserImg(UserImg userImg) {
        return userImgMapper.insertUserImg(userImg);
    }

    @Override
    public int updateUserImg(UserImg userImg) {
        return userImgMapper.updateUserImg(userImg);
    }

    @Override
    public int deleteUserImg(Long userImgId) {
        return userImgMapper.deleteUserImg(userImgId);
    }
}