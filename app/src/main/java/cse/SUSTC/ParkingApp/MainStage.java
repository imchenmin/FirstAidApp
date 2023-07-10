package cse.SUSTC.ParkingApp;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.media.SoundPool;
import android.os.Build;
import android.os.Bundle;
import android.telecom.Connection;
import android.util.Log;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.route.RouteSearch;
import com.amap.api.services.route.WalkPath;
import com.amap.api.services.route.WalkRouteResult;
import com.amap.api.services.route.WalkStep;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;

/**
 * This class load the main stage which has three fragment to achieve different functions
 */
enum MainStageFragments {
    USER, MAP, ORDER
}

public class MainStage extends AppCompatActivity {


    private transient FragmentTransaction transaction;
    static transient FragmentManager fragmentManager;
    static MainStageFragments currentFragment = MainStageFragments.MAP;
    private SharedPreferences sharedPreferences;
    private AMapLocationClient locationClientContinue = null;
    private WebSocket WSclient = null;
    private OkHttpClient okHttpClient;
    private SoundPool sp;


    private transient MapStage mapFrame;
    private transient OrderStage orderFrame;
    private transient UsrStage usrFrame;

    public static final String RECEIVER_ACTION = "location_in_background";
    public static final String TAG = "MainStage";
    private int recev_sound_id;



    private transient BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @SuppressLint("NonConstantResourceId")
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.NavigationMap:
                    transaction = fragmentManager.beginTransaction();
                    transaction.replace(R.id.Content, mapFrame);
                    currentFragment = MainStageFragments.MAP;
                    transaction.commit();
                    return true;
//                case R.id.NavigationOrder:
//                    transaction = fragmentManager.beginTransaction();
//                    transaction.replace(R.id.Content, orderFrame);
//                    currentFragment = MainStageFragments.ORDER;
//                    transaction.commit();
//                    return true;
                case R.id.NavigationFirstAid:
                    // 之后会变成一个界面，当下先是一个请求弹窗
                    // 声音提示

                    AlertDialog.Builder builder = new AlertDialog.Builder(MainStage.this);
                    builder.setTitle("需要帮助吗？");
                    builder.setCancelable(false);
                    builder.setMessage("您即将发起急救请求，与您直线距离500米内的经过培训的人员将会收到请求并获知您的位置，确定吗？");
                    builder.setPositiveButton("同意", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    String roomId = sentFirstAidRequest();
                                    Intent intent = new Intent(MainStage.this, WalkRouteActivity.class);
                                    WalkRouteResult walkRouteResult = null;
                                    intent.putExtra("roomId", roomId);
                                    startActivity(intent);
                                }
                            }).start();
                        }
                    });
                    builder.setNegativeButton("拒绝", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // User cancelled the dialog
                        }
                    });

// Create and show the AlertDialog
                    AlertDialog dialog = builder.create();
                    dialog.show();
                    return true;
                case R.id.NavigationUser:
                    transaction = fragmentManager.beginTransaction();
                    transaction.replace(R.id.Content, usrFrame);
                    currentFragment = MainStageFragments.USER;
                    transaction.commit();
                    return true;
            }
            return false;
        }
    };

    public String sentFirstAidRequest() {
        // 发送急救请求
        String access_token = sharedPreferences.getString("access_token","");
        if (access_token.equals("")) {
            return "";
        }
//        int play_ret = sp.play(recev_sound_id, 50.0f, 50.0f, 1, 0, 0.8f);
        OkHttpClient client = new OkHttpClient();
        MediaType mediaType = MediaType.parse("application/json; charset=utf-8");
        String json = String.format("{\"aid_type\": \"%s\", \"description\": \"%s\"}", "testing", "testing");

        RequestBody requestBody = RequestBody.create(mediaType, json);
        Request request = new Request.Builder()
                .url(MyApplication.URL + "/first_aid/")
                .addHeader("Authorization", "Bearer " + access_token)
                .addHeader("accept", "application/json")
                .addHeader("Content-Type", "application/x-www-form-urlencoded")
                .post(requestBody)
                .build();
        String roomId = "";
        try (Response response = client.newCall(request).execute()) {
            String responseBody = response.body().string();
            JSONObject responseJson = new JSONObject(responseBody);
            roomId = "first_aid_room_" + responseJson.getString("id");
            Log.d(TAG, "first aid onClick: " + responseBody);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return roomId;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_stage);

        mapFrame = new MapStage();
        mapFrame.setArguments(savedInstanceState);
        orderFrame = new OrderStage();
        usrFrame = new UsrStage();

        sharedPreferences = getSharedPreferences("userInfo", Context.MODE_PRIVATE);

        SoundPool.Builder spb = new SoundPool.Builder();
        spb.setMaxStreams(10);
//        spb.setAudioAttributes(AudioAttributes.CONTENT_TYPE_MUSIC);    //转换音频格式
        sp = spb.build();      //创建SoundPool对象
        recev_sound_id = sp.load(MainStage.this, R.raw.rece_notification, 1);
        setCurrentFragment();
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.MainMenu);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);


        if (Build.VERSION.SDK_INT >= 23) {
            String[] permissions = {
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.READ_PHONE_STATE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.CAMERA,
                    Manifest.permission.ACCESS_BACKGROUND_LOCATION,
                    Manifest.permission.RECORD_AUDIO
            };

            if (checkSelfPermission(permissions[0]) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(permissions, 0);
            }
        }
        // 调用后台定位服务
//        IntentFilter intentFilter = new IntentFilter();
//        intentFilter.addAction(RECEIVER_ACTION);
//        registerReceiver(locationChangeBroadcastReceiver, intentFilter);
//        startService();


    }
    /**
     * 启动或者关闭定位服务
     *
     */
    public void startService() {
        startLocationService();
        LocationStatusManager.getInstance().resetToInit(getApplicationContext());
    }
    public void stopService() {
        stopLocationService();
        LocationStatusManager.getInstance().resetToInit(getApplicationContext());
    }
    private Connection mLocationServiceConn = null;

    /**
     * 开始定位服务
     */
    private void startLocationService(){
        getApplicationContext().startService(new Intent(this, LocationService.class));
    }
    void startContinueLocation(){
        if(null == locationClientContinue){
            try {
                locationClientContinue = new AMapLocationClient(getApplicationContext());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        //使用连续的定位方式  默认连续
        AMapLocationClientOption locationClientOption = new AMapLocationClientOption();
        // 地址信息
        locationClientOption.setNeedAddress(true);
        locationClientOption.setInterval(5000);
        locationClientContinue.setLocationOption(locationClientOption);
        locationClientContinue.setLocationListener(locationListener);
        locationClientContinue.startLocation();
    }

    AMapLocationListener locationListener = new AMapLocationListener() {
        @Override
        public void onLocationChanged(AMapLocation location) {
//            if (location.getLatitude() == 0.00 && location.getLongitude() == 0.00) {
//                return;
//            }
            long callBackTime = System.currentTimeMillis();
            StringBuffer sb = new StringBuffer();
            JSONObject jsonObject = location.toJson(1);
            try {
                jsonObject.put("msg_type","location");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            String msg_string = jsonObject  == null ? null : jsonObject.toString();
            WSclient.send(msg_string);
            sb.append("定位完成\n");
            if(null == location){
                sb.append("定位失败：location is null!!!!!!!");
            } else {
                sb.append(location.toStr());
            }
            Log.d("TAG", "onLocationChanged: " + sb.toString());
        }
    };
    /**
     * 停止连续客户端
     */
    void stopContinueLocation(){
        if(null != locationClientContinue){
            locationClientContinue.stopLocation();
        }
    }


    /**
     * 关闭服务
     * 先关闭守护进程，再关闭定位服务
     */
    private void stopLocationService(){
        sendBroadcast(Utils.getCloseBrodecastIntent());
    }

    private BroadcastReceiver locationChangeBroadcastReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Log.d(TAG, "GPS onReceive: ");
            if (action.equals(RECEIVER_ACTION)) {
                String locationResult = intent.getStringExtra("result");
                if (null != locationResult && !locationResult.trim().equals("")) {
                    Log.d(TAG, "GPS onReceive: " + locationResult);
                }
            }
        }
    };
    @Override
    protected void onStart() {
        super.onStart();

    }

    private void setCurrentFragment() {
        Fragment dstFrag;

        switch (currentFragment) {
            case MAP:
                dstFrag = mapFrame;
                break;
            case USER:
                dstFrag = usrFrame;
                break;
            case ORDER:
                dstFrag = orderFrame;
                break;
            default:
                dstFrag = mapFrame;
        }
        fragmentManager = getSupportFragmentManager();
        transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.Content, dstFrag);
        transaction.commit();
    }


    @Override
    protected void onResume() {
        super.onResume();
        startContinueLocation();
        okHttpClient=new OkHttpClient();
        String token = sharedPreferences.getString("access_token","");
        Request request=new Request
                .Builder().url("ws://47.113.221.224:8012/ws?token="+token).build(); // TODO config
        WSclient = okHttpClient.newWebSocket(request, new Listener());
        setCurrentFragment();
    }

    @Override
    protected void onPause() {
        super.onPause();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        WSclient.close(0,"normal");
    }
    public String assembleChatInfo(JSONObject responseObject) throws JSONException {
        String roomID = responseObject.getString("room_id");
        return roomID;
    }
    public WalkRouteResult assembleWalkRouteResult(JSONObject responseObject) throws JSONException {
        JSONObject routePathJSON = responseObject.getJSONObject("route");
        JSONArray routeStepJSONArray = routePathJSON.getJSONArray("steps");
        JSONArray startPointArray = responseObject.getJSONArray("start_point");
        JSONArray endPointArray = responseObject.getJSONArray("end_point");
        List<WalkStep> walkSteps = new ArrayList<>();
        WalkPath walkPath = new WalkPath();
        for (int i=0; i < routeStepJSONArray.length(); i++) {
            JSONObject step = routeStepJSONArray.getJSONObject(i);
            WalkStep walkStep = new WalkStep();
            walkStep.setInstruction(step.getString("instruction"));
            walkStep.setOrientation(step.getString("orientation"));
            walkStep.setRoad(step.getString("road_name"));
            walkStep.setDistance(step.getInt("step_distance"));
            walkStep.setDuration(step.getJSONObject("cost").getInt("duration"));
            String polyLineString = step.getString("polyline");
            String[] polyLineStringArr = polyLineString.split(";");
            List<LatLonPoint> latLontPointList = new ArrayList<>();
            for (int j=0; j < polyLineStringArr.length; j++) {
                String[] lonlat = polyLineStringArr[j].split(",");
                latLontPointList.add(
                        new LatLonPoint(
                                Double.parseDouble(lonlat[1]),
                                Double.parseDouble(lonlat[0])
                        )
                );
            }
            walkStep.setPolyline(latLontPointList);
            walkSteps.add(walkStep);
        }
        walkPath.setSteps(walkSteps);
        walkPath.setDistance(routePathJSON.getInt("distance"));
        walkPath.setDuration(routePathJSON.getJSONObject("cost").getInt("duration"));
        List<WalkPath> walkPaths = new ArrayList<>();
        walkPaths.add(walkPath);
        WalkRouteResult walkRouteResult = new WalkRouteResult();
        walkRouteResult.setPaths(walkPaths);
        walkRouteResult.setPaths(walkPaths);
        LatLonPoint mStartPoint =
                new LatLonPoint(
                        startPointArray.getDouble(1),
                        startPointArray.getDouble(0)
                );
        LatLonPoint mEndPoint =
                new LatLonPoint(
                        endPointArray.getDouble(1),
                        endPointArray.getDouble(0)
                );
        walkRouteResult.setStartPos(mStartPoint);
        walkRouteResult.setTargetPos(mEndPoint);
        final RouteSearch.FromAndTo fromAndToQ = new RouteSearch.FromAndTo(
                mStartPoint, mEndPoint);
        RouteSearch.WalkRouteQuery query = new RouteSearch.WalkRouteQuery(fromAndToQ,  RouteSearch.WalkDefault);
        walkRouteResult.setWalkQuery(query);
        return walkRouteResult;
    }

    @Override
    public void onBackPressed() {
    }

    private class Listener extends WebSocketListener {
        @Override
        public void onOpen(WebSocket webSocket, Response response) {
            super.onOpen(webSocket, response);
        }

        @Override
        public void onMessage(WebSocket webSocket,final String text) {
            super.onMessage(webSocket, text);
            // 判断类类型 first_aid_request
            try {
                Log.d(TAG, "onMessage: call" + text);
                JSONObject responseObject = new JSONObject(text);
                String msg_type = responseObject.getString("type");
                JSONArray startPointArray = responseObject.getJSONArray("start_point");
                JSONArray endPointArray = responseObject.getJSONArray("end_point");
                double[] fromToDoubleArray = new double[4];
                fromToDoubleArray[0] = startPointArray.getDouble(0);
                fromToDoubleArray[1] = startPointArray.getDouble(1);
                fromToDoubleArray[2] = endPointArray.getDouble(0);
                fromToDoubleArray[3] = endPointArray.getDouble(1);
                if (msg_type.equals("first_aid_request")){
                    Log.d(TAG, "onMessage: first aid");
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            // 弹窗询问是否同意
                            int play_ret = sp.play(recev_sound_id, 50.0f, 50.0f, 1, 0, 0.8f);
                            AlertDialog.Builder builder = new AlertDialog.Builder(MainStage.this);
                            builder.setTitle("急救请求");
                            builder.setCancelable(false);
                            builder.setMessage("您收到一个急救请求，与您直线距离500米内，请问是否响应？");
                            builder.setPositiveButton("同意", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    // User clicked OK button
                                    //组装WalkPath

                                    Intent intent = new Intent(MainStage.this, WalkRouteActivity.class);
                                    intent.putExtra("fromToArray", fromToDoubleArray);
                                    WalkRouteResult walkRouteResult = null;
                                    String roomId = null;
                                    try {
                                        walkRouteResult = assembleWalkRouteResult(responseObject);
                                        roomId = assembleChatInfo(responseObject);
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                    intent.putExtra("walkRouteResult",walkRouteResult);
                                    intent.putExtra("roomId", roomId);
                                    startActivity(intent);
                                }
                            });
                            builder.setNegativeButton("拒绝", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    // User cancelled the dialog
                                }
                            });

// Create and show the AlertDialog
                            AlertDialog dialog = builder.create();
                            dialog.show();
                        }
                    });



                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

        @Override
        public void onClosed(WebSocket webSocket, int code, String reason) {
            super.onClosed(webSocket, code, reason);
        }

        @Override
        public void onFailure(WebSocket webSocket, Throwable t, Response response) {
            super.onFailure(webSocket, t, response);
        }
    }


}
