package back.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Configuration 
//설정 클래스라는 의미야. 스프링이 이 클래스를 읽고 @Bean 메서드들을 실행해서 객체(Bean)를 등록
public class Config {
	@Bean
    public BCryptPasswordEncoder passwordEncoder() {
		//BCryptPasswordEncoder는 Spring Security에서 일반적으로 쓰는 강력한 단방향 암호화 도구
        return new BCryptPasswordEncoder();
    }
    
    @Bean
    //인증을 실제로 처리하는 객체.
    //Spring Security 내부에서 로그인 처리, 세션 인증 등에 사용됨.
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }
}