package back.service.announcement;

import java.io.IOException;
import java.util.List;

import back.model.announcement.Announcement;

public interface AnnouncementService {
	
	public List<Announcement> getAnnouncementList(Announcement announcement);
	
    public Announcement getAnnouncementById(long annId);
    
    public boolean createAnnouncement(Announcement announcement)throws NumberFormatException, IOException;
    
    public boolean updateAnnouncement(Announcement announcement)throws NumberFormatException, IOException;
    
    public boolean deleteAnnouncement(Announcement announcement);
    
}
