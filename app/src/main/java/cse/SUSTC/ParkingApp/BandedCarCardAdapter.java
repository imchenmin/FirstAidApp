package cse.SUSTC.ParkingApp;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;
/**
 * A BandedCarCard adapter used by RecyclerView.
 */
public class BandedCarCardAdapter extends  RecyclerView.Adapter<BandedCarCardAdapter.ViewHolder>{
    private transient Context mContext;
    private transient List<Car> mCarList;

    /**
     * View holder used by outer class.
     */
    static class ViewHolder extends RecyclerView.ViewHolder{
        private transient TextView gPlateNum;
        private transient TextView gDescription;
        private transient TextView gOwnerName;
        private ViewHolder(View view) {
            super(view);
            gPlateNum =view.findViewById(R.id.PlateNumCar);
            gDescription =view.findViewById(R.id.Description);
            gOwnerName =view.findViewById(R.id.OwnerName);
        }
    }

    /**
     * An adapter to banded car card
     *
     * @param carList banded car's lisr
     */
    BandedCarCardAdapter(List<Car> carList) {
        this.mCarList = carList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (mContext==null){
            mContext=parent.getContext();
        }
        View view= LayoutInflater.from(mContext).inflate(R.layout.bounding_car_card,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(BandedCarCardAdapter.ViewHolder holder, int position) {
        Car car= mCarList.get(position);
        holder.gPlateNum.setText(car.getPlateNum());
        holder.gDescription.setText(car.getDescription());
        holder.gOwnerName.setText(car.getOwnerName());
    }

    @Override
    public int getItemCount() {
        return mCarList.size();
    }
}
