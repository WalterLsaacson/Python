package com.vivo.statsany;

import android.Manifest;
import android.app.AppOpsManager;
import android.app.usage.NetworkStats;
import android.app.usage.NetworkStatsManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.RemoteException;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private static final long fiveSeconds = 5 * 1000;/*30 mins*/
    private static final long thirdSeconds = 30 * 1000;/*30 mins*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (!hasPermissionToReadNetworkStats()) {
            requestReadNetworkStats();
        }
        final NetworkStatsManager networkStatsManager = (NetworkStatsManager) getSystemService(NETWORK_STATS_SERVICE);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(fiveSeconds);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                try {
                    specificApp(networkStatsManager,"com.xiaomi.market");
                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }).start();
        findViewById(R.id.hello).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NetworkStats.Bucket bucket = getAllBytesWifi(networkStatsManager);
                //这里可以区分发送和接收
                String uploadString = Utils.getPrintSize(bucket.getTxBytes());
                String downloadString = Utils.getPrintSize(bucket.getRxBytes());
                Log.d(TAG, "----------wifi-------" );
                Log.d(TAG, "getAllBytesWifi: upload " + uploadString);
                Log.d(TAG, "getAllBytesWifi: download " + downloadString);
                 bucket = getAllBytesMobile(networkStatsManager);
                //这里可以区分发送和接收
                 uploadString = Utils.getPrintSize(bucket.getTxBytes());
                 downloadString = Utils.getPrintSize(bucket.getRxBytes());
                Log.d(TAG, "----------mobile-------" );
                Log.d(TAG, "getAllBytesWifi: upload " + uploadString);
                Log.d(TAG, "getAllBytesWifi: download " + downloadString);
                //allApp(networkStatsManager);
                try {
                    specificApp(networkStatsManager,"com.xiaomi.market");
                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                }
            }
        });
    }
    private void allApp(NetworkStatsManager networkStatsManager){
        PackageManager packageManager = getPackageManager();
        final List<PackageInfo> applicationInfoList = packageManager.getInstalledPackages(0);
        int uid;
        NetworkStats.Bucket bucket;
        for(PackageInfo info : applicationInfoList) {
            try {
                PackageInfo pack = packageManager.getPackageInfo(info.packageName,PackageManager.GET_PERMISSIONS);
                String[] requestedPermissions = pack.requestedPermissions;
                if (requestedPermissions == null)
                    continue;
                if (info.applicationInfo.uid == 1000)
                    continue;
                for (String str : requestedPermissions) {
                    if (str.equals("android.permission.INTERNET")) {
                        uid = getUidByPackageName(this,info.packageName);
                        //这里可以区分发送和接收
                         bucket = getWifiBucket(networkStatsManager,uid);
                        String uploadString = Utils.getPrintSize(bucket.getTxBytes());
                        String downloadString = Utils.getPrintSize(bucket.getRxBytes());
                        Log.d(TAG, "-------------------------------");
                        Log.d(TAG, "allApp: " + info.packageName);
                        Log.d(TAG, "----------wifi-------" );
                        Log.d(TAG, "getAllBytesWifi: upload " + uploadString);
                        Log.d(TAG, "getAllBytesWifi: download " + downloadString);
                        bucket = getMobileBucket(networkStatsManager,uid);
                        //这里可以区分发送和接收
                        uploadString = Utils.getPrintSize(bucket.getTxBytes());
                        downloadString = Utils.getPrintSize(bucket.getRxBytes());
                        Log.d(TAG, "----------mobile-------" );
                        Log.d(TAG, "getAllBytesWifi: upload " + uploadString);
                        Log.d(TAG, "getAllBytesWifi: download " + downloadString);
                        break;
                    }
                }
            } catch (PackageManager.NameNotFoundException exception) {
                exception.printStackTrace();
            }
        }
    }
    private void specificApp(NetworkStatsManager networkStatsManager,String pkgName) throws PackageManager.NameNotFoundException {
        PackageManager packageManager = getPackageManager();
        final List<PackageInfo> applicationInfoList = packageManager.getInstalledPackages(0);
        int uid;
        NetworkStats.Bucket bucket;
                PackageInfo pack = packageManager.getPackageInfo(pkgName,PackageManager.GET_PERMISSIONS);
                String[] requestedPermissions = pack.requestedPermissions;
                for (String str : requestedPermissions) {
                    if (str.equals("android.permission.INTERNET")) {
                        uid = getUidByPackageName(this,pkgName);
                        //这里可以区分发送和接收
                        bucket = getWifiBucket(networkStatsManager,uid);
                        String uploadString = Utils.getPrintSize(bucket.getTxBytes());
                        String downloadString = Utils.getPrintSize(bucket.getRxBytes());
                        Log.d(TAG, "-------------------------------");
                        Log.d(TAG, "allApp: " + pkgName);
                        Log.d(TAG, "----------wifi-------" );
                        Log.d(TAG, "getAllBytesWifi: upload " + uploadString);
                        Log.d(TAG, "getAllBytesWifi: download " + downloadString);
                        bucket = getMobileBucket(networkStatsManager,uid);
                        //这里可以区分发送和接收
                        uploadString = Utils.getPrintSize(bucket.getTxBytes());
                        downloadString = Utils.getPrintSize(bucket.getRxBytes());
                        Log.d(TAG, "----------mobile-------" );
                        Log.d(TAG, "getAllBytesWifi: upload " + uploadString);
                        Log.d(TAG, "getAllBytesWifi: download " + downloadString);
                        break;
                    }
                }
    }

    private NetworkStats.Bucket getWifiBucket(NetworkStatsManager networkStatsManager, int uid) {
        NetworkStats networkStats;
        NetworkStats.Bucket bucket = new NetworkStats.Bucket();
        networkStats = networkStatsManager.queryDetailsForUid(ConnectivityManager.TYPE_WIFI,
                    "",
                System.currentTimeMillis() - thirdSeconds,
                    System.currentTimeMillis(),
                    uid);
        networkStats.getNextBucket(bucket);
        return bucket;
    }

    private NetworkStats.Bucket getMobileBucket(NetworkStatsManager networkStatsManager, int uid) {
        NetworkStats networkStats;
        NetworkStats.Bucket bucket = new NetworkStats.Bucket();
        networkStats = networkStatsManager.queryDetailsForUid(ConnectivityManager.TYPE_MOBILE,
                getSubscriberId(this, ConnectivityManager.TYPE_MOBILE),
                System.currentTimeMillis() - thirdSeconds,
                System.currentTimeMillis(),
                uid);
        networkStats.getNextBucket(bucket);
        return bucket;
    }

    /**
     * 根据包名获取uid
     * @param context       上下文
     * @param packageName   包名
     */
    public static int getUidByPackageName(Context context, String packageName) {
        int uid = -1;
        PackageManager packageManager = context.getPackageManager();
        try {
            PackageInfo packageInfo = packageManager.getPackageInfo(packageName, PackageManager.GET_META_DATA);
            uid = packageInfo.applicationInfo.uid;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return uid;
    }

    public NetworkStats.Bucket getAllBytesWifi(NetworkStatsManager networkStatsManager) {
        NetworkStats.Bucket bucket;
        try {
            bucket = networkStatsManager.querySummaryForDevice(ConnectivityManager.TYPE_WIFI,
                    "",
                    System.currentTimeMillis() - fiveSeconds,
                    System.currentTimeMillis());
        } catch (RemoteException e) {
            return null;
        }

        return bucket;
    }

    private String getSubscriberId(Context context, int networkType) {
        if (ConnectivityManager.TYPE_MOBILE == networkType) {
            TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return "";
            }
            return tm.getSubscriberId();
        }
        return "";
    }

    /**
     * 本机使用的 mobile 总流量
     */
    public NetworkStats.Bucket getAllBytesMobile(NetworkStatsManager networkStatsManager) {
        NetworkStats.Bucket bucket;
        try {
            bucket = networkStatsManager.querySummaryForDevice(ConnectivityManager.TYPE_MOBILE,
                    getSubscriberId(this, ConnectivityManager.TYPE_MOBILE),
                    System.currentTimeMillis() - fiveSeconds,
                    System.currentTimeMillis());
        } catch (RemoteException e) {
            return null;
        }
        //这里可以区分发送和接收
        return bucket;
    }

    private boolean hasPermissionToReadNetworkStats() {
        final AppOpsManager appOps = (AppOpsManager) getSystemService(Context.APP_OPS_SERVICE);
        int mode = appOps.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS,
                android.os.Process.myUid(), getPackageName());
        if (mode == AppOpsManager.MODE_ALLOWED) {
            return true;
        }

        requestReadNetworkStats();
        return false;
    }
    // 打开“有权查看使用情况的应用”页面
    private void requestReadNetworkStats() {
        Intent intent = new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS);
        startActivity(intent);
    }
}
