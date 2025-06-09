package back.service.user;

import java.util.List;

import back.model.user.User;

public interface UserService {
	 public boolean registerUser(User user);
	    
	 public boolean validateUser(User user);
	    
	 public User getUserById(String userId);
	    
	 public boolean updateUser(User user);
	 
	 public boolean deleteUser(User user);
	 
	 public List<User> getUserList(User user);
	 
	 public boolean userM(User user);

}