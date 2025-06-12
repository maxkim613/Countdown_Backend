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

public class UserServiceImpl implements UserService {	
	  
   @Autowired
    private UserMapper userMapper;
   @Autowired
    private BCryptPasswordEncoder passwordEncoder; // MyBatis SQL 세션 팩토리
 
  
  
    
   
	@Override
	@Transactional
	public boolean registerUser(User user) {

		try {

			String password = user.getPassword();

			user.setPassword(password != null ? passwordEncoder.encode(password) : null);

			return userMapper.registerUser(user) > 0;

		} catch (Exception e) {
			log.error("회원 가입중 오류", e);
			throw new HException("회원 가입중 오류", e);
		}
	}
 
	@Override
	public boolean validateUser(User user) {

		try {
			User dBUser = userMapper.getUserById(user.getUserId());

			if (dBUser == null)
				return false;

			String encryptedPassword = passwordEncoder.encode(user.getPassword());

			return passwordEncoder.matches(dBUser.getPassword(), encryptedPassword);

		} catch (Exception e) {
			log.error("사용자 조회 중 오류", e);
			throw new HException("사용자 조회 중 오류", e);
		}

	}

	@Override
	public User getUserById(String userId) {
		try {

			return userMapper.getUserById(userId);

		} catch (Exception e) {
			log.error("사용자 조회 중 오류", e);
			throw new HException("사용자 조회 중 오류", e);
		}
	}

	@Override
	@Transactional
	public boolean updateUser(User user) {

		try {

			String Password = user.getPassword();

			user.setPassword(Password != null ? passwordEncoder.encode(Password) : null);

			return userMapper.updateUser(user) > 0;
		
		} catch (Exception e) {
		
			log.error("사용자 정보 수정 중 오류", e);
			
			throw new HException("사용자 정보 수정 중 오류", e);
		}
	}
	
	@Override
	@Transactional
	public boolean deleteUser(User user) {

		try {

			return userMapper.deleteUser(user) > 0;
			
		} catch (Exception e) {
		
			log.error("사용자 삭제 중 오류", e);
			
			throw new HException("사용자 삭제 중 오류", e);
		}
	}

	@Override
	@Transactional
	public List<User> getUserList(User user) {
		try {

			int page = user.getPage();
			int size = user.getSize();

			int totalCount = userMapper.getTotalUserCount(user);
			int totalPage = (int) Math.ceil((double) totalCount / size);

			int startRow = (page - 1) * size + 1;
			int endRow = page * size;

			user.setTotalCount(totalCount);
			user.setTotalPages(totalPage);
			user.setStartRow(startRow);
			user.setEndRow(endRow);

			List list = userMapper.getUserList(user);

			return list;
		} catch (Exception e) {
			log.error("게시글 목록 조회 실패", e);
			throw new HException("게시글 목록 조회 실패", e);
		}
	}

	@Override
	@Transactional
	public boolean userM(User user) {
		try {

			return userMapper.userM(user) > 0;
			
		} catch (Exception e) {
		
			log.error("사용자 관리 중 오류", e);
			
			throw new HException("사용자 관리 실패", e);
		}
	}

	@Override
	@Transactional
	public boolean userIdOverlap(String userId) {
		
		 return userMapper.overlapByUserId(userId)>0;
	}

	@Override
	public boolean emailOverlap(String email) {
		
		return userMapper.emailOverlap(email)>0;
	}

	@Override
	public boolean nicknameOverlap(String nickname) {
		
		return userMapper.nicknameOverlap(nickname)>0;
	}

	@Override
	public String findUserId(String username, String email) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean checkUserByInfo(String username, String userId, String email) {
		
		return userMapper.countUserByInfo(username, userId, email) > 0;
	}

	@Override
	public boolean resetPassword(String userId, String newPassword) {
		
	    try {
	    	
	        String encryptedPassword = passwordEncoder.encode(newPassword);
	        
	        int updated = userMapper.updatePassword(userId, encryptedPassword);
	        
	        return updated > 0;
	        
	    } catch (Exception e) {
	    	
	        log.error("비밀번호 재설정 중 오류", e);
	        
	        throw new HException("비밀번호 재설정 중 오류", e);
	    }
	}

	@Override
    public boolean saveOrUpdateUserImg(User user) {
        int count = userMapper.existsUserImg(user.getUserId());
        if (count > 0) {
            return userMapper.updateUserImg(user) > 0;
        } else {
            return userMapper.insertUserImg(user) > 0;
        }
    }

	@Override
	public boolean updateUserStatus(String userId, String status) {
	    int updated = userMapper.updateUserStatus(userId, status);
	    return updated > 0;
	}
	
	



}
	
	

	
	


