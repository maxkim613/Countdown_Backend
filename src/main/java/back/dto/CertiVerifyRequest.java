package back.dto;

import lombok.Data;

@Data
public class CertiVerifyRequest {
	
    private String email;
    
    private String certiNum;
}
