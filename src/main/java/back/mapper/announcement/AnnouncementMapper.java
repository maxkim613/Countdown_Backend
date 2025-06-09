package back.mapper.announcement;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import back.model.announcement.Announcement;

@Mapper	
public interface AnnouncementMapper {
	 	public List<Announcement> getAnnouncementBoardList(Announcement announcement);
	 	
	    public Announcement getAnnouncementById(String announcementId);

	    public int annCreate(Announcement announcement);

	    public int annUpdate(Announcement announcement);

	    public int annDelete(Announcement announcement);

}
