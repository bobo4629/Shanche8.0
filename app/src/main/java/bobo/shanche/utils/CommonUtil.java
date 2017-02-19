package bobo.shanche.utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import java.net.URLEncoder;
import java.util.List;

import bobo.shanche.base.BaseApp;
import bobo.shanche.bean.BeanSiteInfo;

/**
 * Created by bobo1 on 2017/2/4.
 */

public class CommonUtil {

    private static double PI = 3.1415926535897932384626;
    private static double a = 6378245.0;
    private static double ee = 0.00669342162296594323;
    private static int UP = 1;
    private static int DOWM = 2;


    public static boolean openAlipayPayPage(Context context) {
        return openAlipayPayPage(context, "https://qr.alipay.com/a6x01363mqhor6we8wezm9b");
    }

    public static boolean openAlipayPayPage(Context context, String qrcode) {
        try {
            //https%3A%2F%2Fqr.alipay.com%2Fap9meauipfitn4t148
            qrcode = URLEncoder.encode(qrcode, "utf-8");
        } catch (Exception e) {
        }
        try {
            final String alipayqr = "alipayqr://platformapi/startapp?saId=10000007&clientVersion=3.7.0.0718&qrcode=" + qrcode;
            Uri uri = Uri.parse(alipayqr + "%3F_s%3Dweb-other&_t=" + System.currentTimeMillis());
            Intent intent = new Intent(Intent.ACTION_VIEW,uri);
            context.startActivity(intent);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static void closeKeyboard(View view) {
        InputMethodManager imm = (InputMethodManager) BaseApp.getAppContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
    public static String getStartSiteName (int upDown,List<BeanSiteInfo> mainBus) {
        String str = "";
        for (int i = 0; i < mainBus.size(); i++)
            if (mainBus.get(i).getUpDown() == upDown)
                if (mainBus.get(i).getSeq() == 1)
                    str = mainBus.get(i).getSiteName();
        //找不到对应的车站名 判断是否有另一方向
        if(str.equals("")) {
            boolean isExistAnotherSide = false;
            for (BeanSiteInfo info : mainBus) {
                if (info.getUpDown() != upDown) {
                    isExistAnotherSide = true;
                }
            }
            if (isExistAnotherSide) {
                str = getStartSiteName(upDown == UP ? DOWM : UP, mainBus);
            } else {
                str = "该公交线路仅单向经过该站。";
            }
        }
        return str;
    }
    public static String getEndSiteName (int upDown,List<BeanSiteInfo> mainBus){
        String str = "";
        for (int i = 0; i < mainBus.size(); i++)
            if (mainBus.get(i).getUpDown() == upDown)
                if (mainBus.get(i).getSeq() > 1) {
                    str = mainBus.get(i).getSiteName();
                }
        //找不到对应的车站名 判断是否有另一方向
        if(str.equals("")){
            boolean isExistAnotherSide = false;
            for(BeanSiteInfo info : mainBus){
                if( info.getUpDown() != upDown){
                    isExistAnotherSide = true;
                }
            }
            if(isExistAnotherSide){
                str = getEndSiteName(upDown == UP ? DOWM : UP,mainBus);
            }else {
                str = "该公交线路仅单向经过该站。";
            }
        }
        return str;
    }



    public static Double[] GCJ02ToWGS84(Double gcj_lon,Double gcj_lat){
        if(outOfChina(gcj_lon, gcj_lat)){
            return new Double[]{gcj_lon,gcj_lat};
        }
        double dlat = transformlat(gcj_lon - 105.0, gcj_lat - 35.0);
        double dlng = transformlng(gcj_lon - 105.0, gcj_lat - 35.0);
        double radlat = gcj_lat / 180.0 * PI;
        double magic = Math.sin(radlat);
        magic = 1 - ee * magic * magic;
        double sqrtmagic = Math.sqrt(magic);
        dlat = (dlat * 180.0) / ((a * (1 - ee)) / (magic * sqrtmagic) * PI);
        dlng = (dlng * 180.0) / (a / sqrtmagic * Math.cos(radlat) * PI);
        double mglat = gcj_lat + dlat;
        double mglng = gcj_lon + dlng;
        return new Double[]{gcj_lon * 2 - mglng, gcj_lat * 2 - mglat};
    }

    private static Double transformlat(double lng, double lat) {
        double ret = -100.0 + 2.0 * lng + 3.0 * lat + 0.2 * lat * lat + 0.1 * lng * lat + 0.2 * Math.sqrt(Math.abs(lng));
        ret += (20.0 * Math.sin(6.0 * lng * PI) + 20.0 * Math.sin(2.0 * lng * PI)) * 2.0 / 3.0;
        ret += (20.0 * Math.sin(lat * PI) + 40.0 * Math.sin(lat / 3.0 * PI)) * 2.0 / 3.0;
        ret += (160.0 * Math.sin(lat / 12.0 * PI) + 320 * Math.sin(lat * PI / 30.0)) * 2.0 / 3.0;
        return ret;
    }

    private static Double transformlng(double lng,double lat) {
        double ret = 300.0 + lng + 2.0 * lat + 0.1 * lng * lng + 0.1 * lng * lat + 0.1 * Math.sqrt(Math.abs(lng));
        ret += (20.0 * Math.sin(6.0 * lng * PI) + 20.0 * Math.sin(2.0 * lng * PI)) * 2.0 / 3.0;
        ret += (20.0 * Math.sin(lng * PI) + 40.0 * Math.sin(lng / 3.0 * PI)) * 2.0 / 3.0;
        ret += (150.0 * Math.sin(lng / 12.0 * PI) + 300.0 * Math.sin(lng / 30.0 * PI)) * 2.0 / 3.0;
        return ret;
    }


    private static boolean outOfChina(Double lng,Double lat) {
        return (lng < 72.004 || lng > 137.8347) || ((lat < 0.8293 || lat > 55.8271) || false);
    }
}
