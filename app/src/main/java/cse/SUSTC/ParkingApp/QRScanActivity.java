package cse.SUSTC.ParkingApp;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

//import com.uuzuche.lib_zxing.activity.CaptureActivity;
import com.uuzuche.lib_zxing.activity.CodeUtils;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;


public class QRScanActivity extends AppCompatActivity {
    public final static int REQUEST_CODE = 1;
    private EditText etInput;
    private Button btnGetTopBill;
    static String currentSlot;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.qrscan_main);

        init();
    }

    private void init() {
        etInput = (EditText) findViewById(R.id.et_car_plate_num);
        btnGetTopBill = (Button) findViewById(R.id.btn_get_top_bill);

        //send
        btnGetTopBill.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String numplate = etInput.getText().toString();
                RequestParams billInfo = new RequestParams(MyApplication.URL + "/bill/payqrcode");
                String sessionId = MyApplication.SESSION_NAME + "=" +
                        MyApplication.getSession();
                billInfo.addHeader("cookie", sessionId);
                billInfo.addHeader("Content-Type", "application/x-www-form-urlencoded");
                System.out.print(billInfo.getHeaders());

                billInfo.addBodyParameter("parkingSlotId", currentSlot);
                billInfo.addBodyParameter("numberPlate", numplate);
                x.http().post(billInfo, new Callback.CommonCallback<String>() {
                    @Override
                    public void onSuccess(String result) {
                        System.out.println(result);
                        try {

                            JSONObject resultJson = new JSONObject(result);
                            JSONObject dataJson = resultJson.getJSONObject("data");
                            String returnStatus = resultJson.getString("status");
                            if (returnStatus.equals("success")) {
                                Order o = new Order(dataJson);
                                OrderStage.TMP_ORDER_LIST.add(o);
                                MainStage.currentFragment = MainStageFragments.ORDER;
                                Intent intent = new Intent();
                                intent.setClass(QRScanActivity.this, MainStage.class);
                                startActivity(intent);
                            } else {
                                Toast.makeText(QRScanActivity.this,dataJson.getString("errMsg"), Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            System.out.println("JSONException: ");
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
//                        OrderStage.loadOrderStage();

                    }
                });
            }
        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE) {
            if (null != data) {
                Bundle bundle = data.getExtras();
                if (bundle == null) {
                    return;
                }

                if (bundle.getInt(CodeUtils.RESULT_TYPE) == CodeUtils.RESULT_SUCCESS) {
                    String result = bundle.getString(CodeUtils.RESULT_STRING);
//                    currentSlot = result;
                    Toast.makeText(this, "解析结果:" + result, Toast.LENGTH_LONG).show();
                } else if (bundle.getInt(CodeUtils.RESULT_TYPE) == CodeUtils.RESULT_FAILED) {
//                    Toast.makeText(MainActivity.this, "解析二维码失败", Toast.LENGTH_LONG).show();
                }
            }
        }
    }




}
