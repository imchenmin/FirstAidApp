package cse.SUSTC.ParkingApp;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * The order class which include all information of a order.
 */
public class Order {
    private transient String orderId;
    private transient double price;
    private transient Date begin;
    private transient Date end;
    private transient String billPlateNum;
    private transient boolean paid;

    private transient SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA);
    private transient SimpleDateFormat parkingFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA);

    /**
     * Instantiates a new Order.
     *
     * @param j a json object contains information of a order received from server
     */
    Order(JSONObject j) {

        try {
            orderId = j.getString("id");
            begin = sdf.parse(j.getString("begin").split("\\.")[0].replace("T", " "));
            billPlateNum = j.getString("carNumberPlate");
            if (!j.getString("end").equals("null")) {
                end = sdf.parse(j.getString("end").split("\\.")[0].replace("T", " "));
            } else {
                end = sdf.parse("1111-11-11 11:11:11");

            }
            price = j.getDouble("price");
            paid = j.getString("paid").equals("1");

        } catch (ParseException px) {
            px.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }


    }


    /**
     * Gets order id.
     *
     * @return id of the order
     */
    String getOrderId() {
        return orderId;
    }

    /**
     * Gets price.
     *
     * @return the price of bill
     */
    String getPrice() {
        return "￥"+String.valueOf(price);
    }

    /**
     * Gets start time of the bill.
     *
     * @return the start time of the bill
     */
    String getBegin() {
        return "start: "+parkingFormat.format((Date)begin.clone());
    }

    /**
     * Gets bill plate number of the bill.
     *
     * @return the start time of the bill
     */
    String getPlateNum() {
        return billPlateNum;
    }

    /**
     * @return status string
     */
    String getEnd() {
        System.out.println(end.clone().toString());
        if(end.clone().toString().endsWith("1111")){
            return "car is in slot now";
        }
        return "end: "+parkingFormat.format((Date)end.clone());
    }

    /**
     * Get the string that indicates current bill payment state.
     *
     * @return the Payment state string
     */
    protected String paymentState() {
        return paid?"paid":"unpaid";
    }

    /**
     * @param paid paid status
     */
    public void setPaid(boolean paid) {
        this.paid = paid;
    }
}
