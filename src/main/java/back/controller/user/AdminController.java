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
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import back.dto.CertiRequest;
import back.dto.CertiVerifyRequest;
import back.dto.FindIdResponseDto;
import back.dto.ResetPasswordRequest;
import back.mapper.user.UserMapper;
import back.model.board.Board;
import back.model.common.CustomUserDetails;
import back.model.user.User;
import back.service.user.CertiService;
import back.service.user.UserService;
import back.service.util.EmailService;
import back.util.ApiResponse;
import back.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin")
public class AdminController {

    private final AuthenticationManager authenticationManager;

    @PostMapping("/login.do")
    public ResponseEntity<?> login(@RequestBody User user, HttpServletRequest request) {
        try {
            Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(user.getUserId(), user.getPassword()));
            SecurityContextHolder.getContext().setAuthentication(auth);

            CustomUserDetails userDetails = (CustomUserDetails) auth.getPrincipal();

            // 세션 저장
            HttpSession session = request.getSession(true);
            session.setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, SecurityContextHolder.getContext());

            return ResponseEntity.ok(new ApiResponse<>(true, "로그인 성공", userDetails.getUser()));

        } catch (AuthenticationException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new ApiResponse<>(false, "아이디 또는 비밀번호가 일치하지 않습니다.", null));
        }
    }
}
