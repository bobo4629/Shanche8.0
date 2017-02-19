package bobo.shanche.base;

import android.app.Application;
import android.content.Context;

import com.tendcloud.tenddata.TCAgent;


public class BaseApp extends Application {
    private static BaseApp instance;


    public static Context getAppContext() {
        return instance;
    }

    @Override
    public void onCreate() {
        instance = this;
        super.onCreate();
        TCAgent.LOG_ON=true;
        TCAgent.init(this);
        TCAgent.setReportUncaughtExceptions(true);
    }
}
