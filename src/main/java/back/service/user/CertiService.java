package back.service.user;


public interface CertiService {
	
	String generateCertiNum();
	
	void saveCertiInfo(String username, String email, String certiNum);
	
	boolean verifyCerti(String email, String certiNum);
	
}
