package back.controller.user;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import back.model.board.Board;
import back.model.common.CustomUserDetails;
import back.model.user.User;
import back.service.user.UserService;
import back.util.ApiResponse;
import back.util.SecurityUtil;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/user")
@Slf4j
public class UserController {

	@Autowired
	 private AuthenticationManager authenticationManager;
	
	@Autowired
	 private UserService userService;
	
	@PostMapping("/view.do")
	public ResponseEntity<?> view(@RequestBody User user) {
		//@RequestBody 클라이언트가 보낸 JSON 데이터를 자바 객체로 자동 매핑
		CustomUserDetails userDetails = (CustomUserDetails) SecurityContextHolder.getContext()
				.getAuthentication().getPrincipal();
		//SecurityContextHolder는 Spring Security에서 
		//현재 로그인한 사용자 정보를 저장하고 꺼내는 데 사용하는 클래스
		String userId = "";
		if(user.getUserId() == null || user.getUserId().equals("")) {
			userId= userDetails.getUser().getUserId();
		} else {
			userId = user.getUserId();
		}
		
//		SecurityUtil.checkAuthorization(userDetails, userDetails.getUser().getUserId());
		User selectUser = userService.getUserById(userId);
		
		return ResponseEntity.ok(new ApiResponse<>(true,"조회 성공",selectUser));
	}
	
	@PostMapping("/register.do")
	public ResponseEntity<?> register(@RequestBody User user) {
		log.info("회원가입 요청: {}",user.getUserId());
		
		user.setCreateId("SYSTEM");
		boolean success = userService.registerUser(user);
		
		return ResponseEntity.ok(new ApiResponse<>(true,"조회 성공",user));
	}
	
	@PostMapping("/update.do")
	public ResponseEntity<?> update(@RequestBody User user) {
		CustomUserDetails userDetails = (CustomUserDetails) SecurityContextHolder.getContext()
				.getAuthentication().getPrincipal();
		
		
		log.info("회원정보 수정 요청: {}",user.getUserId());
		SecurityUtil.checkAuthorization(userDetails, user.getUserId());
		user.setUpdateId(userDetails.getUsername());
		
		boolean success = userService.updateUser(user);
		
		return ResponseEntity.ok(new ApiResponse<>(success,success?"수정 성공":"수정 실패",null));
	}
	
	@PostMapping("/delete.do")
	public ResponseEntity<?> delete(@RequestBody User user, HttpSession session) {
		CustomUserDetails userDetails = (CustomUserDetails) SecurityContextHolder.getContext()
				.getAuthentication().getPrincipal();
		
		
		log.info("회원탈퇴 수정 요청: {}",user.getUserId());
		SecurityUtil.checkAuthorization(userDetails, user.getUserId());
		user.setUpdateId(userDetails.getUsername());
		
		boolean success = userService.deleteUser(user);
		if(success) {
			session.invalidate();
			SecurityContextHolder.clearContext(); 
		}
		
		return ResponseEntity.ok(new ApiResponse<>(success,success?"삭제 성공":"삭제 실패",null));
	}
	@PostMapping("/userM.do")
	public ResponseEntity<?> userM(@RequestBody User user, HttpSession session) {
		CustomUserDetails userDetails = (CustomUserDetails) SecurityContextHolder.getContext()
				.getAuthentication().getPrincipal();
		
		
		log.info("회원관리 요청: {}",user.getUserId());
		user.setUpdateId(userDetails.getUsername());
		boolean success = userService.userM(user);
		
		return ResponseEntity.ok(new ApiResponse<>(success,success?"관리 성공":"관리 실패",null));
	}


	@PostMapping("/list.do")
	public ResponseEntity<?> getUserList(@RequestBody User user) {
		log.info(user.toString());
		List<User> userList = userService.getUserList(user);
		Map dataMap = new HashMap();
		dataMap.put("list",userList);
		dataMap.put("user",user);
		return ResponseEntity.ok(new ApiResponse<>(true,"목록조회성공",dataMap));
	}
	@PostMapping("/login.do")
	public ResponseEntity<?> login(@RequestBody User user, HttpServletRequest request) {
		log.info("로그인 시도: {}",user.getUserId());
		try {
			Authentication auth = authenticationManager.authenticate(
					new UsernamePasswordAuthenticationToken(user.getUserId(),user.getPassword())
			);
			
			SecurityContextHolder.getContext().setAuthentication(auth);
			
			HttpSession session = request.getSession(true);
			session.setAttribute(
			HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY,
			SecurityContextHolder.getContext()
			);
			log.info("세선 id:{}", session.getId());
			CustomUserDetails userDetails = (CustomUserDetails) SecurityContextHolder.getContext()
					.getAuthentication().getPrincipal();
			return ResponseEntity.ok(new ApiResponse<>(true,"로그인성공",userDetails.getUser()));
		} catch (AuthenticationException e) {
			log.warn("로그인실패:{}",user.getUserId());
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
					.body(new ApiResponse<>(false,"아이디 또는 비밀번호가 일치하지 않습니다.",null));
		}
	}   
	@PostMapping("/logout.do")
	public ResponseEntity<?> logout(HttpServletRequest request, HttpServletResponse response) {
	    log.info("로그아웃 요청");

	        request.getSession().invalidate(); // 세션 무효화
	        SecurityContextHolder.clearContext(); // 보안 컨텍스트 제거

	        // JSESSIONID 쿠키 삭제
	        Cookie cookie = new Cookie("JSESSIONID", null); // null 또는 빈 값
	        cookie.setMaxAge(0); // 즉시 만료
	        cookie.setPath("/"); // 경로 주의: 쿠키 설정된 경로와 맞춰야 함
	        cookie.setHttpOnly(true); // 원래 쿠키가 HttpOnly였다면 유지
	        cookie.setSecure(true);   // HTTPS 환경이면 true
	        response.addCookie(cookie);

	        return ResponseEntity.ok(new ApiResponse<>(true, "로그아웃 완료", null));
	    }

}
