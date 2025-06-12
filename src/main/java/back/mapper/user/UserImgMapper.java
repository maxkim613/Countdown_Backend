package back.mapper.user;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import back.model.user.UserImg;

@Mapper
public interface UserImgMapper {

    // 이미지 단건 조회 (삭제되지 않은 이미지)
    UserImg findByUserId(String userId);

    // 이미지 등록
    int insert(UserImg userImg);

    // 이미지 수정
    int update(UserImg userImg);

    // 이미지 삭제 (DEL_YN = 'Y')
    int deleteById(@Param("userImgId") Long userImgId);
}
