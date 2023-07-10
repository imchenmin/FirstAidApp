package cse.SUSTC.ParkingApp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.http.cookie.DbCookieStore;
import org.xutils.x;

import java.net.HttpCookie;
import java.util.List;

/**
 * This class will load log in stage and do logic judgement for log in action.
 */
public class LogInActivity extends AppCompatActivity {

    private transient EditText username;
    private transient EditText password;


    private transient JSONObject resultJson;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    public static final String TAG = "LoginActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        username = findViewById(R.id.LoginUsername);
        password = findViewById(R.id.LoginPassword);
        // 尝试读取保存信息

        sharedPreferences = getSharedPreferences("userInfo", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();//获取编辑器
    }

    /**
     * This method will redirect the stage to register stage
     *
     * @param view am view the text hint of go to register
     */
    public void goToReg(View view) {
        startActivity(new Intent(this, RegisterActivity.class));
        finish();

    }

    /**
     * Do logic judgement when user push log in button.
     * Do network communication with server and redirect to main stage if successfully log in or
     * raise a notification if something goes wrong.
     *
     * @param view the log in button in log in stage
     */
    public void login(View view) {
        String name = username.getText().toString().trim();
        String pwd = password.getText().toString().trim();

        RequestParams usrInfo = new RequestParams(MyApplication.URL + "/token");
        usrInfo.addBodyParameter("username", name);
        usrInfo.addBodyParameter("password", pwd);
        usrInfo.addHeader("accept", "application/json");
        usrInfo.addHeader("Content-Type", "application/x-www-form-urlencoded");
        System.out.println(usrInfo.getHeaders());
        x.http().post(usrInfo, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Log.d(TAG, "onSuccess: "+result);
                try {
                    resultJson = new JSONObject(result);
                    // check whether login success from response
                    // TODO later: remove duplicated logic control
                    if (resultJson.has("access_token")){
                        editor.putString("access_token", resultJson.getString("access_token"));
                        editor.putString("token_type", resultJson.getString("access_token"));
                        editor.commit();//提交修改
                        MyApplication.setLoginCode("success");
                    } else {
                        MyApplication.setLoginCode("fail");
                    }
                    if (MyApplication.getLoginCode().equals("success")) {
                        MyApplication.setLoginState(true);
                        DbCookieStore instance = DbCookieStore.INSTANCE;
                        List<HttpCookie> cookies = instance.getCookies();
                        for (int i = 0; i < cookies.size(); i++) {
                            HttpCookie cookie = cookies.get(i);
                            if (cookie.getName() != null && cookie.getName().equals(MyApplication.SESSION_NAME)) {
                                MyApplication.setSession(cookie.getValue());
                                System.out.println("ss:" + cookie.getValue());
                            }
                        }
                    } else {
                        MyApplication.setLoginState(false);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                System.out.println(ex.toString());

            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {
                System.out.println("fin");
                tryLogin();

            }
        });


    }



    private void tryLogin() {
        if (MyApplication.getLoginCode().equals("success")) {
            System.out.println("saved_s:" + MyApplication.getSession());
            startActivity(new Intent(this, MainStage.class));
            finish();
        } else {
            AlertDialog alertDialog1 = null;
            try {
                alertDialog1 = new AlertDialog.Builder(this)
                        .setTitle("Login Fail")
                        .setMessage(resultJson.getJSONObject("data").getString("errMsg"))
                        .setPositiveButton("OK", null)
                        .create();
                alertDialog1.show();
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }
}
