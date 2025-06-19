package back.controller.user;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.el.stream.Optional;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import back.dto.CertiRequest;
import back.dto.CertiVerifyRequest;
import back.dto.FindIdResponseDto;
import back.dto.ResetPasswordRequest;
import back.dto.UserStatusRequestDto;
import back.exception.HException;
import back.mapper.user.UserMapper;
import back.model.board.Board;
import back.model.common.CustomUserDetails;
import back.model.user.User;
import back.service.user.CertiService;
import back.service.user.UserService;
import back.service.util.EmailService;
import back.util.ApiResponse;
import back.util.SecurityUtil;
import lombok.extern.slf4j.Slf4j;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;


@RestController
@RequestMapping("/api/user")
@Slf4j
public class UserController {

	@Autowired
	private AuthenticationManager authenticationManager;

	@Autowired
	private UserService userService;
	
	@Autowired
	private CertiService certiService;
	
	@Autowired
	private EmailService emailService;
	
	@Autowired
	private UserMapper userMapper;
	
	private final Map<String, String> certiMap = new HashMap<>();
	
	
	
		/**
		 * 회원정보 리스트
		 * 
		 * @return
		 */
	@PostMapping("/list.do")
	public ResponseEntity<?> getUserList(@RequestBody User user) {

		log.info(user.toString());

		List<User> userList = userService.getUserList(user);

		Map dataMap = new HashMap();

		dataMap.put("list", userList);

		dataMap.put("user", user);

		return ResponseEntity.ok(new ApiResponse<>(true, "목표 조회 성공", dataMap));

	}
		
		
	
	
		/**
		 * 회원정보 조회
		 * 
		 * @return
		 */
	@PostMapping("/view.do")
	
	public ResponseEntity<?> view(@RequestBody User user) {

		CustomUserDetails userDetails = (CustomUserDetails) SecurityContextHolder.getContext()

				.getAuthentication().getPrincipal();
		
		String userId = "";

		//SecurityUtil.checkAuthorization(userDetails, userDetails.getUser().getUserId());
		if(user.getUserId() == null || user.getUserId().equals("")) {
			
			userId = userDetails.getUser().getUserId();
			
		} else {
			
			userId = user.getUserId();
		}
		
		User selectUser = userService.getUserById(userId);

		return ResponseEntity.ok(new ApiResponse<>(true, "조회 성공", selectUser));
	}
 
	
		/**
		 * 회원가입
		 * 
		 * @param user
		 * @return
		 */
		@PostMapping("/join.do")
		public ResponseEntity<?> register(@RequestBody User user) {

			
			log.info("회원가입 유저 : {}", user.getUserId());
            log.info("회원가입 유저 : {}", user.toString());

			user.setCreateId("SYSTEM");

			boolean success = userService.registerUser(user);
			

			return ResponseEntity.ok(new ApiResponse<>(success, success ? "회원가입 성공" : "회원가입 실패", null));
		}
		
	 
		/**
		 * 회원장보 수정
		 */
		@PostMapping("/update.do")
		public ResponseEntity<?> update(
		        @RequestPart("user") User user,
		        @RequestPart(value = "file", required = false) MultipartFile file) {
			
		    if (file != null && !file.isEmpty()) {
		        // userId null 체크
		        String userId = user.getUserId();
		        if (userId == null || userId.isEmpty()) {
		            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("UserId가 없습니다.");
		        }
		

		    CustomUserDetails userDetails = (CustomUserDetails) SecurityContextHolder.getContext()
		            .getAuthentication().getPrincipal();

		    user.setUpdateId(userDetails.getUsername());

		

		        // 절대경로로 변경 (예: c:/uploads/userId/)
		        String baseUploadDir = "c:/uploads";  // 보통은 application.properties에서 관리하는게 좋음
		        File uploadDir = new File(baseUploadDir, userId);

		        if (!uploadDir.exists()) {
		            uploadDir.mkdirs();
		        }

		        // OS 독립적인 경로 생성
		        String originalFilename = file.getOriginalFilename();
		        File destinationFile = new File(uploadDir, originalFilename);

		        try {
		            file.transferTo(destinationFile);
		        } catch (IOException e) {
		            e.printStackTrace();
		            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("파일 저장 실패");
		        }

		        // DB에 저장할 경로와 파일명 설정
		        user.setUserImgName(originalFilename);
		        user.setUserImgPath(destinationFile.getAbsolutePath());

		        userService.saveOrUpdateUserImg(user);
		    }

		    boolean success = userService.updateUser(user);

		    return ResponseEntity.ok(new ApiResponse<>(success, success ? "수정 성공" : "수정 실패", null));
		}

		

		/**
		 * 회원탈퇴
		 * 
		 * @param user
		 * @param session
		 * @return
		 */
		@PostMapping("/delete.do")
		public ResponseEntity<?> delete(@RequestBody User user, HttpSession session) {

			CustomUserDetails userDetails = (CustomUserDetails) SecurityContextHolder.getContext()

					.getAuthentication().getPrincipal();

			log.info("회원탈되 요청: {}", user.getUserId());

			SecurityUtil.checkAuthorization(userDetails, user.getUserId());

			user.setUpdateId(userDetails.getUsername());

			boolean success = userService.deleteUser(user);

			if (success) {

				session.invalidate();

				SecurityContextHolder.clearContext();
			}

			return ResponseEntity.ok(new ApiResponse<>(success, success ? "삭제 성공" : "삭제 실패", null));
		}
	 
	 
		/**
		 * 로그인 처리
		 */
		@PostMapping("/login.do")
		public ResponseEntity<?> login(@RequestBody User user, HttpServletRequest request) {

			log.info("로그인 시도: {}", user.getUserId());

			try {

				Authentication auth = authenticationManager.authenticate(

						new UsernamePasswordAuthenticationToken(user.getUserId(), user.getPassword()));
				SecurityContextHolder.getContext().setAuthentication(auth);

				HttpSession session = request.getSession(true);

				session.setAttribute(

						HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY,

						SecurityContextHolder.getContext());
				log.info("세션 ID: {}", session.getId());
				
				CustomUserDetails userDetails = (CustomUserDetails) SecurityContextHolder.getContext()

						.getAuthentication().getPrincipal();

				return ResponseEntity.ok(new ApiResponse<>(true, "로그인 성공", userDetails.getUser()));

			} catch (AuthenticationException e) {

				return ResponseEntity.status(HttpStatus.UNAUTHORIZED)

						.body(new ApiResponse<>(false, "아이디 또는 비밀번호가 일치하지 않습니다.", null));
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
		
		@PostMapping("/userM.do")
		public ResponseEntity<?> userM(@RequestBody User user) {

			CustomUserDetails userDetails = (CustomUserDetails) SecurityContextHolder.getContext()

					.getAuthentication().getPrincipal();

			log.info("회원관리 요청: {}", user.getUserId());		

			user.setUpdateId(userDetails.getUsername());

			boolean success = userService.userM(user);

			return ResponseEntity.ok(new ApiResponse<>(success, success ? "회원관리 성공" : "회원관리 실패", null));
		}
		
		@PostMapping("/checkUserId.do")
		public ResponseEntity<?> checkUserId(@RequestBody User user) {

		    log.info("아이디 중복 확인 요청: {}", user.getUserId());

		    boolean success = userService.userIdOverlap(user.getUserId());

		    return ResponseEntity.ok(new ApiResponse<>(true, success ? "중복된 아이디입니다." : "사용 가능한 아이디입니다.", success));
		}
		
		@PostMapping("/checkNickname.do")
		public ResponseEntity<?> checkNickname(@RequestBody User user) {
			
			String nickname = user.getNickname();

		    log.info("닉네임 중복 확인 요청: {}", nickname);

		    boolean success = userService.nicknameOverlap(nickname);

		    return ResponseEntity.ok(new ApiResponse<>(true, success ? "중복된 닉네임입니다." : "사용 가능한 닉네임입니다.", success));
		}
		
		@PostMapping("/checkEmail.do")
		public ResponseEntity<?> checkEmail(@RequestBody User user) {
			
			String email = user.getEmail();

		    log.info("이메일 중복 확인 요청: {}", email);

		    boolean success = userService.emailOverlap(email);

		    return ResponseEntity.ok(new ApiResponse<>(true, success ? "중복된 이메일입니다." : "사용 가능한 이메일입니다.", success));
		}
		
		@PostMapping("/findId")
		
		public ResponseEntity<?> findUserId(@RequestBody Map<String, String> body) {
			
			String username = body.get("username");
			
			String email = body.get("email");
			
			log.info("아이디 찾기 요청: username={}, email={}", username, email);
			
			String userId = userService.findUserId(username, email);		  

		    boolean success = (userId != null);

		    return ResponseEntity.ok(new ApiResponse<>(true, success ? "일치하는 아이디를 찾았습니다." : "일치하는 회원이 없습니다.", userId));
		}
		
		@PostMapping("/sendCertiNum")
		
		public ResponseEntity<?> sendCertiNum(@RequestBody Map<String, String> body) {
			
		    String email = body.get("email");
		    
		    String name = body.get("username");		    

		    String certiNum = certiService.generateCertiNum(); // 6자리 랜덤 생성
		    
		    certiService.saveCertiInfo(name, email, certiNum); // DB 저장
		    
		    emailService.sendCertiEmail(email, certiNum); // 이메일 전송

		    return ResponseEntity.ok(new ApiResponse<>(true, "이메일로 인증번호를 발송했습니다."));
		}
		
		 @PostMapping("/verifyCertiNum")
		 
			public ResponseEntity<?> verifyCertiNum(@RequestBody Map<String, String> body) {

			String email = body.get("email");

			String inputCertiNum = body.get("certiNum");

			if (certiService.verifyCerti(email, inputCertiNum)) {

				User user = userMapper.getUserByEmail(email);

			if (user != null) {

				FindIdResponseDto dto = new FindIdResponseDto(user.getUsername(), user.getUserId());

				return ResponseEntity.ok(new ApiResponse<>(true, "아이디 찾기 성공", dto));

				} else {

				return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse<>(false, "해당 이메일로 등록된 사용자가 없습니다."));
					}
				}

				return ResponseEntity.ok(new ApiResponse<>(false, "인증번호 불일치"));
		}
		 
		 @PostMapping("/sendCerti")
		    public Map<String, String> sendCertiNum(@RequestBody CertiRequest request) {
			 
		        boolean exists = userService.checkUserByInfo(request.getUsername(), request.getUserId(), request.getEmail());
		        
		        if (!exists) {
		            return Map.of("message", "입력한 정보와 일치하는 계정이 없습니다.");
		        }

		        String certiNum = UUID.randomUUID().toString().substring(0, 6);
		        certiMap.put(request.getEmail(), certiNum);
		        emailService.sendCertiEmail(request.getEmail(), certiNum);

		        return Map.of("message", "인증번호가 이메일로 전송되었습니다.");
		    }
		  @PostMapping("/verifyCerti")
		    public Map<String, String> verifyCerti(@RequestBody CertiVerifyRequest request) {
		        String savedCerti = certiMap.get(request.getEmail());
		        if (savedCerti == null || !savedCerti.equals(request.getCertiNum())) {
		            return Map.of("message", "인증번호가 일치하지 않습니다.");
		        }
		        return Map.of("message", "인증번호가 확인되었습니다.");
		    }
		 
		  @PostMapping("/resetPassword")
		  public ResponseEntity<?> resetPassword(@RequestBody ResetPasswordRequest request) {
		      System.out.println("Request: " + request);
		      boolean result = userService.resetPassword(request.getUserId(), request.getPassword());
		      if (result) {
		          return ResponseEntity.ok(Map.of("message", "비밀번호가 성공적으로 변경되었습니다."));
		      } else {
		          return ResponseEntity.badRequest().body(Map.of("message", "비밀번호 변경에 실패했습니다."));
		      }
		  }		
		 
		  
		  @PostMapping("/update-status.do")
		  public ResponseEntity<?> updateUserStatus(@RequestBody UserStatusRequestDto dto) {
		      try {
		          User user = new User();
		          user.setUserId(dto.getUserId());
		          user.setUpdateId(dto.getAdminId());

		          if ("정지".equals(dto.getStatus())) {
		              user.setAccYn("Y");
		          } else if ("활성".equals(dto.getStatus())) {
		              user.setAccYn("N");
		          }

		          userService.updateUserStatus(user);

		          return ResponseEntity.ok(new ApiResponse<>(true, "상태 변경 완료", null));
		      } catch (HException e) {
		          e.printStackTrace();
		          return ResponseEntity.ok(new ApiResponse<>(false, "상태 변경 실패: " + e.getMessage(), null));
		      }
		  }

}


		

