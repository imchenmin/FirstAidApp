package cse.SUSTC.ParkingApp;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.List;

/**
 * A order adapter used by RecyclerView.
 */
public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.ViewHolder> {
    private final String type;
    private transient Context mContext;
    private transient List<Order> mOrderList;


    private void makePayment() {

        AlertDialog alertDialog1 = new AlertDialog.Builder(mContext)
                .setTitle("AliPay")
                .setMessage("Please make your payment in AliPay")
                .setPositiveButton("OK", null)
                .create();
        alertDialog1.show();

        try {
            final String alipayqr = "alipayqr://platformapi/startapp?clientVersion=3.7.0.0718";
            openUri(mContext, alipayqr);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 发送一个intent
     */
    private static void openUri(Context context, String s) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(s));
        context.startActivity(intent);
    }

    /**
     * View holder used by outer class.
     */
    static class ViewHolder extends RecyclerView.ViewHolder {
        private transient TextView orderId;
        private transient TextView billPlateNum;
        private transient TextView beginTime;
        private transient TextView totalFee;
        private transient TextView paymentState;
        private transient Button payButton;
        private transient CardView cardView;
        private transient TextView endTime;

        private ViewHolder(View view) {
            super(view);
            orderId = view.findViewById(R.id.OrderId);
            billPlateNum = view.findViewById(R.id.bill_PlateNum);
            beginTime = view.findViewById(R.id.ParkingTime);
            endTime = view.findViewById(R.id.endTime);
            totalFee = view.findViewById(R.id.TotalFee);
            paymentState = view.findViewById(R.id.PaymentState);
            payButton = view.findViewById(R.id.PayBtn);
            cardView = view.findViewById(R.id.Bill_x);


        }
    }

    /**
     * @param orderList order's list
     * @param type order's type
     */
    OrderAdapter(List<Order> orderList, String type) {
        this.mOrderList = orderList;
        this.type = type;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (mContext == null) {
            mContext = parent.getContext();
        }
        View view = LayoutInflater.from(mContext).inflate(R.layout.order_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final Order order = mOrderList.get(position);
        holder.orderId.setText(String.valueOf(order.getOrderId()));
        holder.billPlateNum.setText(order.getPlateNum());
        holder.beginTime.setText(order.getBegin());
        holder.endTime.setText(order.getEnd());
        holder.totalFee.setText(String.valueOf(order.getPrice()));
        holder.paymentState.setText(String.valueOf(order.paymentState()));
        if (order.paymentState().equals("paid")) {
            holder.paymentState.setTextColor(Color.parseColor("#4CAF50"));
            holder.payButton.setVisibility(View.INVISIBLE);
        } else {
            holder.paymentState.setTextColor(Color.parseColor("#E9643B"));
            holder.payButton.setVisibility(View.VISIBLE);
            holder.payButton.setTag(order.getOrderId());
            holder.payButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    makePayment();


                    OrderStage.makePay(v.getTag().toString());
                    order.setPaid(true);

                }
            });

        }

        if (type.equals("tmp")) {
            holder.cardView.setCardBackgroundColor(Color.parseColor("#C0FFA64C"));
        } else {
            holder.cardView.setCardBackgroundColor(Color.parseColor("#C0DEE8C8"));
        }
    }

    @Override
    public int getItemCount() {
        return mOrderList.size();
    }
}
