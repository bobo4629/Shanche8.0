package bobo.shanche.bean;

import java.util.List;

/**
 * Created by bobo1 on 2017/2/11.
 */

public class BeanDetailLineSiteInfo {
    private String LineId;
    private int UpDown;
    private String SiteId;
    private int Seq;
    private String SiteName;
    private float Longitude;
    private float Latitude;
    private List<String> BusList;

    public List<String> getBusList() {
        return BusList;
    }

    public float getLatitude() {
        return Latitude;
    }

    public String getLineId() {
        return LineId;
    }

    public float getLongitude() {
        return Longitude;
    }

    public int getSeq() {
        return Seq;
    }

    public String getSiteId() {
        return SiteId;
    }

    public String getSiteName() {
        return SiteName;
    }

    public int getUpDown() {
        return UpDown;
    }


}
