package bobo.shanche.bean;

/**
 * Created by bobo1 on 2017/2/13.
 */

public class BeanUpdate {
    private String version;
    private String changelog;

    public String getInstallUrl() {
        return installUrl;
    }

    public String getChangelog() {
        return changelog;
    }

    public String getVersion() {
        return version;
    }

    private String installUrl;
}
