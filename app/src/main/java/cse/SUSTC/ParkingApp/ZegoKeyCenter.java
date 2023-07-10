package cse.SUSTC.ParkingApp;

public final class ZegoKeyCenter {
    // Developers can get appID from admin console.
    // https://console.zego.im/dashboard
    // for example: 123456789L;
    private long _appID = 1539440265;

    // AppSign only meets simple authentication requirements.
    // If you need to upgrade to a more secure authentication method,
    // please refer to [Guide for upgrading the authentication mode from using the AppSign to Token](https://docs.zegocloud.com/faq/token_upgrade)
    // Developers can get AppSign from admin [console](https://console.zego.im/dashboard)
    // for example: "abcdefghijklmnopqrstuvwxyz0123456789abcdegfhijklmnopqrstuvwxyz01";
    private String _appSign = "2f3e922ad2dc0fb6f6410a1776844a55e3e3e9ddaf98cad6a7a94e208c384b51";

    private static ZegoKeyCenter instance = new ZegoKeyCenter();
    private ZegoKeyCenter() {}

    public static ZegoKeyCenter getInstance() {
        return instance;
    }

    public long getAppID() {
        return _appID;
    }

    public void setAppID(long appID) {
        _appID = appID;
    }

    public String getAppSign() {
        return _appSign;
    }

    public void setAppSign(String appSign) {
        _appSign = appSign;
    }
}
