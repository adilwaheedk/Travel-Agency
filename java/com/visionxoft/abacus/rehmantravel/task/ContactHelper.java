package com.visionxoft.abacus.rehmantravel.task;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.provider.ContactsContract.Contacts;
import android.provider.ContactsContract.CommonDataKinds.Phone;

import com.visionxoft.abacus.rehmantravel.model.Contact;

import java.util.ArrayList;
import java.util.List;

public class ContactHelper {


    /**
     * Get all contacts from PhoneBook
     *
     * @param context Context
     * @return List of contacts
     */
    public static ArrayList<Contact> getAllContacts(Context context) {
        ContentResolver cr = context.getContentResolver();
        String[] projection = new String[]{Contacts._ID, Contacts.DISPLAY_NAME};
        Cursor cursor = cr.query(Contacts.CONTENT_URI, projection, null, null, null);
        ArrayList<Contact> contactList = new ArrayList<>();
        while (cursor.moveToNext()) {
            contactList.addAll(getContactFromCursor(cr, cursor));
        }
        cursor.close();
        return contactList;
    }


    /**
     * Get list of contacts from PhoneBook
     *
     * @param context Context
     * @param name    Name to search in PhoneBook
     * @return List of contacts
     */
    public static List<Contact> getContactByName(Context context, String name) {
        ContentResolver cr = context.getContentResolver();
        String selection = Phone.DISPLAY_NAME + " like'%" + name + "%' and " + Contacts.HAS_PHONE_NUMBER + " = 1";
        String[] projection = new String[]{Contacts._ID, Contacts.DISPLAY_NAME, Contacts.HAS_PHONE_NUMBER};
        Cursor cursor = cr.query(Contacts.CONTENT_URI, projection, selection, null, null);
        List<Contact> contactList = new ArrayList<>();
        while (cursor.moveToNext()) {
            contactList.addAll(getContactFromCursor(cr, cursor));
        }
        cursor.close();
        return contactList;
    }

    /**
     * Retrieve list of contacts from cursor object
     *
     * @param cr     Content Resolver object
     * @param cursor Cursor object containing tabular data
     * @return List of contacts
     */
    private static List<Contact> getContactFromCursor(ContentResolver cr, Cursor cursor) {
        List<Contact> contactsList = new ArrayList<>();
        Contact contact;
        String contact_id = cursor.getString(cursor.getColumnIndex(Contacts._ID));
        String contact_name = cursor.getString(cursor.getColumnIndex(Contacts.DISPLAY_NAME));
        Cursor cur = cr.query(Phone.CONTENT_URI, null, Phone.CONTACT_ID + " = ?", new String[]{contact_id}, null);
        while (cur.moveToNext()) {
            contact = new Contact();
            contact.id = contact_id + cur.getPosition();
            contact.name = contact_name;
            contact.phone = cur.getString(cur.getColumnIndex(Phone.NUMBER));
            contactsList.add(contact);
        }
        cur.close();
        return contactsList;
    }
}
