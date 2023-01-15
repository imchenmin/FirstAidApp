package cse.SUSTC.ParkingApp;

import android.os.Bundle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.util.ArrayList;
import java.util.List;


/**
 * This class will load register stage and do register judgement for register action.
 */
public class CarManagementActivity extends AppCompatActivity {
    private transient RecyclerView carContainer;
    private transient BandedCarCardAdapter adapter;
    private Button button1;
    private String statuscode;
    private transient EditText carPlate;
    protected transient List<Car> carList = new ArrayList<>();

    private transient JSONObject resultJson;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public void onStart() {
        setContentView(R.layout.fragment_car_stage);
        carContainer = (RecyclerView) findViewById(R.id.CarContainer);
        GridLayoutManager layoutManager = new GridLayoutManager(this, 1);
        carContainer.setLayoutManager(layoutManager);
        pullCars();
        button1 = (Button) findViewById(R.id.addCarBtn);
        button1.setOnClickListener(listener);
        super.onStart();

    }

    public View.OnClickListener listener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub
            if (v.getId() == R.id.addCarBtn) {
                gotoRegisterCar();
            }
        }
    };

    private void gotoRegisterCar() {
        setContentView(R.layout.car_management);
        //方法2可以把button2的定义和监听放到这里
        carPlate = findViewById(R.id.CarPlate);
        findViewById(R.id.RegisterCar).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String plate = carPlate.getText().toString().trim();

                RequestParams usrInfo = new RequestParams(MyApplication.URL + "/car/register");
                String sessionId = MyApplication.SESSION_NAME + "=" + MyApplication.getSession();
                usrInfo.addHeader("cookie", sessionId);
                usrInfo.addHeader("Content-Type", "application/x-www-form-urlencoded");

                System.out.println(usrInfo.getHeaders());

                usrInfo.addBodyParameter("numberPlate", plate);
                usrInfo.addBodyParameter("description", "des");

                x.http().post(usrInfo, new Callback.CommonCallback<String>() {
                    @Override
                    public void onSuccess(String result) {
                        System.out.println("register car succ");
                        System.out.println(result);
                        try {
                            resultJson = new JSONObject(result);
                            statuscode = resultJson.getString("status");

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
                        try {
                            tryregister();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });

            }
        });
    }

    private void tryregister() throws JSONException {
        if (statuscode.equals("success")) {
            AlertDialog alertDialog1 = new AlertDialog.Builder(this)
                    .setTitle("Register Success")
                    .setMessage("You have successfully Registered！")
                    .setPositiveButton("OK", null)
                    .create();
            alertDialog1.show();
            setContentView(R.layout.fragment_car_stage);
            carContainer = (RecyclerView) findViewById(R.id.CarContainer);
            GridLayoutManager layoutManager = new GridLayoutManager(this, 1);
            carContainer.setLayoutManager(layoutManager);
            pullCars();
            button1 = (Button) findViewById(R.id.addCarBtn);
            button1.setOnClickListener(listener);

        } else {
            JSONObject errordata;
            errordata = new JSONObject(resultJson.getString("data"));
            AlertDialog alertDialog1 = new AlertDialog.Builder(this)
                    .setTitle("Register Fail")
                    .setMessage(errordata.getString("errMsg"))
                    .setPositiveButton("OK", null)
                    .create();
            alertDialog1.show();
        }
    }

    /**
     * get cars info from server
     */
    protected void pullCars() {
        carList.clear();
        RequestParams usrInfo = new RequestParams(MyApplication.URL + "/car/listmycar");
        String sessionId = MyApplication.SESSION_NAME + "=" +
                MyApplication.getSession();
        usrInfo.addHeader("cookie", sessionId);
        usrInfo.addHeader("Content-Type", "application/x-www-form-urlencoded");
        System.out.print(usrInfo.getHeaders());

        x.http().get(usrInfo, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                System.out.println(result);
                try {
                    JSONObject resultJson = new JSONObject(result);
                    JSONArray orderArrayJ = resultJson.getJSONArray("data");

                    for (int i = 0; i < orderArrayJ.length(); i++) {
                        JSONObject currentCarJ = orderArrayJ.getJSONObject(i);
                        Car o = new Car(currentCarJ);
                        carList.add(o);
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
                loadCarStage();
            }
        });
    }

    private void loadCarStage() {
        adapter = new BandedCarCardAdapter(carList);
        carContainer.setAdapter(adapter);
    }

}
