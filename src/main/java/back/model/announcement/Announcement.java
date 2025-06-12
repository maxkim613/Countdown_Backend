package back.model.announcement;

import com.fasterxml.jackson.annotation.JsonProperty;

import back.model.Model;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;



@Data
@NoArgsConstructor // 기본 생성자 자동 생성
@AllArgsConstructor // 모든 필드를 매개변수로 하는 생성자 자동 생성
@EqualsAndHashCode(callSuper = true)
public class Announcement extends Model {

    private String searchText;
    private String startDate;
    private String endDate;

    private long annId;
    
    private String annTitle;
    private String annContent;

    private String createId;
    private String updateId;
    private String createDt;
    private String updateDt;
    
    private int rn;
    private int startRow;
    private int endRow;
    private int page = 1;
    private int size = 10;
    private int totalCount;
    private int totalPages;

    private String sortField = "CREATE_DT";
    private String sortOrder = "DESC";
}