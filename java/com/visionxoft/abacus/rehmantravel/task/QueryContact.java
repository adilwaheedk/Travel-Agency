package com.visionxoft.abacus.rehmantravel.task;

import android.content.Context;
import android.os.AsyncTask;
import android.view.View;

import com.visionxoft.abacus.rehmantravel.adapter.ContactListAdapter;
import com.visionxoft.abacus.rehmantravel.fragment.FlightReservationFragment;
import com.visionxoft.abacus.rehmantravel.model.Contact;

import java.util.ArrayList;

/**
 * AsyncTask to request list of contacts from input name or phone number
 */
public class QueryContact extends AsyncTask<Void, Void, Integer> {
    private FlightReservationFragment fragment;
    private Context context;
    private ArrayList<Contact> contactList;

    /**
     * Query and display list of Contacts
     *
     * @param fragment Parent Fragment Class
     */
    public QueryContact(FlightReservationFragment fragment) {
        this.fragment = fragment;
        this.context = fragment.getContext();
    }

    @Override
    protected Integer doInBackground(Void... params) {
        contactList = ContactHelper.getAllContacts(context);
        if (contactList != null && contactList.size() > 0) return 1;
        else return 0;
    }

    @Override
    protected void onPostExecute(Integer feedback) {
        super.onPostExecute(feedback);
        try {
            fragment.send_sms_progress.setVisibility(View.GONE);
            fragment.send_sms_progress_title.setVisibility(View.GONE);
            if (feedback == 1) {
                fragment.contactListAdapter = new ContactListAdapter(context, contactList);
                fragment.send_sms_contact_list.setAdapter(fragment.contactListAdapter);
                fragment.send_sms_mactv_ll.setVisibility(View.VISIBLE);
                fragment.send_sms_contact_list.setVisibility(View.VISIBLE);
            } else fragment.send_sms_contact_rl.setVisibility(View.GONE);
        } catch (Exception ignored) {
            // Ignored because fragment is not visible anymore
        }
    }
}