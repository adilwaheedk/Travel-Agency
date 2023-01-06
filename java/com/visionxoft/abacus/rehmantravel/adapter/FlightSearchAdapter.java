package com.visionxoft.abacus.rehmantravel.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.visionxoft.abacus.rehmantravel.R;
import com.visionxoft.abacus.rehmantravel.fragment.SearchFragment;
import com.visionxoft.abacus.rehmantravel.model.PricedItinerary;
import com.visionxoft.abacus.rehmantravel.model.PricedItinerary.AirItinerary.OriginDestinationOptions.*;
import com.visionxoft.abacus.rehmantravel.task.SecureWebService;
import com.visionxoft.abacus.rehmantravel.views.LayoutHack;

import java.util.ArrayList;
import java.util.List;

/**
 * Maintain view and pricedItineraries of Low Fare available flights coming from Server
 */
public class FlightSearchAdapter extends RecyclerView.Adapter<FlightSearchAdapter.MyViewHolder> {

    private SearchFragment searchFragment;
    private Context context;
    private List<PricedItinerary> pricedItineraries;
    private boolean button_clicked = false;

    /**
     * Maintain view and pricedItineraries of Low Fare available flights coming from Server
     *
     * @param searchFragment    Parent fragment class
     * @param pricedItineraries List of Itineraries
     */
    public FlightSearchAdapter(SearchFragment searchFragment, List<PricedItinerary> pricedItineraries) {
        try {
            this.searchFragment = searchFragment;
            this.context = searchFragment.getContext();
            this.pricedItineraries = pricedItineraries;
        } catch (Exception ignored) {
            // Ignore because fragment is no longer visible
        }
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_flight_search_parent, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {

        final PricedItinerary _PricedItinerary = pricedItineraries.get(position);

        final int pos = position;

        // Book Button
        holder.buttonbook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!button_clicked) {
                    button_clicked = true;
                    // Access web service
                    SecureWebService.accessAPI(searchFragment, _PricedItinerary);
                    button_clicked = false;
                }
            }
        });

        // Details Button
        holder.detailbtnll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!button_clicked) {
                    button_clicked = true;
                    if (searchFragment.detail_PricedItinerary.containsKey(pos)) {
                        searchFragment.showTripDetailDialog(searchFragment.detail_PricedItinerary.get(pos),
                                searchFragment.detail_airport_names.get(pos),
                                searchFragment.detail_airline_names.get(pos));
                    } else {
                        // Access web service
                        SecureWebService.accessAPI(searchFragment, _PricedItinerary, pos);
                    }
                    button_clicked = false;
                }
            }
        });

        holder.currency.setText(_PricedItinerary._AirItineraryPricingInfo.get(0)._ItinTotalFare._TotalFare.CurrencyCode);
        holder.amount.setText(_PricedItinerary._AirItineraryPricingInfo.get(0)._ItinTotalFare._TotalFare.Amount);

        final List<OriginDestinationOption> list = new ArrayList<>();
        for(OriginDestinationOption odo: _PricedItinerary._AirItinerary._OriginDestinationOptions._OriginDestinationOption) {
            if (odo.segment.equals("0") || odo.segment.equals("1")) {
                list.add(odo);
            }
        }

        FlightSearchRowAdapter adapter = new FlightSearchRowAdapter(context, list);
        holder.listView_flight_row = LayoutHack.setListViewHeightBasedOnChildren(adapter, holder.listView_flight_row);
        holder.listView_flight_row.setAdapter(adapter);
    }

    @Override
    public long getItemId(int position) {
        return Long.parseLong(String.valueOf(position));
    }

    @Override
    public int getItemCount() {
        return pricedItineraries.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        protected final TextView currency, amount;
        protected final View buttonbook, detailbtnll;
        protected ListView listView_flight_row;

        MyViewHolder(View v) {
            super(v);
            currency = (TextView) v.findViewById(R.id.currency);
            amount = (TextView) v.findViewById(R.id.amount);
            buttonbook = v.findViewById(R.id.buttonbook);
            detailbtnll = v.findViewById(R.id.detailbtnll);
            listView_flight_row = (ListView) v.findViewById(R.id.listView_flight_row);
        }
    }
}