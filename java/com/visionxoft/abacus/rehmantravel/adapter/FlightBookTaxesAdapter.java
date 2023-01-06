package com.visionxoft.abacus.rehmantravel.adapter;

import android.content.Context;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.visionxoft.abacus.rehmantravel.R;
import com.visionxoft.abacus.rehmantravel.model.PricedItinerary.AirItineraryPricingInfo.PTC_FareBreakdowns.PTC_FareBreakdown.PassengerFare.Taxes.*;

import java.util.List;

/**
 * Maintain view and data of booking flight Taxes detail
 */
public class FlightBookTaxesAdapter extends BaseAdapter {

    private List<Tax> list;
    private LayoutInflater inflater;

    /**
     * Maintain view and data of booking flight Taxes detail
     *
     * @param context Context
     * @param list    List of Taxes
     */
    public FlightBookTaxesAdapter(Context context, List<Tax> list) {
        this.list = list;
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
                convertView = inflater.inflate(R.layout.row_flight_book_taxes_detail, parent, false);
            } catch (InflateException e) {
                e.printStackTrace();
            }
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else
            holder = (ViewHolder) convertView.getTag();

        final Tax tax = list.get(position);
        holder.tax_code.setText(tax.TaxCode);
        holder.tax_amount.setText(tax.Amount);
        holder.tax_currency.setText(tax.CurrencyCode);
        return convertView;
    }

    public class ViewHolder {
        protected final TextView tax_code, tax_amount, tax_currency;

        ViewHolder(View v) {
            tax_code = (TextView) v.findViewById(R.id.tax_code);
            tax_amount = (TextView) v.findViewById(R.id.tax_amount);
            tax_currency = (TextView) v.findViewById(R.id.tax_currency);
        }
    }
}