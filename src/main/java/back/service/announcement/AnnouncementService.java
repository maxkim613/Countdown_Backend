package back.service.announcement;

import java.io.IOException;
import java.util.List;

import back.model.announcement.Announcement;

public interface AnnouncementService {
	public List getAnnouncementBoardList(Announcement announcement);
	
    public Announcement getAnnouncementById(String announcementId);
    
    public boolean createannBoard(Announcement announcement)throws NumberFormatException, IOException;
    
    public boolean updateannBoard(Announcement announcement)throws NumberFormatException, IOException;
    
    public boolean deleteannBoard(Announcement announcement);
    
}
