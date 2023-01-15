package cse.SUSTC.ParkingApp;

import org.junit.Test;



import java.lang.reflect.Field;
import java.text.SimpleDateFormat;

import java.util.Locale;

import static org.junit.Assert.assertEquals;




public class OrderTest {
    private transient String classForName = "CSE.SUSTC.ParkingApp.Order";
    private transient String orderID = "orderId";
    private transient String price = "price";
    private transient String begin = "begin";

    private transient SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA);


    @Test
    public void getOrderIdTest1() throws Exception{
        Class reflectionUsage = Class.forName(classForName);
        Order o = (Order) reflectionUsage.newInstance();
        Field field = reflectionUsage.getDeclaredField(orderID);

        field.setAccessible(true);
        field.set(o,12);
        field.setAccessible(false);

        assertEquals("getOrderId method test unsuccessfully",12,o.getOrderId());
    }

    @Test
    public void getOrderIdTest2() throws Exception{
        Class reflectionUsage = Class.forName(classForName);
        Order o = (Order) reflectionUsage.newInstance();
        Field field = reflectionUsage.getDeclaredField(orderID);
        field.setAccessible(true);
        field.set(o,3);
        field.setAccessible(false);

        assertEquals("getOrderId method test unsuccessfully",3,o.getOrderId());
    }

    @Test
    public void getPriceTest1() throws Exception{
        Class reflectionUsage = Class.forName(classForName);
        Order o = (Order) reflectionUsage.newInstance();
        Field field = reflectionUsage.getDeclaredField(price);
        field.setAccessible(true);
        field.set(o,33.45);
        field.setAccessible(false);

        assertEquals("getPrice method test unsuccessfully","￥33.45",o.getPrice());
    }

    @Test
    public void getPriceTest2() throws Exception{
        Class reflectionUsage = Class.forName(classForName);
        Order o = (Order) reflectionUsage.newInstance();
        Field field = reflectionUsage.getDeclaredField(price);
        field.setAccessible(true);
        field.set(o,68.93);
        field.setAccessible(false);

        assertEquals("getPrice method test unsuccessfully","￥68.93",o.getPrice());
    }

    @Test
    public void getBeginTest1() throws Exception{

        Class reflectionUsage = Class.forName(classForName);
        Order o = (Order) reflectionUsage.newInstance();
        Field field = reflectionUsage.getDeclaredField(begin);
        field.setAccessible(true);
        field.set(o,sdf.parse("2018-09-07 12:23:51"));
        field.setAccessible(false);

        assertEquals("getBegin method test unsuccessfully","start: 2018-09-07 12:23:51",o.getBegin());

    }

    @Test
    public void getBeginTest2() throws Exception{

        Class reflectionUsage = Class.forName(classForName);
        Order o = (Order) reflectionUsage.newInstance();
        Field field = reflectionUsage.getDeclaredField(begin);
        field.setAccessible(true);
        field.set(o,sdf.parse("2019-03-02 10:23:51"));
        field.setAccessible(false);

        assertEquals("getBegin method test unsuccessfully","start: 2019-03-02 10:23:51",o.getBegin());

    }



}