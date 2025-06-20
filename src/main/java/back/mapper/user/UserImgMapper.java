// UserImgMapper.java
package back.mapper.user;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import back.model.user.UserImg;

import java.util.List;

@Mapper
public interface UserImgMapper {

    // 1. 이미지 목록 조회
    List<UserImg> getUserImgs(@Param("userId") String userId);

    // 2. 이미지 등록
    int insertUserImg(UserImg userImg);

    // 3. 이미지 수정
    int updateUserImg(UserImg userImg);

    // 4. 이미지 삭제 (논리 삭제)
    int deleteUserImg(@Param("userImgId") Long userImgId);
}