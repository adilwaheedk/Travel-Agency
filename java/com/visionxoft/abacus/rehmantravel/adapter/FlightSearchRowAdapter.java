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
import com.visionxoft.abacus.rehmantravel.model.PricedItinerary.AirItinerary.OriginDestinationOptions.*;
import com.visionxoft.abacus.rehmantravel.utils.FormatString;

import java.util.List;

/**
 * Maintain view and data of flight segment in each Itinerary (Inner ListView)
 */
class FlightSearchRowAdapter extends BaseAdapter {

    private List<OriginDestinationOption> list;
    private LayoutInflater inflater;
    private Context context;
    private final String arrow;

    /**
     * Maintain view and data of flight segment in each Itinerary
     *
     * @param context Context
     * @param list    List of origin and destination options
     */
    FlightSearchRowAdapter(Context context, List<OriginDestinationOption> list) {
        this.context = context;
        this.list = list;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        arrow = context.getString(R.string.arrow);
    }

    public int getCount() {
        return list.size();
    }

    public OriginDestinationOption getItem(int position) {
        return list.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        if (convertView == null) {
            try {
                convertView = inflater.inflate(R.layout.row_flight_search_child, parent, false);
            } catch (InflateException e) {
                e.printStackTrace();
            }
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else
            holder = (ViewHolder) convertView.getTag();

        final OriginDestinationOption od = getItem(position);

        String locationmap = "N/A";
        if (od._LocationMap.Location != null) locationmap = od._LocationMap.Location.replace("&rarr;", arrow);
        holder.locationmap.setText(locationmap);

        //OriginDestinationOption first_flight_segment = od._FlightSegment.get(0);
        //final int flight_segments = od._FlightSegment.size();
        //if (getCount() == 1) {
        //    //holder.locationmap.setText(od._LocationMap.Location);
        //    holder.durationstop.setText(R.string.non_stop);
        //} else {
        //    String location_map = first_flight_segment._DepartureAirport.LocationCode + arrow;
        //    int stops = Integer.valueOf(first_flight_segment._attr.StopQuantity);
        //
        //    for (int i = 1; i < flight_segments; i++) {
        //        location_map += od._DepartureAirport.LocationCode + arrow;
        //        stops += Integer.valueOf(od._FlightSegment.get(i)._attr.StopQuantity);
        //    }
        //
        //    location_map += od._FlightSegment.get(flight_segments - 1)._ArrivalAirport._attr.LocationCode;
        //
        //    holder.locationmap.setText(location_map);
        //}

        switch (od._attr.StopQuantity) {
            case "0":
                if (od.segment.equals("0")) {
                    holder.durationstop.setText("Non-stop");
                } else {
                    holder.durationstop.setText("0 Stop");
                }
                break;
            case "1":
                holder.durationstop.setText("1 Stop");
                break;
            default:
                holder.durationstop.setText(od._attr.StopQuantity + " Stops");
                break;
        }

        String airline_code = od._OperatingAirline.Code;
        holder.airlinelogo.setImageResource(context.getResources().getIdentifier(
                "_" + FormatString.filterString(airline_code), "drawable", context.getPackageName()));
        holder.airlinecode.setText(airline_code + "-" + od._OperatingAirline.FlightNumber);
        String departureDateTime = od._attr.DepartureDateTime;
        holder.departuretime.setText(FormatString.getTime(context, departureDateTime));
        holder.departuredate.setText(FormatString.getDate(context, departureDateTime));
        String arrivalDateTime = od._attr.ArrivalDateTime;
        holder.arrivaltime.setText(FormatString.getTime(context, arrivalDateTime));
        holder.arrivaldate.setText(FormatString.getDate(context, arrivalDateTime));
        holder.durationtime.setText(FormatString.convertMinutesToTime(od._attr.ElapsedTime));

        return convertView;
    }

    class ViewHolder {
        private final ImageView airlinelogo;
        private final TextView airlinecode, departuredate, departuretime, locationmap,
                arrivaltime, arrivaldate, durationtime, durationstop;

        ViewHolder(View v) {
            airlinelogo = (ImageView) v.findViewById(R.id.airlinelogo);
            airlinecode = (TextView) v.findViewById(R.id.airlinecode);
            departuredate = (TextView) v.findViewById(R.id.departuredate);
            departuretime = (TextView) v.findViewById(R.id.departuretime);
            locationmap = (TextView) v.findViewById(R.id.locationmap);
            arrivaltime = (TextView) v.findViewById(R.id.arrivaltime);
            arrivaldate = (TextView) v.findViewById(R.id.arrivaldate);
            durationtime = (TextView) v.findViewById(R.id.durationtime);
            durationstop = (TextView) v.findViewById(R.id.durationstop);
        }
    }
}