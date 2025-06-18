package back.mapper.user;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import back.model.user.User;

@Mapper
public interface UserMapper {
	
	public List<User> getUserList(User user); 
	
	public int registerUser(User user);
	
	public User getUserById(String userId);
	
	public int updateUser(User user);
	
	public int deleteUser(User user);
	
	public int getTotalUserCount(User user);

	public int userM(User user);
	
	public int overlapByUserId(String userId);

	public int emailOverlap(String email);

	public int nicknameOverlap(String nickname);
	
	public User getUserByEmail(String email);

	public int countUserByInfo(String username, String userId, String email);

	public  int updatePassword(@Param("userId") String userId, @Param("password") String newPassword);

	public int existsUserImg(String userId);

	public int insertUserImg(User user);

	public int updateUserImg(User user);
	
	public int updateUserStatus(@Param("userId") String userId, @Param("status") String status);
	
	public String findIdByNickname(String nickname);
	
}