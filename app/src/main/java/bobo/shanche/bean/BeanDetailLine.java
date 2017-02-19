package bobo.shanche.bean;

import android.support.annotation.NonNull;

import java.util.List;

/**
 * Created by bobo1 on 2017/1/15.
 */

public class BeanDetailLine {
    private String LineId;
    private String LineName;
    private int UpDown;
    private List<BeanDetailLineSiteInfo> List;

    @NonNull
    public List<BeanDetailLineSiteInfo> getList() {
        return List;
    }

    public String getLineId() {
        return LineId;
    }

    public String getLineName() {
        return LineName;
    }

    public int getUpDown() {
        return UpDown;
    }


}
