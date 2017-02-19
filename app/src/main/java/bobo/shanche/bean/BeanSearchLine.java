package bobo.shanche.bean;

import java.util.List;

/**
 * Created by bobo1 on 2017/1/11.
 */

public class BeanSearchLine {
    public String getId() {
        return Id;
    }

    public String getLineName() {
        return LineName;
    }

    public String getDownStartTime() {
        return DownStartTime;
    }

    public String getDownEndTime() {
        return DownEndTime;
    }

    public int getFare() {
        return Fare;
    }

    public List<BeanSiteInfo> getStartEndSites() {
        return StartEndSites;
    }

    private String Id;
    private String LineName;
    private String DownStartTime;
    private String DownEndTime;
    private int Fare;
    private List<BeanSiteInfo> StartEndSites;
}
