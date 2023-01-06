package com.visionxoft.abacus.rehmantravel.adapter;

import android.content.Context;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.visionxoft.abacus.rehmantravel.R;
import com.visionxoft.abacus.rehmantravel.model.AirportLocation;
import com.visionxoft.abacus.rehmantravel.model.PricedItinerary.AirItinerary.OriginDestinationOptions.*;
import com.visionxoft.abacus.rehmantravel.utils.FormatString;

import java.util.HashMap;
import java.util.List;

/**
 * Maintain view and data of booking flight segments summary
 */
public class FlightBookFlightSummaryAdapter extends BaseAdapter {

    private List<OriginDestinationOption> list;
    private HashMap<String, AirportLocation> airport_names;
    private LayoutInflater inflater;
    private Context context;

    /**
     * Maintain view and data of booking flight segments summary
     *
     * @param context       Context
     * @param list          List of Origin Destination Option
     * @param airport_names Map of airport codes and their locations
     */
    public FlightBookFlightSummaryAdapter(Context context, List<OriginDestinationOption> list,
                                          HashMap<String, AirportLocation> airport_names) {
        this.context = context;
        this.list = list;
        this.airport_names = airport_names;
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
                convertView = inflater.inflate(R.layout.row_flight_book_flight_summary, parent, false);
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
        String depDateTime = fs._attr.DepartureDateTime;

        String airline_code = fs._OperatingAirline.Code;
        holder.sumairlinelogo.setImageResource(context.getResources().getIdentifier(
                "_" + airline_code.toLowerCase().replace("-", "_").replace("(", ""), "drawable", context.getPackageName()));
        holder.sumairlinecode.setText(airline_code + "-" + fs._OperatingAirline.FlightNumber);

        holder.sumdepartdetail.setText(origin_location + "\n" + FormatString.getDate(context, depDateTime) +
                " (" + FormatString.getTime(context, depDateTime) + ")");

        String arrDateTime = fs._attr.ArrivalDateTime;
        holder.sumarrivaldetail.setText(destination_location + "\n" + FormatString.getDate(context, arrDateTime) +
                " (" + FormatString.getTime(context, arrDateTime) + ")");

        return convertView;
    }

    public class ViewHolder {
        protected final ImageView sumairlinelogo;
        protected final TextView sumairlinecode, sumdepartdetail, sumarrivaldetail;

        ViewHolder(View v) {
            sumairlinelogo = (ImageView) v.findViewById(R.id.frairlinelogo);
            sumairlinecode = (TextView) v.findViewById(R.id.frairlinecode);
            sumdepartdetail = (TextView) v.findViewById(R.id.frdepartdetail);
            sumarrivaldetail = (TextView) v.findViewById(R.id.frarrivaldetail);
        }
    }
}