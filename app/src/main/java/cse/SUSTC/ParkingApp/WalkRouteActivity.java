package cse.SUSTC.ParkingApp;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.amap.api.maps.AMap;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.services.core.AMapException;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.route.BusRouteResult;
import com.amap.api.services.route.DriveRouteResult;
import com.amap.api.services.route.RideRouteResult;
import com.amap.api.services.route.RouteSearch;
import com.amap.api.services.route.WalkPath;
import com.amap.api.services.route.WalkRouteResult;

import org.json.JSONObject;

import java.util.ArrayList;

import cse.SUSTC.ParkingApp.overlay.WalkRouteOverlay;
import cse.SUSTC.ParkingApp.util.AMapUtil;
import cse.SUSTC.ParkingApp.util.AppLogger;
import cse.SUSTC.ParkingApp.util.ToastUtil;
import im.zego.zegoexpress.ZegoExpressEngine;
import im.zego.zegoexpress.callback.IZegoApiCalledEventHandler;
import im.zego.zegoexpress.callback.IZegoEventHandler;
import im.zego.zegoexpress.callback.IZegoRoomLoginCallback;
import im.zego.zegoexpress.constants.ZegoPlayerState;
import im.zego.zegoexpress.constants.ZegoPublisherState;
import im.zego.zegoexpress.constants.ZegoRoomStateChangedReason;
import im.zego.zegoexpress.constants.ZegoScenario;
import im.zego.zegoexpress.constants.ZegoStreamQualityLevel;
import im.zego.zegoexpress.constants.ZegoUpdateType;
import im.zego.zegoexpress.constants.ZegoVideoConfigPreset;
import im.zego.zegoexpress.entity.ZegoEngineProfile;
import im.zego.zegoexpress.entity.ZegoPlayStreamQuality;
import im.zego.zegoexpress.entity.ZegoPublishStreamQuality;
import im.zego.zegoexpress.entity.ZegoRoomConfig;
import im.zego.zegoexpress.entity.ZegoStream;
import im.zego.zegoexpress.entity.ZegoUser;
import im.zego.zegoexpress.entity.ZegoVideoConfig;


/**
 * 步行路径规划 示例
 */
public class WalkRouteActivity extends Activity implements AMap.OnMapClickListener,
        AMap.OnMarkerClickListener, AMap.OnInfoWindowClickListener, AMap.InfoWindowAdapter, RouteSearch.OnRouteSearchListener {
    private AMap aMap;
    private MapView mapView;
    private Context mContext;
    private RouteSearch mRouteSearch;
    private WalkRouteResult mWalkRouteResult;
    private LatLonPoint mStartPoint;
    private LatLonPoint mEndPoint;
    private final int ROUTE_TYPE_WALK = 3;

    private RelativeLayout mBottomLayout, mHeadLayout;
    private TextView mRotueTimeDes, mRouteDetailDes;
    private ProgressDialog progDialog = null;// 搜索时进度条
    private WalkRouteResult walkRouteResult;

    ZegoExpressEngine engine;
    ZegoVideoConfig config;
    long appID;
    String userID;
    String appSign;
    String roomID;
    String userName;
    ZegoUser user;
    String streamID;
    int fps;
    //Whether the user is publishing the stream.
    Boolean[] isPublish = {false};
    //The number of users in the room.
    int userCount = 0;
    //The number of streams in the room.
    int streamCount = 0;

    VideoViewAdapter videoViewAdapter;
    GridLayoutManager layoutManager;
    RecyclerView playView;
//    TextView roomIDText;
//    TextView streamIDText;
//    TextView userIDText;
//    TextView userNameText;
    Button streamListButton;
    Button userListButton;
//    TextView roomState;

    // Unicode of Emoji
    int roomConnectedEmoji = 0x1F7E2;
    int roomDisconnectedEmoji = 0x1F534;



    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.route_activity);
        Intent intent = getIntent();
        double[] fromToArrays = intent.getDoubleArrayExtra("fromToArray");
        walkRouteResult = intent.getParcelableExtra("walkRouteResult");
        roomID = intent.getStringExtra("roomId");
        requestPermission();

        if (fromToArrays != null) {
            mStartPoint = new LatLonPoint(fromToArrays[1], fromToArrays[0]);
            mEndPoint = new LatLonPoint(fromToArrays[3], fromToArrays[2]);
        }
        mContext = this.getApplicationContext();
        mapView = (MapView) findViewById(R.id.route_map);
        mapView.onCreate(bundle);// 此方法必须重写
        init();

        if (walkRouteResult != null) {
            setfromandtoMarker();
            searchRouteResult(ROUTE_TYPE_WALK, RouteSearch.WalkDefault);
        } else {
            mapView.setVisibility(View.GONE);
        }
        initUI();
        getAppIDAndUserIDAndAppSign();
        setDefaultValue();

        initEngineAndUser();

        setEventHandler();
        loginRoom();
        bindView();
        initTextView();
        setUserListButtonClickEvent();
        setStreamListButtonClickEvent();
        setApiCalledResult();
    }

    // Set log commponent. It includes a pop-up dialog.
//    public void setLogComponent(){
//        logLinearLayout logHiddenView = findViewById(R.id.logView);
//        logHiddenView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                LogView logview = new LogView(getApplicationContext());
//                logview.show(getSupportFragmentManager(),null);
//            }
//        });
//    }
    public void getAppIDAndUserIDAndAppSign(){
        appID = ZegoKeyCenter.getInstance().getAppID();
        userID = UserIDHelper.getInstance().getUserID();
        appSign = ZegoKeyCenter.getInstance().getAppSign();
    }
    public void setDefaultValue(){
        userName = ("Android_" + Build.MODEL).replaceAll(" ", "_");
        //create the user
        user = new ZegoUser(userID, userName);
        // set default configuration
        config = new ZegoVideoConfig(ZegoVideoConfigPreset.PRESET_180P);
        config.setEncodeResolution(360,640);
        config.setVideoBitrate(600);
        config.setVideoFPS(15);


    }
    public void initEngineAndUser(){
        // Initialize ZegoExpressEngine
        ZegoEngineProfile profile = new ZegoEngineProfile();
        profile.appID = appID;
        profile.appSign = appSign;
        profile.scenario = ZegoScenario.HIGH_QUALITY_VIDEO_CALL;
        profile.application = getApplication();
        engine = ZegoExpressEngine.createEngine(profile, null);
        engine.setVideoConfig(config);
        setLayout();

        //create the user
        user = new ZegoUser(userID, userName);
        //add the user into user list
        videoViewAdapter.userList.add(user);
        //update the number of user
        userCount += 1;

        videoViewAdapter.notifyDataSetChanged();
    }
    public void loginRoom() {
        ZegoRoomConfig RoomConfig = new ZegoRoomConfig();
        //enable the user status notification
        RoomConfig.isUserStatusNotify = true;
        user.userID = userID;
        user.userName = userName;
        //login room
        engine.loginRoom(roomID, user, RoomConfig, new IZegoRoomLoginCallback() {
            @Override
            public void onRoomLoginResult(int errorCode, JSONObject extendedData) {
                if (errorCode != 0) {
                    Toast.makeText(WalkRouteActivity.this, String.format("Login Room failed errorCode: %d", errorCode), Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        });
    }
    public void setEventHandler(){
        engine.setEventHandler(new IZegoEventHandler() {
            @Override
            public void onPublisherQualityUpdate(String streamID, ZegoPublishStreamQuality quality) {
                super.onPublisherQualityUpdate(streamID, quality);
                //After calling the [startPlayingStream] successfully, this callback will be triggered every 3 seconds.
                //The collection frame rate, bit rate, RTT, packet loss rate and other quality data can be obtained,
                //such the health of the publish stream can be monitored in real time.

                //update publish quality
                videoViewAdapter.publishQuality = quality;
                //notify the adapter to update the view.
                videoViewAdapter.notifyItemChanged(0);
            }

            @Override
            public void onRoomUserUpdate(String roomID, ZegoUpdateType updateType, ArrayList<ZegoUser> userList) {
                super.onRoomUserUpdate(roomID, updateType, userList);
                // The callback triggered when the number of other users in the room increases or decreases.

                //if the number of users increases
                if (updateType.equals(ZegoUpdateType.ADD)) {
                    for (ZegoUser user : userList) {
                        //add user in to the user list
                        videoViewAdapter.userList.add(user);
                        //insert the user into the view.
                        videoViewAdapter.notifyItemInserted(videoViewAdapter.userList.size());
                    }
                    //update the number of users in the room.
                    userCount += userList.size();
                } else {
                    //if the number of users decreases
                    for (ZegoUser User : userList) {
                        //get the index of the users who log out in user list
                        for (int i = 0; i < videoViewAdapter.userList.size(); i++) {
                            if (videoViewAdapter.userList.get(i).userID.equals(User.userID)) {
                                //remove the user from user list
                                videoViewAdapter.userList.remove(i);
                                //remove the user from view.
                                videoViewAdapter.notifyItemRemoved(i);
                            }
                        }
                    }
                    //update the number of users in the room.
                    userCount -= userList.size();
                }
                //update the text of the button.
                userListButton.setText("UserList("+userCount+")");
            }

            @Override
            public void onRoomStreamUpdate(String roomID, ZegoUpdateType updateType, ArrayList<ZegoStream> streamList, JSONObject extendedData) {
                super.onRoomStreamUpdate(roomID, updateType, streamList, extendedData);
                // The callback triggered when the number of streams published by the other users in the same room increases or decreases.

                //if the number of streams increases
                if (updateType.equals(ZegoUpdateType.ADD)) {
                    for (ZegoStream stream : streamList) {
                        //add the stream to the stream list
                        videoViewAdapter.streams.add(stream);
                        //notify the adapter to update the view
                        int index = getStreamIndex(stream.streamID, videoViewAdapter.streams);
                        if (index!=-1) {
                            videoViewAdapter.notifyItemChanged(getStreamIndex(stream.streamID, videoViewAdapter.streams));
                        } else {
                            videoViewAdapter.notifyDataSetChanged();
                        }
                    }
                    //update the number of streams
                    streamCount += streamList.size();
                } else {
                    //if the number of streams decreases
                    for (ZegoStream stream : streamList) {
                        for (int i = 0; i < videoViewAdapter.streams.size(); i++) {
                            videoViewAdapter.isPlay.remove(stream.streamID);
                            //get the index of streams which quit the room.
                            if (videoViewAdapter.streams.get(i).streamID.equals(stream.streamID)) {
                                //notify the adapter to update the view.
                                videoViewAdapter.notifyItemChanged(getStreamIndex(stream.streamID, videoViewAdapter.streams));
                                //remove the stream from stream list
                                videoViewAdapter.streams.remove(i);
                            }
                        }
                    }
                    //update the number of streams.
                    streamCount -= streamList.size();
                }
                //update the text of the button.
                streamListButton.setText("StreamList("+ streamCount +")");
            }

            @Override
            public void onPublisherStateUpdate(String streamID, ZegoPublisherState state, int errorCode, JSONObject extendedData) {
                super.onPublisherStateUpdate(streamID, state, errorCode, extendedData);
                //The callback triggered when the state of stream publishing changes.

                //if the user is publishing the stream
                if (state.equals(ZegoPublisherState.PUBLISHING)) {
                    //update the number of stream
                    if (!isPublish[0]) {
                        //update the number of stream
                        streamCount += 1;
                    }
                    //update publish status
                    isPublish[0] = true;
                    //update the text of button.
                    streamListButton.setText("StreamList(" + streamCount + ")");
                } else if (state.equals(ZegoPublisherState.NO_PUBLISH)) {
                    if (isPublish[0]) {
                        //update the number of stream
                        streamCount -= 1;
                    }
                    //update publish status
                    isPublish[0] = false;
                    //update the text of button.
                    streamListButton.setText("StreamList(" + streamCount + ")");
                }
                // If the state is PUBLISHER_STATE_NO_PUBLISH and the errcode is not 0, it means that stream publishing has failed
                // and no more retry will be attempted by the engine. At this point, the failure of stream publishing can be indicated
                // on the UI of the App.
                if (errorCode != 0 && state.equals(ZegoPublisherState.NO_PUBLISH)) {
                    if (isPublish[0]) {
                        // The user fails to publish the stream.
                        videoViewAdapter.setPublisherState(1);
                        videoViewAdapter.notifyItemChanged(0);
                    }
                } else {
                    if (isPublish[0]) {
                        // The user is publishing the stream successfully.
                        videoViewAdapter.setPublisherState(2);
                        videoViewAdapter.notifyItemChanged(0);
                    }
                }
            }

            @Override
            public void onPlayerStateUpdate(String streamID, ZegoPlayerState state, int errorCode, JSONObject extendedData) {
                super.onPlayerStateUpdate(streamID, state, errorCode, extendedData);
                videoViewAdapter.setPlayerState(streamID,state,errorCode);
                videoViewAdapter.notifyItemChanged(getStreamIndex(streamID,videoViewAdapter.streams));
            }

            @Override
            public void onPlayerQualityUpdate(String streamID, ZegoPlayStreamQuality quality) {
                super.onPlayerQualityUpdate(streamID, quality);
                // Callback for current stream playing quality.
                // After calling the [startPlayingStream] successfully, this callback will be triggered every 3 seconds.
                videoViewAdapter.streamQuality.put(streamID, quality);
                //update the viewf
                videoViewAdapter.notifyItemChanged(getStreamIndex(streamID, videoViewAdapter.streams));
            }

            @Override
            public void onPlayerVideoSizeChanged(String streamID, int width, int height) {
                super.onPlayerVideoSizeChanged(streamID, width, height);
                // The callback triggered when the stream playback resolution changes.

                int[] temp = {width,height};
                videoViewAdapter.videoSize.put(streamID, temp);
                //update the view
                videoViewAdapter.notifyItemChanged(getStreamIndex(streamID, videoViewAdapter.streams));
            }
            // The callback triggered when the room connection state changes.
            @Override
            public void onRoomStateChanged(String roomID, ZegoRoomStateChangedReason reason, int errorCode, JSONObject extendedData) {
//                ZegoViewUtil.UpdateRoomState(roomState, reason);
            }

            @Override
            public void onNetworkQuality(String userID, ZegoStreamQualityLevel upstreamQuality, ZegoStreamQualityLevel downstreamQuality) {
                super.onNetworkQuality(userID, upstreamQuality, downstreamQuality);
                if (userID.isEmpty()) {
                    // Local user's network quality
                    videoViewAdapter.publishNetworkQuality = upstreamQuality;
                    videoViewAdapter.notifyItemChanged(0);
                } else {
                    videoViewAdapter.networkQuality.put(userID, upstreamQuality);
                    videoViewAdapter.notifyItemChanged(getUserIndex(userID));
                }
            }
        });
    }
    public void setUserListButtonClickEvent() {

        userListButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ListDialog.Builder Builder = new ListDialog.Builder(WalkRouteActivity.this);
                Builder.setTitle("UserList");
                Builder.setUserListString(videoViewAdapter.userList);
                ListDialog dialog = Builder.create();
                Builder.refresh.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //update user list
                        Builder.setUserListString(videoViewAdapter.userList);
                        //update the view.
                        Builder.refresh();
                    }
                });
                dialog.show();
            }
        });
    }

    public void setStreamListButtonClickEvent() {
        streamListButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ListDialog.Builder Builder = new ListDialog.Builder(WalkRouteActivity.this);
                Builder.setTitle("StreamList");
                Builder.setMyStream(user, streamID);
                Builder.setStreamListString(videoViewAdapter.streams,isPublish[0]);
                ListDialog dialog = Builder.create();
                Builder.refresh.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //update the stream list and publish status
                        Builder.setStreamListString(videoViewAdapter.streams,isPublish[0]);
                        //update the view
                        Builder.refresh();
                    }
                });
                dialog.show();
            }
        });
    }
    public void setApiCalledResult(){
        // Update log with api called results
        ZegoExpressEngine.setApiCalledCallback(new IZegoApiCalledEventHandler() {
            @Override
            public void onApiCalledResult(int errorCode, String funcName, String info) {
                super.onApiCalledResult(errorCode, funcName, info);
                if (errorCode == 0){
                    AppLogger.getInstance().success("[%s]:%s", funcName, info);
                } else {
                    AppLogger.getInstance().fail("[%d]%s:%s", errorCode, funcName, info);
                }
            }
        });
    }
    private void setfromandtoMarker() {
        aMap.addMarker(new MarkerOptions()
                .position(AMapUtil.convertToLatLng(mStartPoint))
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.start)));
        aMap.addMarker(new MarkerOptions()
                .position(AMapUtil.convertToLatLng(mEndPoint))
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.end)));
    }
    //请求摄像头、录音权限
    private void requestPermission() {
        String[] permissionNeeded = {
                "android.permission.CAMERA",
                "android.permission.RECORD_AUDIO"};
        if (ContextCompat.checkSelfPermission(getApplicationContext(), "android.permission.CAMERA") != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(getApplicationContext(), "android.permission.RECORD_AUDIO") != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(permissionNeeded, 101);
            }
        }
    }
    /**
     * 初始化AMap对象
     */
    private void init() {
        if (aMap == null) {
            aMap = mapView.getMap();
        }
        registerListener();
        try {
            mRouteSearch = new RouteSearch(this);
            mRouteSearch.setRouteSearchListener(this);
        } catch (AMapException e) {
            e.printStackTrace();
        }
        mBottomLayout = (RelativeLayout) findViewById(R.id.bottom_layout);
//        mHeadLayout = (RelativeLayout) findViewById(R.id.routemap_header);
//        mHeadLayout.setVisibility(View.GONE);
        mRotueTimeDes = (TextView) findViewById(R.id.firstline);
        mRouteDetailDes = (TextView) findViewById(R.id.secondline);
    }
    public void initUI() {
        bindView();
        initTextView();
    }
    public void bindView(){
        playView = findViewById(R.id.allVideo);
//        roomIDText = findViewById(R.id.roomID);
//        streamIDText = findViewById(R.id.streamID);
//        userNameText = findViewById(R.id.userName);
//        userIDText = findViewById(R.id.userID);
        streamListButton = findViewById(R.id.streamListButton);
        userListButton = findViewById(R.id.userListButton);
//        roomState = findViewById(R.id.roomState);
        // chat
    }
    public void initTextView(){
//        roomIDText.setText(roomID);
//        userNameText.setText(userName);
//        streamIDText.setText(streamID);
//        userIDText.setText(userID);
        streamListButton.setText("StreamList(0)");
        userListButton.setText("Userlist(" + userCount + ")");
        setTitle(getString(R.string.video_for_multiple_users));
    }
    public void setLayout(){
        layoutManager = new GridLayoutManager(this, 2);
        videoViewAdapter = new VideoViewAdapter(getApplicationContext());
        streamID = String.valueOf((int)((Math.random()*9+1)*100000));
        videoViewAdapter.myStreamID = streamID;
        //Set the adapter and layout manager of view
        playView.setLayoutManager(layoutManager);
        playView.setAdapter(videoViewAdapter);
        //disable the item animator to avoid blinking
        playView.setItemAnimator(null);
    }

    /**
     * 注册监听
     */
    private void registerListener() {
        aMap.setOnMapClickListener(WalkRouteActivity.this);
        aMap.setOnMarkerClickListener(WalkRouteActivity.this);
        aMap.setOnInfoWindowClickListener(WalkRouteActivity.this);
        aMap.setInfoWindowAdapter(WalkRouteActivity.this);

    }

    @Override
    public View getInfoContents(Marker arg0) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public View getInfoWindow(Marker arg0) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void onInfoWindowClick(Marker arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public boolean onMarkerClick(Marker arg0) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public void onMapClick(LatLng arg0) {
        // TODO Auto-generated method stub

    }


    /**
     * 开始搜索路径规划方案
     */
    public void searchRouteResult(int routeType, int mode) {
        if (mStartPoint == null) {
            ToastUtil.show(mContext, "定位中，稍后再试...");
            return;
        }
        if (mEndPoint == null) {
            ToastUtil.show(mContext, "终点未设置");
        }
        showProgressDialog();
        final RouteSearch.FromAndTo fromAndTo = new RouteSearch.FromAndTo(
                mStartPoint, mEndPoint);
        if (routeType == ROUTE_TYPE_WALK) {// 步行路径规划
            RouteSearch.WalkRouteQuery query = new RouteSearch.WalkRouteQuery(fromAndTo, mode);
            onWalkRouteSearched(walkRouteResult, AMapException.CODE_AMAP_SUCCESS);
//            mRouteSearch.calculateWalkRouteAsyn(query);// 异步路径规划步行模式查询
        }
    }

    @Override
    public void onBusRouteSearched(BusRouteResult result, int errorCode) {

    }

    @Override
    public void onDriveRouteSearched(DriveRouteResult result, int errorCode) {

    }
    @Override
    public void onWalkRouteSearched(WalkRouteResult result, int errorCode) {
        dissmissProgressDialog();
        aMap.clear();// 清理地图上的所有覆盖物
        if (errorCode == AMapException.CODE_AMAP_SUCCESS) {
            if (result != null && result.getPaths() != null) {
                if (result.getPaths().size() > 0) {
                    mWalkRouteResult = result;
                    final WalkPath walkPath = mWalkRouteResult.getPaths()
                            .get(0);
                    if(walkPath == null) {
                        return;
                    }
                    WalkRouteOverlay walkRouteOverlay = new WalkRouteOverlay(
                            this, aMap, walkPath,
                            mWalkRouteResult.getStartPos(),
                            mWalkRouteResult.getTargetPos());
                    walkRouteOverlay.removeFromMap();
                    walkRouteOverlay.addToMap();
                    walkRouteOverlay.zoomToSpan();
                    mBottomLayout.setVisibility(View.VISIBLE);
                    int dis = (int) walkPath.getDistance();
                    int dur = (int) walkPath.getDuration();
                    String des = AMapUtil.getFriendlyTime(dur)+"("+AMapUtil.getFriendlyLength(dis)+")";
                    mRotueTimeDes.setText(des);
                    mRouteDetailDes.setVisibility(View.GONE);
                    mBottomLayout.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(mContext,
                                    WalkRouteDetailActivity.class);
                            intent.putExtra("walk_path", walkPath);
                            intent.putExtra("walk_result",
                                    mWalkRouteResult);
                            startActivity(intent);
                        }
                    });
                } else if (result != null && result.getPaths() == null) {
                    ToastUtil.show(mContext, R.string.no_result);
                }
            } else {
                ToastUtil.show(mContext, R.string.no_result);
            }
        } else {
            ToastUtil.showerror(this.getApplicationContext(), errorCode);
        }
    }
    //return the index of a specific stream in the stream list 获得指定stream在流list中的index
    public int getStreamIndex (String StreamID, ArrayList<ZegoStream> StreamList){
        for (ZegoStream stream:StreamList)
        {
            if (StreamID.equals(stream.streamID)){
                for (int i = 0; i< videoViewAdapter.userList.size(); i++){
                    if (videoViewAdapter.userList.get(i).userID.equals(stream.user.userID)){
                        return i;
                    }
                }
            }
        }
        return -1;
    }
    public int getUserIndex(String userID) {
        for (int i = 0; i< videoViewAdapter.userList.size(); i++){
            if (videoViewAdapter.userList.get(i).userID.equals(userID)){
                return i;
            }
        }
        return -1;
    }
    public String getStreamID(String userID,ArrayList<ZegoStream> streamList){
        for (ZegoStream stream:streamList){
            if (stream.user.userID.equals(userID)){
                return stream.streamID;
            }
        }
        return "";
    }



    /**
     * 显示进度框
     */
    private void showProgressDialog() {
        if (progDialog == null) {
            progDialog = new ProgressDialog(this);
        }
        progDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progDialog.setIndeterminate(false);
        progDialog.setCancelable(true);
        progDialog.setMessage("正在搜索");
        progDialog.show();
    }

    /**
     * 隐藏进度框
     */
    private void dissmissProgressDialog() {
        if (progDialog != null) {
            progDialog.dismiss();
        }
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onRideRouteSearched(RideRouteResult arg0, int arg1) {
        // TODO Auto-generated method stub

    }

}

