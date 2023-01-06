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
import com.visionxoft.abacus.rehmantravel.model.PricedItinerary.AirItinerary.OriginDestinationOptions.OriginDestinationOption;
import com.visionxoft.abacus.rehmantravel.model.PricedItinerary.AirItineraryPricingInfo.FareInfos.FareInfo;
import com.visionxoft.abacus.rehmantravel.utils.FormatString;

import java.util.HashMap;
import java.util.List;

/**
 * Maintain view and data of each flight segment detail
 */
public class FlightSearchDetailAdapter extends BaseAdapter {

    private List<OriginDestinationOption> list;
    private List<FareInfo> fareinfos;
    private HashMap<String, AirportLocation> airport_names;
    private HashMap<String, String> airline_names;
    private LayoutInflater inflater;
    private Context context;

    /**
     * Maintain view and data of each flight segment detail
     *
     * @param context       Context
     * @param list          List of Origin Destination Options
     * @param airport_names Map of airport codes and their locations
     * @param airline_names Map of airline codes and their names
     * @param fareinfos     List of fare info.
     */
    public FlightSearchDetailAdapter(Context context, List<OriginDestinationOption> list,
                                     HashMap<String, AirportLocation> airport_names,
                                     HashMap<String, String> airline_names, List<FareInfo> fareinfos) {
        this.context = context;
        this.list = list;
        this.fareinfos = fareinfos;
        this.airport_names = airport_names;
        this.airline_names = airline_names;
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
                convertView = inflater.inflate(R.layout.row_flight_detail, parent, false);
            } catch (InflateException e) {
                e.printStackTrace();
            }
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else holder = (ViewHolder) convertView.getTag();

        final OriginDestinationOption odo = list.get(position);
        setValues(holder, odo, position);
        return convertView;
    }

    private void setValues(ViewHolder holder, OriginDestinationOption odo, int position) {
        // Set values to the TextViews
        AirportLocation cac_origin = airport_names.get(odo._DepartureAirport.LocationCode);
        AirportLocation cac_dest = airport_names.get(odo._ArrivalAirport.LocationCode);
        String origin_location = cac_origin.location + ", " + cac_origin.country + " (" + cac_origin.iataCode + ")";
        holder.detorigin.setText(origin_location.trim());
        String destination_location = cac_dest.location + ", " + cac_dest.country + " (" + cac_dest.iataCode + ")";
        holder.detdestination.setText(destination_location.trim());
        holder.dettime.setText(FormatString.getTime(context, odo._attr.DepartureDateTime));

        // Flight Airline related values
        String airline_code = odo._OperatingAirline.Code;
        holder.detairlinelogo.setImageResource(context.getResources().getIdentifier(
                "_" + airline_code.toLowerCase().replace("-", "_").replace("(", ""), "drawable", context.getPackageName()));
        holder.detairlinecode.setText(airline_code + "-" + odo._OperatingAirline.FlightNumber);
        holder.detequiptype.setText(odo._Equipment.AirEquipType);
        holder.detairlinename.setText(airline_names.get(airline_code));

        // Flight Departure related values
        String depDateTime = odo._attr.DepartureDateTime;
        String origin_loc_code = odo._DepartureAirport.LocationCode;
        holder.depcodetime.setText(origin_loc_code + " (" + FormatString.getTime(context, depDateTime) + ")");
        holder.depdate.setText(FormatString.getDate(context, depDateTime));
        holder.deploc.setText(origin_location);

        // Flight Arrival related values
        String arrDateTime = odo._attr.ArrivalDateTime;
        String arrival_code = odo._ArrivalAirport.LocationCode;
        holder.arrcodetime.setText(arrival_code + " (" + FormatString.getTime(context, arrDateTime) + ")");
        holder.arrdate.setText(FormatString.getDate(context, arrDateTime));
        holder.arrloc.setText(destination_location);

        // Flight Duration value
        holder.detduration.setText(FormatString.convertMinutesToTime(odo._attr.ElapsedTime));

        // Flight class related values
        holder.detclass.setText(odo._attr.ResBookDesigCode);
        holder.detseats.setText("Seats (" + fareinfos.get(position)._TPA_Extensions._SeatsRemaining.Number + ")");
    }

    private class ViewHolder {
        protected final ImageView detairlinelogo;
        protected final TextView detorigin, detdestination, dettime, detairlinecode, detequiptype, detairlinename, detduration,
                detclass, detseats, depcodetime, depdate, deploc, arrcodetime, arrdate, arrloc;

        ViewHolder(View v) {
            detairlinelogo = (ImageView) v.findViewById(R.id.detairlinelogo);
            detorigin = (TextView) v.findViewById(R.id.detorigin);
            detdestination = (TextView) v.findViewById(R.id.detdestination);
            dettime = (TextView) v.findViewById(R.id.dettime);
            detairlinecode = (TextView) v.findViewById(R.id.detairlinecode);
            detequiptype = (TextView) v.findViewById(R.id.detequiptype);
            detairlinename = (TextView) v.findViewById(R.id.detairlinename);
            detduration = (TextView) v.findViewById(R.id.detduration);
            detclass = (TextView) v.findViewById(R.id.detclass);
            detseats = (TextView) v.findViewById(R.id.detseats);
            depcodetime = (TextView) v.findViewById(R.id.depcodetime);
            depdate = (TextView) v.findViewById(R.id.depdate);
            deploc = (TextView) v.findViewById(R.id.deploc);
            arrcodetime = (TextView) v.findViewById(R.id.arrcodetime);
            arrdate = (TextView) v.findViewById(R.id.arrdate);
            arrloc = (TextView) v.findViewById(R.id.arrloc);
        }
    }
}