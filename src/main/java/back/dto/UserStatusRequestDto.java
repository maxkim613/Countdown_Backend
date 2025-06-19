package back.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor  // 기본 생성자
@AllArgsConstructor // 모든 필드 생성자
public class UserStatusRequestDto {

    private String userId;

    private String status;

	public String getAdminId() {
		// TODO Auto-generated method stub
		return null;
	} 
}
