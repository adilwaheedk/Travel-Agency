package com.visionxoft.abacus.rehmantravel.adapter;

import android.content.Context;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.visionxoft.abacus.rehmantravel.R;
import com.visionxoft.abacus.rehmantravel.model.Contact;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Maintain view and data of Contacts
 */
public class ContactListAdapter extends BaseAdapter implements Filterable {

    private ArrayList<Contact> list;
    private final ArrayList<Contact> allContacts;
    private LayoutInflater inflater;
    public HashMap<String, Contact> selected_contacts;

    /**
     * Maintain view and data of Contacts
     *
     * @param context Context
     * @param list    List of Contacts
     */
    public ContactListAdapter(Context context, ArrayList<Contact> list) {
        this.list = list;
        this.allContacts = list;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        selected_contacts = new HashMap<>();
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
                convertView = inflater.inflate(R.layout.row_contact_list, parent, false);
            } catch (InflateException e) {
                e.printStackTrace();
            }
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else
            holder = (ViewHolder) convertView.getTag();

        final Contact contact = list.get(position);
        holder.contact_name.setText(contact.name);
        holder.contact_number.setText(contact.phone);

        holder.contact_select_cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) selected_contacts.put(contact.id, contact);
                else selected_contacts.remove(contact.id);
            }
        });

        if (selected_contacts.get(contact.id) == null) holder.contact_select_cb.setChecked(false);
        else holder.contact_select_cb.setChecked(true);
        return convertView;
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults results = new FilterResults();
                ArrayList<Contact> filteredContacts = new ArrayList<>();

                if (constraint.length() > 0) {
                    constraint = constraint.toString().toLowerCase();
                    for (Contact contact : allContacts) {
                        if (contact.name.toLowerCase().contains(constraint) || contact.phone.contains(constraint))
                            filteredContacts.add(contact);
                    }
                } else filteredContacts = allContacts;

                results.count = filteredContacts.size();
                results.values = filteredContacts;

                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                if (results.count == 0) {
                    notifyDataSetInvalidated();
                } else {
                    list = (ArrayList<Contact>) results.values;
                    notifyDataSetChanged();
                }
            }
        };
    }

    public class ViewHolder {
        protected final TextView contact_name, contact_number;
        protected final CheckBox contact_select_cb;

        ViewHolder(View v) {
            contact_name = (TextView) v.findViewById(R.id.contact_name);
            contact_number = (TextView) v.findViewById(R.id.contact_number);
            contact_select_cb = (CheckBox) v.findViewById(R.id.contact_select_cb);
        }
    }
}