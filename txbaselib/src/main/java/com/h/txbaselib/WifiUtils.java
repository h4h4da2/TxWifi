package com.h.txbaselib;

import static android.content.Context.WIFI_SERVICE;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkRequest;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.NetworkUtils;
import com.blankj.utilcode.util.Utils;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class WifiUtils {

    private static WifiUtils utils = null;

    public WifiUtils() {

    }


    /**
     * wifi是否打开
     *
     * @return
     */
    public boolean isWifiEnable() {
        boolean isEnable = false;
        WifiManager wifiManager = getWifiManager();
        if (wifiManager != null) {
            if (wifiManager.isWifiEnabled()) {
                isEnable = true;
            }
        }
        return isEnable;
    }

    /**
     * 打开WiFi
     */
    public void openWifi() {
        WifiManager wifiManager = getWifiManager();
        if (wifiManager != null && !isWifiEnable()) {
            wifiManager.setWifiEnabled(true);
        }
    }

    /**
     * 关闭WiFi
     */
    public void closeWifi() {
        WifiManager wifiManager = getWifiManager();
        if (wifiManager != null && isWifiEnable()) {
            wifiManager.setWifiEnabled(false);
        }
    }

    /**
     * 获取WiFi列表
     *
     * @return
     */
    public List<ScanResult> getWifiList() {
        WifiManager wifiManager = getWifiManager();
        List<ScanResult> resultList = new ArrayList<>();
        if (wifiManager != null && isWifiEnable()) {
            resultList.addAll(wifiManager.getScanResults());
        }
        return resultList;
    }

    /**
     * 有密码连接
     *
     * @param ssid
     * @param pws
     */
    public void connectWifiPws(String ssid, String pws) {
        WifiManager wifiManager = getWifiManager();
        wifiManager.disableNetwork(wifiManager.getConnectionInfo().getNetworkId());
        int netId = wifiManager.addNetwork(getWifiConfig(ssid, pws, true));
        wifiManager.enableNetwork(netId, true);
    }

    /**
     * 无密码连接
     *
     * @param ssid
     */
    public void connectWifiNoPws(String ssid) {
        WifiManager wifiManager = getWifiManager();
        wifiManager.disableNetwork(wifiManager.getConnectionInfo().getNetworkId());
        int netId = wifiManager.addNetwork(getWifiConfig(ssid, "", false));
        wifiManager.disconnect();
        wifiManager.enableNetwork(netId, true);
        wifiManager.reconnect();
    }

    /**
     * wifi设置
     *
     * @param ssid
     * @param pws
     * @param isHasPws
     */
    private WifiConfiguration getWifiConfig(String ssid, String pws, boolean isHasPws) {

        WifiManager wifiManager = getWifiManager();
        WifiConfiguration config = new WifiConfiguration();
        config.allowedAuthAlgorithms.clear();
        config.allowedGroupCiphers.clear();
        config.allowedKeyManagement.clear();
        config.allowedPairwiseCiphers.clear();
        config.allowedProtocols.clear();
        config.SSID = "\"" + ssid + "\"";

//        WifiConfiguration tempConfig = isExist(ssid);
//        if (tempConfig != null) {
//            wifiManager.removeNetwork(tempConfig.networkId);
//        }
        if (isHasPws) {
            config.preSharedKey = "\"" + pws + "\"";
            config.hiddenSSID = true;
            config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
            config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
            config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
            config.status = WifiConfiguration.Status.ENABLED;
        } else {
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
        }
        return config;
    }

    /**
     * 得到配置好的网络连接
     *
     * @param ssid
     * @return
     */
    private static WifiConfiguration isExist(String ssid) {
        WifiManager wifiManager = ((WifiManager) Utils.getApp().getApplicationContext().getSystemService(WIFI_SERVICE));
        if (wifiManager == null) return null;
        List<WifiConfiguration> configs = wifiManager.getConfiguredNetworks();
        for (WifiConfiguration config : configs) {
            if (config.SSID.equals("\"" + ssid + "\"")) {
                return config;
            }
        }
        return null;
    }


    /**
     * 获取当前连接的wifi名称
     *
     * @param context
     * @return
     */
    public static String getWIFIName(Context context) {
        WifiManager wifiMgr = (WifiManager) Utils.getApp().getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        int wifiState = wifiMgr.getWifiState();
        WifiInfo info = wifiMgr.getConnectionInfo();
        String wifiId = info != null ? info.getSSID().replace("\"", "") : null;
        return wifiId;
    }


    public static void test() {
        ConnectivityManager cm = (ConnectivityManager) (Utils.getApp().getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE));
        if (cm != null) {
            NetworkRequest.Builder builder = new NetworkRequest.Builder();
            NetworkRequest request = builder.build();
            cm.registerNetworkCallback(request, new ConnectivityManager.NetworkCallback() {
                @Override
                public void onAvailable(Network network) {
                    super.onAvailable(network);

                    LogUtils.d("onAvailable");
                }

                @Override
                public void onUnavailable() {
                    super.onUnavailable();
                    LogUtils.d("onUnavailable");
                }
            });

        }
    }

    public static void test2(String networkSSID) {

        WifiConfiguration conf = new WifiConfiguration();
        conf.allowedAuthAlgorithms.clear();
        conf.allowedGroupCiphers.clear();
        conf.allowedKeyManagement.clear();
        conf.allowedPairwiseCiphers.clear();
        conf.allowedProtocols.clear();
        conf.SSID = "\"" + networkSSID + "\"";   //ssid must be in quotes
        conf.wepKeys[0] = "";
        conf.wepTxKeyIndex = 0;
        conf.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
//        conf.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
//        conf.preSharedKey = "\""+ networkPass +"\"";
//        conf.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);

        WifiManager wifiManager = getWifiManager();
        wifiManager.addNetwork(conf);

        List<WifiConfiguration> list = wifiManager.getConfiguredNetworks();
        for (WifiConfiguration i : list) {
            if (i.SSID != null && i.SSID.equals("\"" + networkSSID + "\"")) {
                wifiManager.disconnect();
                wifiManager.enableNetwork(i.networkId, true);
                wifiManager.reconnect();
                break;
            }
        }
    }

    public static void disconnect() {

        String ssid = NetworkUtils.getSSID();


        try {
            WifiManager wifiManager = getWifiManager();

            List<WifiConfiguration> list = wifiManager.getConfiguredNetworks();
            for (WifiConfiguration i : list) {
                //int networkId = wifiManager.getConnectionInfo().getNetworkId();
                wifiManager.removeNetwork(i.networkId);
                wifiManager.saveConfiguration();
                Method method = wifiManager.getClass().getDeclaredMethod("forget", int.class, OnStartTetheringCallbackClass());
                if (method != null) {
                    try {
                        method.setAccessible(true);
                        method.invoke(wifiManager, i.networkId, null);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                } else {
                    LogUtils.d("forget methond is null");
                }

            }
        } catch (Throwable e) {
            e.printStackTrace();
        }


        try {
            WifiConfiguration tempConfig = isExist(ssid);
            if (tempConfig != null) {
                getWifiManager().removeNetwork(tempConfig.networkId);
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }

        getWifiManager().disconnect();

        NetworkUtils.setWifiEnabled(false);



    }

    public static WifiManager getWifiManager() {
        return ((WifiManager) (Utils.getApp().getApplicationContext().getSystemService(Context.WIFI_SERVICE)));
    }


    private static Class OnStartTetheringCallbackClass() {
        try {
            return Class.forName("android.net.wifi.WifiManager$ActionListener");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }


}
