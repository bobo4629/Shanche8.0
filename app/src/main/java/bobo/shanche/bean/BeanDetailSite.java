package bobo.shanche.bean;

import java.util.List;

/**
 * Created by bobo1 on 2017/1/14.
 */

public class BeanDetailSite {
    private String Id;
    private String LineName;

    public String getDownStartTime() {
        return DownStartTime;
    }

    public String getId() {
        return Id;
    }

    public String getLineName() {
        return LineName;
    }

    public String getDownEndTime() {
        return DownEndTime;
    }

    public int getFare() {
        return Fare;
    }

    public int getExtend() {
        return Extend;
    }

    public List<BeanSiteInfo> getStartEndSites() {
        return StartEndSites;
    }

    private String DownStartTime;
    private String DownEndTime;
    private int Fare;
    private int Extend;
    private List<BeanSiteInfo> StartEndSites;
}
