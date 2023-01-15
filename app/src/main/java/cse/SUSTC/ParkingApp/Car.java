package cse.SUSTC.ParkingApp;
import org.json.JSONException;
import org.json.JSONObject;
/**
 * Instantiates a new Car card.
 *
 * @param j a json object contains information of a car card received from server
 */
class Car {
    private transient String description;
    private transient String plateNum;
    private transient String ownerName;

    /**
     * object of car
     *
     * @param j json data of car's info
     */
    Car(JSONObject j) {

        try {
            plateNum = j.getString("numberplate");
            description = j.getString("description");
            ownerName = j.getString("username");
//
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    /**
     * Gets car plateNum.
     *
     * @return plateNum
     */
    String getPlateNum() {
        return plateNum;
    }

    /**
     * Gets description.
     *
     * @return the description
     */
    String getDescription() {
        return description;
    }

    /**
     * Gets ownerName.
     *
     * @return the ownerName
     */
    String getOwnerName() {
        return ownerName;
    }

}
