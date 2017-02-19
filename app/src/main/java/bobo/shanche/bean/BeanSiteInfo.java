package bobo.shanche.bean;

import java.util.List;

/**
 * Created by bobo1 on 2017/1/11.
 */

public class BeanSiteInfo {

    private String LineId;

    public int getUpDown() {
        return UpDown;
    }

    public String getLineId() {
        return LineId;
    }

    public int getSeq() {
        return Seq;
    }

    public String getLineName() {
        return LineName;
    }

    public String getSiteName() {
        return SiteName;
    }

    public float getLongitude() {
        return Longitude;
    }

    public float getLatitude() {
        return Latitude;
    }

    public List<String> getBusList() {
        return BusList;
    }

    private int UpDown;
    private int Seq;
    private String LineName;
    private String SiteName;
    private float Longitude;
    private float Latitude;
    private List<String> BusList;
}
