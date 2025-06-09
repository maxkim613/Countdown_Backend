package back.service.user;

import java.util.List;

import back.model.user.User;

public interface UserService {
    /**
     * 사용자 회원가입
     */
	public boolean registerUser(User user);
    
	public boolean validateUser(User user);
    
    public User getUserById(String userId);

    public boolean updateUser(User user);

    public boolean deleteUser(User user);
    
    public List<User> getUserList(User user);
    
    public boolean userM(User user);
    
    public boolean userIdOverlap(String userId);
    
    public boolean emailOverlap(String email);
    
    public boolean nicknameOverlap(String nickname);

	public String findUserId(String username, String email);
	
	public boolean checkUserByInfo(String username, String userId, String email);
	
	public boolean resetPassword(String userId, String newPassword);

	
    

}