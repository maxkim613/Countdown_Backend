package back.service.util;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;

    public EmailServiceImpl(JavaMailSender mailSender) {
    	
        this.mailSender = mailSender;
    }

    @Override
    public void sendCertiEmail(String email, String certiNum) {
    	
        SimpleMailMessage message = new SimpleMailMessage();
        
        message.setTo(email);
        
        message.setSubject("[비밀번호 재설정] 인증번호 안내");
        
        message.setText("[비밀번호 재설정] 인증번호는 " + certiNum + " 입니다.");

        mailSender.send(message);
        
        System.out.println("[EmailService] 이메일 전송 완료: " + email);
    }
}
