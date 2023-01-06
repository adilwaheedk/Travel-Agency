package com.visionxoft.abacus.rehmantravel.adapter;

import android.content.Context;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.visionxoft.abacus.rehmantravel.R;
import com.visionxoft.abacus.rehmantravel.model.AirportLocation;
import com.visionxoft.abacus.rehmantravel.model.PricedItinerary.AirItinerary.OriginDestinationOptions.OriginDestinationOption;

import java.util.HashMap;
import java.util.List;

/**
 * Maintain view and data of booking flight segments summary
 */
public class FlightBookFareRulesAdapter extends BaseAdapter {

    private List<OriginDestinationOption> list;
    private HashMap<String, AirportLocation> airport_names;
    private HashMap<Integer, String> fare_rules;
    private LayoutInflater inflater;
    private Context context;

    /**
     * Maintain view and data of booking flight segments summary
     *
     * @param context       Context
     * @param list          List of Origin Destination Option
     * @param airport_names Map of airport codes and their locations
     */
    public FlightBookFareRulesAdapter(Context context, List<OriginDestinationOption> list,
                                      HashMap<String, AirportLocation> airport_names,
                                      HashMap<Integer, String> fare_rules ) {
        this.context = context;
        this.list = list;
        this.airport_names = airport_names;
        this.fare_rules = fare_rules;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public int getCount() {
        return list.size();
    }

    public Object getItem(int position) {
        return list.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        if (convertView == null) {
            try {
                convertView = inflater.inflate(R.layout.row_flight_book_fare_rules, parent, false);
            } catch (InflateException e) {
                e.printStackTrace();
            }
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else
            holder = (ViewHolder) convertView.getTag();

        final OriginDestinationOption fs = list.get(position);

        // Set values to the TextViews
        AirportLocation cac_origin = airport_names.get(fs._DepartureAirport.LocationCode);
        AirportLocation cac_dest = airport_names.get(fs._ArrivalAirport.LocationCode);
        String origin_location = cac_origin.location + ", " + cac_origin.country + " (" + cac_origin.iataCode + ")";
        String destination_location = cac_dest.location + ", " + cac_dest.country + " (" + cac_dest.iataCode + ")";

        String airline_code = fs._OperatingAirline.Code;
        holder.frairlinecode.setText(airline_code + "-" + fs._OperatingAirline.FlightNumber);
        holder.frdepartdetail.setText(origin_location);
        holder.frarrivaldetail.setText(destination_location);
        holder.frfarerules.setText(fare_rules.get(position));

        return convertView;
    }

    public class ViewHolder {
        final TextView frairlinecode, frdepartdetail, frarrivaldetail, frfarerules;

        ViewHolder(View v) {
            frairlinecode = (TextView) v.findViewById(R.id.frairlinecode);
            frdepartdetail = (TextView) v.findViewById(R.id.frdepartdetail);
            frarrivaldetail = (TextView) v.findViewById(R.id.frarrivaldetail);
            frfarerules = (TextView) v.findViewById(R.id.frfarerules);
        }
    }
}