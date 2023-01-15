package cse.SUSTC.ParkingApp;

import org.json.JSONException;
import org.json.JSONObject;

class ParkSlot {
    private double latitude;
    private double longitude;
    String id;
    String lotDescribption;

    /**
     * @return lot Description
     */
    public String getLotDescribption() {
        return lotDescribption;
    }

    private boolean status;


    /**
     * @return latitude
     */
    public double getLatitude() {
        return latitude;
    }

    /**
     * @param latitude position's latitude
     */
    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    /**
     * @return longitude
     */
    public double getLongitude() {
        return longitude;
    }

    /**
     * @param longitude position's longitude
     */
    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    /**
     * @return id
     */
    public String getId() {
        return id;
    }

    /**
     * @param id set id
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * @return status
     */
    public boolean isStatus() {
        return status;
    }

    /**
     * @param status set status
     */
    public void setStatus(boolean status) {
        this.status = status;
    }

    /**
     * @param j position's json data
     * @throws JSONException null object
     */
    ParkSlot(JSONObject j) throws JSONException {
        latitude = j.getDouble("latitude");
        longitude = j.getDouble("longitude");
        id = j.getString("id");
        status = j.getInt("status") == 0;
        lotDescribption = j.getString("lotDescribption");

    }
}
