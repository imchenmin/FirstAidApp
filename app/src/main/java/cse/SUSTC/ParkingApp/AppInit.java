package cse.SUSTC.ParkingApp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

/**
 * This class will load init stage of the android app
 */
public class AppInit extends AppCompatActivity {

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    public static final String TAG = "AppInit";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.init_stage);
        sharedPreferences = getSharedPreferences("userInfo", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();//获取编辑器
        autoLogin();
    //TODO redirection to main stage if already login
    }

    /**
     * This method will redirect the stage to log in stage
     *
     * @param view the login button in init stage
     */
    public void goToLogin(View view) {
        startActivity(new Intent(this, LogInActivity.class));
        finish();
    }

    /**
     * This method will redirect the stage to register stage
     *
     * @param view the text hint of go to register
     */
    public void goToReg(View view) {
        startActivity(new Intent(this, RegisterActivity.class));
        finish();
    }

    /**
     * 调用已存储的token尝试登录
     */
    private void autoLogin() {
        Log.d(TAG, "autoLogin: ");
        String access_token = sharedPreferences.getString("access_token","");
        if (access_token.equals("")) {
            return;
        }
        // 发送验证登录请求
        RequestParams usrInfo = new RequestParams(MyApplication.URL + "/users/me/");
        usrInfo.addHeader("Authorization", "Bearer " + access_token);
        usrInfo.setHeader("connection","");
        usrInfo.setHeader("accept-encoding","");
        Log.d(TAG, "autoLogin: "+ usrInfo.getHeaders());
        x.http().get(usrInfo, new Callback.CommonCallback<String>() {

            @Override
            public void onSuccess(String result) {
                JSONObject responseJson;
                try {
                    responseJson = new JSONObject(result);
                    if (responseJson.has("detail")) {
                        editor.putString("access_token", "");
                        editor.putString("token_type", "");
                        editor.commit();//提交修改
                    } else {
                        Log.d(TAG, "onSuccess responseJson: " + result);
                        MyApplication.setLoginCode("success");

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {

            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {
                if (!MyApplication.getLoginCode().equals("success")) {
                    editor.putString("access_token", "");
                    editor.putString("token_type", "");
                    editor.commit();//提交修改
                } else {
                    redirect();

                }
            }
        });
    }
    private void redirect() {
        startActivity(new Intent(this, MainStage.class));
    }


}
