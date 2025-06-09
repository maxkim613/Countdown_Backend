package back.service.user;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import back.exception.HException;
import back.mapper.user.UserMapper;
import back.model.user.User;
import lombok.extern.slf4j.Slf4j;


@Service
@Slf4j
public class UserServiceImpl implements UserService{
	@Autowired
	private UserMapper userMapper;
	@Autowired
	private BCryptPasswordEncoder passwordEncoder;
	

	@Override
	@Transactional
	public boolean registerUser(User user) {
        try {
        	String password = user.getPassword();
        	user.setPassword(password != null ? passwordEncoder.encode(password) : null);
        	return userMapper.registerUser(user) >0;
        } catch (Exception e) {
        	log.error("회원가입중 오류",e);
        	throw new HException("회원가입 실패",e);
        }
	}

	@Override
	public boolean validateUser(User user) {
		try {
			User dbUser = userMapper.getUserById(user.getUserId());
			if(dbUser == null) return false;
			
        	String encryptedPassword = passwordEncoder.encode(user.getPassword()); 
        	return passwordEncoder.matches(dbUser.getPassword(), encryptedPassword);
        } catch (Exception e) {
        	log.error("로그인 검증 중 오류",e);
        	throw new HException("로그인 검증 실패",e);
        }
	}

	@Override
	public User getUserById(String userId) {
		return userMapper.getUserById(userId);
	}

	@Override
	public boolean updateUser(User user) {
		String password = user.getPassword();
		user.setPassword(password != null ? passwordEncoder.encode(password) : null);
		return userMapper.updateUser(user) > 0;
	}

	@Override
	public boolean deleteUser(User user) {
		
		 String password = user.getPassword();
         user.setPassword(password!=null ? passwordEncoder.encode(password):null);
         return userMapper.deleteUser(user) > 0;
	}
	@Override
	public boolean userM(User user) {
		try {
			return userMapper.userM(user) > 0;
		}catch (Exception e) {
			log.error("사용자 관리 중 오류",e);
			throw new HException("사용자 관리 중 오류",e);
		}
		 
	}

	@Override
	public List<User> getUserList(User user) {
		try {
			int page = user.getPage();
	    	int size = user.getSize();
	    	
	    	int totalCount = userMapper.getTotalUserCount(user);
	    	int totalPages = (int) Math.ceil((double) totalCount/size);
	    	
	    	int startRow = (page - 1)*size + 1;
	    	int endRow = page*size;
	    	
	    	user.setTotalCount(totalCount);
	    	user.setTotalPages(totalPages);
	    	user.setStartRow(startRow);
	    	user.setEndRow(endRow);
	    	
	    	return userMapper.getUserList(user);
		} catch (Exception e) {
			log.error("유저 목록 조회실패",e);
			throw new HException("유저 목록 조회실패",e);
		}
	}

}
