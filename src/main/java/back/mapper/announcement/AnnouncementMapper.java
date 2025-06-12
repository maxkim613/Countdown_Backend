package back.mapper.announcement;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import back.model.announcement.Announcement;

@Mapper	
public interface AnnouncementMapper {
	
	 	public List<Announcement> getAnnouncementList(Announcement announcement);
	 	
	 	public int getTotalAnnouncementCount(Announcement announcement);
	 	
	    public Announcement getAnnouncementById(long annId);

	    public int create(Announcement announcement);

	    public int update(Announcement announcement);

	    public int delete(Announcement announcement);

}
