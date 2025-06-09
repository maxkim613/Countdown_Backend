package back.service.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import back.mapper.CertiMapper;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
public class CertiServiceImpl implements CertiService {

    @Autowired
    private CertiMapper certiMapper;

    @Override
    public String generateCertiNum() {
    	
        int num = (int)(Math.random() * 900000) + 100000; // 6자리 랜덤
        
        return String.valueOf(num);
    }

    @Override
    public void saveCertiInfo(String username, String email, String certiNum) {
    	
        String uuid = UUID.randomUUID().toString();
        
        certiMapper.insertCertiInfo(uuid, username, email, certiNum);
    }

    @Override
    public boolean verifyCerti(String email, String inputCertiNum) {
    	
        String savedNum = certiMapper.findCertiNumByEmail(email);
        
        return savedNum != null && savedNum.equals(inputCertiNum);
    }
}
