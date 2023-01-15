package cse.SUSTC.ParkingApp;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.fragment.app.Fragment;

import android.os.Parcel;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapFragment;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.MyLocationStyle;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.core.ServiceSettings;
import com.amap.api.services.route.WalkRouteResult;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Objects;

import cse.SUSTC.ParkingApp.util.AMapUtil;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;


/**
 * This part will load map stage that can achieve map functions in our app
 * not finished yet
 */
public class MapStage extends Fragment {

    private MapView mapView;
    private AMap aMap;
    private static MapFragment fragment=null;
    private View mapLayout;
    public static final int POSITION=0;
    private AMapLocationClient locationClientContinue = null;
    private OkHttpClient okHttpClient;
    private static final String TAG = "map stage";
    private SharedPreferences sharedPreferences;
//    private WebSocket WSclient = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (mapLayout == null) {
            Log.i("sys", "MF onCreateView() null");
            mapLayout = inflater.inflate(R.layout.fragment_map_stage, null);
            mapView = (MapView) mapLayout.findViewById(R.id.map);
            mapView.onCreate(savedInstanceState);
            if (aMap == null) {
                aMap = mapView.getMap();
            }
        }else {

            if (mapLayout.getParent() != null) {
                ((ViewGroup) mapLayout.getParent()).removeView(mapLayout);
            }
        }
        try {
            ServiceSettings.updatePrivacyShow(getContext(), true, true);
            ServiceSettings.updatePrivacyAgree(getContext(),true);
        } catch (Exception e) {
            e.printStackTrace();
        }

        sharedPreferences = getActivity().getSharedPreferences("userInfo", Context.MODE_PRIVATE);

        return mapLayout;
    }
    /**
     * 启动连续客户端定位
     */
    void startContinueLocation(){
        if(null == locationClientContinue){
            try {
                locationClientContinue = new AMapLocationClient(this.getActivity().getApplicationContext());
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

    /**
     * 停止连续客户端
     */
    void stopContinueLocation(){
        if(null != locationClientContinue){
            locationClientContinue.stopLocation();
        }
    }
    AMapLocationListener locationListener = new AMapLocationListener() {
        @Override
        public void onLocationChanged(AMapLocation location) {
            long callBackTime = System.currentTimeMillis();
            StringBuffer sb = new StringBuffer();
            JSONObject jsonObject = location.toJson(1);
            try {
                jsonObject.put("msg_type","location");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            String msg_string = jsonObject  == null ? null : jsonObject.toString();
//            WSclient.send(msg_string);
            sb.append("定位完成\n");
            if(null == location){
                sb.append("定位失败：location is null!!!!!!!");
            } else {
                sb.append(location.toStr());
            }
            Log.d("TAG", "onLocationChanged: " + sb.toString());
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }
    @Override
    public void onStart() {
        mapView = Objects.requireNonNull(getActivity()).findViewById(R.id.map);
        super.onStart();
    }

    @Override
    public void onDestroy() {
        stopContinueLocation();
        mapView.onDestroy();
//        WSclient.close(0,"normal");
        super.onDestroy();
    }

    @Override
    public void onPause() {
        mapView.onPause();
        super.onPause();
    }

    @Override
    public void onResume() {
        mapView.onResume();
        aMap.moveCamera(CameraUpdateFactory.zoomTo(Float.valueOf(17)));
        MyLocationStyle myLocationStyle;
        myLocationStyle = new MyLocationStyle();//初始化定位蓝点样式类myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATION_ROTATE);//连续定位、且将视角移动到地图中心点，定位点依照设备方向旋转，并且会跟随设备移动。（1秒1次定位）如果不设置myLocationType，默认也会执行此种模式。
//        myLocationStyle.interval(10000); //设置连续定位模式下的定位间隔，只在连续定位模式下生效，单次定位模式下不会生效。单位为毫秒。
        aMap.setMyLocationStyle(myLocationStyle);//设置定位蓝点的Style
//        startContinueLocation();
        aMap.getUiSettings().setMyLocationButtonEnabled(true);//设置默认定位按钮是否显示，非必需设置。
        aMap.setMyLocationEnabled(true);// 设置为true表示启动显示定位蓝点，false表示隐藏定位蓝点并不进行定位，默认是false。
//        okHttpClient=new OkHttpClient();
//        String token = sharedPreferences.getString("access_token","");
//        Request request=new Request
//                .Builder().url("ws://10.27.132.158:8012/ws?token="+token).build();
//        WSclient = okHttpClient.newWebSocket(request,new Listener());
        super.onResume();
    }

    @Override
    public void onStop() {
        super.onStop();
    }
    /**
     * 方法必须重写
     * map的生命周期方法
     */
    @Override
    public void onSaveInstanceState(Bundle outState) {
        Log.i("sys", "mf onSaveInstanceState");
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
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
                LatLonPoint mStartPoint =
                        new LatLonPoint(
                                startPointArray.getDouble(0),
                                startPointArray.getDouble(1)
                        );
                LatLonPoint mEndPoint =
                        new LatLonPoint(
                                endPointArray.getDouble(0),
                                endPointArray.getDouble(1)
                        );
                double[] fromToDoubleArray = new double[4];
                fromToDoubleArray[0] = startPointArray.getDouble(0);
                fromToDoubleArray[1] = startPointArray.getDouble(1);
                fromToDoubleArray[2] = endPointArray.getDouble(0);
                fromToDoubleArray[3] = endPointArray.getDouble(1);
                if (msg_type.equals("first_aid_request")){
                    Log.d(TAG, "onMessage: first aid");
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            // 弹窗询问是否同意
                            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                            builder.setTitle("急救请求");
                            builder.setCancelable(false);
                            builder.setMessage("您收到一个急救请求，与您直线距离500米内，请问是否响应？");
                            builder.setPositiveButton("同意", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    // User clicked OK button
                                    //组装WalkPath
                                    Intent intent = new Intent(getActivity(), WalkRouteActivity.class);
                                    intent.putExtra("fromToArray", fromToDoubleArray);
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
