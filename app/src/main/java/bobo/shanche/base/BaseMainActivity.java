package bobo.shanche.base;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;


import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import bobo.shanche.R;

/**
 * Created by bobo1 on 2017/1/29.
 */

public class BaseMainActivity extends AppCompatActivity {

    //申请组内的一个权限可获得整个权限组的权限
    private String[] mNeedPermissions ={Manifest.permission.CAMERA,Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_PHONE_STATE};
    private ArrayList<String> mPermissions = new ArrayList<>();

    private AlertDialog dialog;

    private final int RESULT_CODE_REQUEST = 52032;
    private final int RESULT_CODE_SETTINGS = 32520;

    private static Boolean mIsExit = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            for(String permission : mNeedPermissions){
                int i = ContextCompat.checkSelfPermission(this,permission);
                if(i != PackageManager.PERMISSION_GRANTED){
                    mPermissions.add(permission);
                }
            }

            if (!mPermissions.isEmpty()){
                showDialogTipRequestPermissions();
            }
        }
    }

    private void showDialogTipRequestPermissions(){
        dialog = new AlertDialog.Builder(this)
                .setTitle("缺少必要权限")
                .setMessage("由于定位SDK需要，请在点击设置后允许权限的申请，\n否则无法正常使用本应用。" +
                        "\n拍照权限为反馈 添加截图需要 \n需要时才开启 不影响正常使用")
                .setPositiveButton("设置", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        startRequestPermissions();
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finish();
                    }
                }).setCancelable(false).show();
    }

    private void showDialogTipGoToSettings(){
        dialog = new AlertDialog.Builder(this)
                .setTitle("缺少必要权限")
                .setMessage("请在-应用设置-权限-中，允许本应用的所有权限。")
                .setPositiveButton("设置", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        goToSettings();
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finish();
                    }
                }).setCancelable(false).show();
    }


    private void startRequestPermissions(){
        ActivityCompat.requestPermissions(this, mPermissions.toArray(new String[mPermissions.size()]), RESULT_CODE_REQUEST);
    }

    private void goToSettings(){
        Intent intent = new Intent();
        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", getPackageName(), null);
        intent.setData(uri);
        startActivityForResult(intent, RESULT_CODE_SETTINGS);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == RESULT_CODE_REQUEST){
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                for (int i = 0;i<grantResults.length;i++){
                    if(grantResults[i] != PackageManager.PERMISSION_GRANTED){
                        if(shouldShowRequestPermissionRationale(permissions[i])){
                            showDialogTipGoToSettings();
                        }else {
                            finish();
                        }

                    }
                }
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == RESULT_CODE_SETTINGS){
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                boolean isDone = true;
                for (String permission : mNeedPermissions){
                    if(ContextCompat.checkSelfPermission(this,permission) != PackageManager.PERMISSION_GRANTED){
                        isDone = false;
                    }
                }
                if (!isDone){
                    showDialogTipGoToSettings();
                }else {
                    if(dialog != null && dialog.isShowing()){
                        dialog.dismiss();
                    }
                }
            }
        }
    }
    //双击退出
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if(keyCode == KeyEvent.KEYCODE_BACK)
        {
            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            if(drawer.isDrawerOpen(GravityCompat.START)) {
                drawer.closeDrawer(GravityCompat.START, true);
            }
            else {
                exitBy2Click();
            }
        }
        return false;
    }
    private void exitBy2Click() {
        Timer tExit ;
        if (!mIsExit) {
            mIsExit = true; // 准备退出
            CoordinatorLayout coordinatorLayout = (CoordinatorLayout)findViewById(R.id.coordinatorLayout_main);
            Snackbar.make(coordinatorLayout,"再按一次退出程序",Snackbar.LENGTH_SHORT).show();
            tExit = new Timer();
            tExit.schedule(new TimerTask() {
                @Override
                public void run() {
                    mIsExit = false; // 取消退出
                }
            }, 2000); // 如果2秒钟内没有按下返回键，则启动定时器取消掉刚才执行的任务

        } else {
            finish();
            System.exit(0);
        }
    }

}
