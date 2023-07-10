package cse.SUSTC.ParkingApp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

/**
 * This class will load register stage and do register judgement for register action.
 */
public class RegisterActivity extends AppCompatActivity {


    private static final String TAG = "RegisterActivity";
    private transient EditText username;

    private transient EditText password;
    private transient EditText telephone;
    private transient EditText optcode;
    private transient RadioGroup rg;
    private Button button1;

    private transient JSONObject resultJson;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register);
        username = findViewById(R.id.RegisterUsername);
        password = findViewById(R.id.RegisterPassword);
        telephone = findViewById(R.id.RegisterTel);
        optcode = findViewById(R.id.RegisterOpt);
        rg = findViewById(R.id.rg);
        button1 =(Button)findViewById(R.id.GetOptNum);
        button1.setOnClickListener(listener);

    }
    public View.OnClickListener listener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            if (v.getId() == R.id.GetOptNum) {
                getOptCode();
            }
        }
    };
    /**
     * This method is to push request to get opt code
     *
     *
     */
    private void getOptCode(){
        Map jsonMap = new HashMap();
        String telephoneNum = telephone.getText().toString().trim();
        jsonMap.put("telephone", telephoneNum);
        JSONObject jsonObject = new JSONObject(jsonMap);
        RequestBody requestBodyJson
                = RequestBody.create(
                MediaType.parse("application/json; charset=utf-8"),
                jsonObject.toString());
        Request request = new Request.Builder()
                .url(MyApplication.URL + "/user/getotp")
                .addHeader("accept", "application/json")
                .addHeader("Content-Type", "application/json")
                .post(requestBodyJson)
                .build();
        OkHttpClient client = new OkHttpClient();
        client.newCall(request).enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                String message = e != null ? e.getMessage() : "";
                Log.e(TAG, "onFailure: "+ message );
            }

            @Override
            public void onResponse(Call call, okhttp3.Response response) throws IOException {
                if (response.code() == 200){
                    MyApplication.setRegisterCode("200");
                    try {
                        tryRegister(response.code());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                String body = response.body().string();
                Log.e(TAG, "onResponse: body = " + body);
            }
        });
//        RequestParams usrRegInfo = new RequestParams(MyApplication.URL + "/user/getotp");
//        usrRegInfo.addHeader("Content-Type", "application/json");
//        usrRegInfo.addHeader("accept", "application/json");
//        String tel = telephone.getText().toString().trim();
//        usrRegInfo.addBodyParameter("telephone", tel);
        // TODO 用okhttp替换

//        x.http().post(usrRegInfo, new Callback.CommonCallback<String>() {
//            @Override
//            public void onSuccess(String result) {
//                try {
//                    resultJson = new JSONObject(result);
//                    Toast.makeText(getApplicationContext(),"Get Code: "+ resultJson.getString("status"),Toast.LENGTH_LONG).show();
//                    System.out.println("Get opt code status: " +resultJson.getString("status") );
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//            }
//
//            @Override
//            public void onError(Throwable ex, boolean isOnCallback) {
//                System.out.println(ex.toString());
//                System.out.println("err");
//            }
//
//            @Override
//            public void onCancelled(CancelledException cex) {
//
//            }
//
//            @Override
//            public void onFinished() {
//            }
//
//        });
    }
    /**
     * This method will redirect the stage to log in stage
     *
     * @param view the the text hint of go to log in stage
     */
    public void goToLogin(View view) {
        startActivity(new Intent(this, LogInActivity.class));
        finish();
    }

    /**
     * Do logic judgement when user push register button.
     * Do network communication with server and redirect to log in stage if successfully register an
     * account or raise a notification if something goes wrong.
     *
     * @param view the register button in log in stage
     */
    public void register(View view) {
        String name = username.getText().toString().trim();
        String pwd = password.getText().toString().trim();
        String tel = telephone.getText().toString().trim();
        String opt = optcode.getText().toString().trim();
        String genderSelected = " ";
        for (int i = 0; i < rg.getChildCount(); i++) {
            RadioButton rd = (RadioButton) rg.getChildAt(i);
            if (rd.isChecked()) {
                genderSelected = rd.getText().toString();
                break;
            }
        }
        OkHttpClient client = new OkHttpClient();
        Map jsonMap = new HashMap();

        jsonMap.put("username", name);
        jsonMap.put("password", pwd);
        jsonMap.put("telephone", tel);
        jsonMap.put("email", tel);
        if(genderSelected.equals("Male"))
            jsonMap.put("gender", "1");
        else
            jsonMap.put("gender", "2");
        JSONObject jsonObject = new JSONObject(jsonMap);
        RequestBody requestBodyJson
                = RequestBody.create(
                        MediaType.parse("application/json; charset=utf-8"),
                        jsonObject.toString());
        Request request = new Request.Builder()
                .url(MyApplication.URL + "/user/getotp")
                .addHeader("accept", "application/json")
                .addHeader("Content-Type", "application/json")
                .post(requestBodyJson)
                .build();
        client.newCall(request).enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

                String message = e != null ? e.getMessage() : "";
                Log.e(TAG, "onFailure: "+message );
            }

            @Override
            public void onResponse(Call call, okhttp3.Response response) throws IOException {
                if (response.code() == 200){
                    MyApplication.setRegisterCode("200");
                    try {
                        tryRegister(response.code());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                String body = response.body().string();
                Log.e(TAG, "onResponse: body = " + body);
            }
        });

    }

    private void tryRegister(int code) throws JSONException {

        if (code == 200){
            AlertDialog alertDialog1 = new AlertDialog.Builder(this)
                    .setTitle("Register Success")
                    .setMessage("You have successfully Registered！")
                    .setPositiveButton("OK", null)
                    .create();
            alertDialog1.show();
            startActivity(new Intent(this, LogInActivity.class));
            finish();
        } else {
            JSONObject errordata;
            errordata = new JSONObject(MyApplication.getRegisterCode());
            AlertDialog alertDialog1 = new AlertDialog.Builder(this)
                    .setTitle("Register Fail")
                    .setMessage(errordata.optString("errMsg"))
                    .setPositiveButton("OK", null)
                    .create();
            alertDialog1.show();

        }
    }

}