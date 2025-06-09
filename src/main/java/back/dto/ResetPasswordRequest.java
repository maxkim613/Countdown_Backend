package back.dto;

import lombok.Data;

@Data
public class ResetPasswordRequest {
	
    private String userId;
    
    private String password;
}
