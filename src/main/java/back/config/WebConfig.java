package back.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**") // /api/로 시작하는 주소들에 대해 CORS 검사 허용/거부 설정
                .allowedOrigins("http://192.168.0.60:3000") // 프론트 주소 허용하는 주소
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*") // 헤더허용 *은 모든걸 의미
                .allowCredentials(true); // 쿠키 허용!
    }
}

