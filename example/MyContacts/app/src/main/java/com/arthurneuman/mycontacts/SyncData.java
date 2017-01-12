package com.arthurneuman.mycontacts;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.preference.PreferenceManager;

import com.arthurneuman.mycontacts.network.APIWrapper;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class SyncData extends AsyncTask<Void, Void, Void> {
    private Context mContext;

    private final static String LOGTAG = SyncData.class.getSimpleName();

    public SyncData(Context context) {
        mContext = context;
    }

    // sync data - for simplicity this will only add new contacts but will not update existing
    // contacts
    @Override
    protected Void doInBackground(Void... voids) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(mContext);
        String baseURL = sharedPref.getString("pref_key_sync_server", "");
        String username = sharedPref.getString("pref_key_sync_username", "");
        String password = sharedPref.getString("pref_key_sync_password", "");
        if (baseURL.equals("") || username.equals("") || password.equals("")) {
            return null;
        }

        APIWrapper apiWrapper = new APIWrapper(baseURL, username, password);
        AddressBook addressBook = AddressBook.get(mContext);
        List<Contact> localContacts = addressBook.getContacts();

        List<Contact> remoteContacts = apiWrapper.getContacts();
        // if server returns an invalid response, remoteContacts will be null
        if (remoteContacts == null) {
            return null;
        }


        List<UUID> localUUIDs = new ArrayList<>();
        List<UUID> remoteUUIDs = new ArrayList<>();

        for (Contact c: localContacts) {
            localUUIDs.add(c.getID());
        }

        for (Contact c: remoteContacts) {
            remoteUUIDs.add(c.getID());
        }

        // update remote server
        for (Contact c: localContacts) {
            if (!remoteUUIDs.contains(c.getID())) {
                apiWrapper.createContact(c);
            }
        }

        // update local
        for (Contact c: remoteContacts) {
            if (!localUUIDs.contains(c.getID())) {
                addressBook.add(c);
            }
        }
        return null;
    }
}
