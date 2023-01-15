package cse.SUSTC.ParkingApp;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


/**
 * This part will load order stage which let user check their bill
 */
public class OrderStage extends Fragment {

    private transient RecyclerView orderContainer;
    private transient RecyclerView tmpOrderContainer;
    private transient OrderAdapter adapter;
    transient List<Order> orderList = new ArrayList<>();
    static final transient List<Order> TMP_ORDER_LIST = new ArrayList<>();



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_order_stage, container, false);
    }

    @Override
    public void onStart() {
        orderContainer = Objects.requireNonNull(getView()).findViewById(R.id.OrderContainer);
        GridLayoutManager layoutManager = new GridLayoutManager(this.getActivity(), 1);
        orderContainer.setLayoutManager(layoutManager);

        tmpOrderContainer = Objects.requireNonNull(getView()).findViewById(R.id.TmpOrderContainer);
        GridLayoutManager layoutManager2 = new GridLayoutManager(this.getActivity(), 1);
        tmpOrderContainer.setLayoutManager(layoutManager2);



        pullOrders();


        super.onStart();

    }

    @Override
    public void onResume() {
        loadOrderStage();

        super.onResume();
    }

    /**
     * get order
     */
    protected void pullOrders() {
        orderList.clear();

        RequestParams usrInfo = new RequestParams(MyApplication.URL + "/bill/listall");
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
                        JSONObject currentOrderJ = orderArrayJ.getJSONObject(i);
                        Order o = new Order(currentOrderJ);
                        orderList.add(o);
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
                loadOrderStage();

            }
        });
    }

    /**
     * @param billNumber bill's number
     */
    protected static void makePay(String billNumber) {
        RequestParams usrInfo = new RequestParams(MyApplication.URL + "/bill/pay");
        String sessionId = MyApplication.SESSION_NAME + "=" +
                MyApplication.getSession();
        usrInfo.addHeader("cookie", sessionId);
        usrInfo.addHeader("Content-Type", "application/x-www-form-urlencoded");
        System.out.print(usrInfo.getHeaders());
        usrInfo.addBodyParameter("billId", billNumber);

        System.out.println(usrInfo.toString());
        x.http().post(usrInfo, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                System.out.println(result);

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

            }
        });
    }


    private void loadOrderStage() {
//        orderList.addAll(TMP_ORDER_LIST);
        adapter = new OrderAdapter(orderList,"normal");
        orderContainer.setAdapter(adapter);
        adapter = new OrderAdapter(TMP_ORDER_LIST,"tmp");
        tmpOrderContainer.setAdapter(adapter);

        if (orderList.size() + TMP_ORDER_LIST.size() == 0) {
            TextView noOrderMsg = Objects.requireNonNull(getView()).findViewById(R.id.NoOrderMsg);
            noOrderMsg.setText("No Bill Found");
        } else {
            if (TMP_ORDER_LIST.size() != 0) {
                TextView tmpOrd = Objects.requireNonNull(getView()).findViewById(R.id.tmpOrderTitle);
                tmpOrd.setText("临时订单");
            }
            if (TMP_ORDER_LIST.size() != 0) {
                TextView tmpOrd = Objects.requireNonNull(getView()).findViewById(R.id.OrderTitle);
                tmpOrd.setText("普通订单");
            }
            TextView noOrderMsg = Objects.requireNonNull(getView()).findViewById(R.id.NoOrderMsg);
            noOrderMsg.setText("");
        }
    }


}
