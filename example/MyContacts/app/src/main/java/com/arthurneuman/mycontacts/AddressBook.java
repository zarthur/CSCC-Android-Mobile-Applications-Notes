package com.arthurneuman.mycontacts;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;

import com.arthurneuman.mycontacts.database.ContactBaseHelper;
import com.arthurneuman.mycontacts.database.ContactCursorWrapper;
import com.arthurneuman.mycontacts.database.ContactDbSchema.ContactTable;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class AddressBook {
    private static AddressBook sAddressBook;
    private SQLiteDatabase mDatabase;

    private AddressBook(Context context) {
        mDatabase = new ContactBaseHelper(context).getWritableDatabase();
    }

    public static AddressBook get(Context context) {
        if (sAddressBook == null) {
            sAddressBook = new AddressBook(context);
        }
        return sAddressBook;
    }

    public void add(Contact contact) {
        ContentValues values = getContentValues(contact);
        mDatabase.insert(ContactTable.NAME, null, values);
    }


    public void updateContact(Contact contact) {
        String uuidString = contact.getID().toString();
        ContentValues values = getContentValues(contact);
        mDatabase.update(ContactTable.NAME, values,
                ContactTable.Cols.UUID  + " = ?",
                new String[] { uuidString });
    }

    public List<Contact> getContacts() {
        List<Contact> contacts = new ArrayList<>();
        // get all contacts
        ContactCursorWrapper cursorWrapper = queryContacts(null, null);
        try {
            cursorWrapper.moveToFirst();
            while (!cursorWrapper.isAfterLast()) {
                contacts.add(cursorWrapper.getContact());
                cursorWrapper.moveToNext();
            }
        }
        finally {
            cursorWrapper.close();
        }
        return contacts;
    }

    public Contact getContact(UUID id) {
        // get only contacts with matching UUID
        ContactCursorWrapper cursorWrapper = queryContacts(
                ContactTable.Cols.UUID + " = ?",
                new String[] { id.toString() }
        );
        try {
            if (cursorWrapper.getCount() == 0) {
                return null;
            }

            cursorWrapper.moveToFirst();
            return cursorWrapper.getContact();
        }
        finally {
            cursorWrapper.close();
        }

    }

    public List<Contact> getFavoriteContacts() {
        List<Contact> contacts = new ArrayList<>();
        // get only contacts with favorite == 1
        ContactCursorWrapper cursorWrapper = queryContacts(
                ContactTable.Cols.FAVORITE + " = ?",
                new String[] { "true" }
        );
        try {
            if (cursorWrapper.getCount() == 0) {
                return contacts;
            }

            cursorWrapper.moveToFirst();
            while (!cursorWrapper.isAfterLast()) {
                contacts.add(cursorWrapper.getContact());
                cursorWrapper.moveToNext();
            }
        }
        finally {
            cursorWrapper.close();
        }
        return contacts;
    }

    private static ContentValues getContentValues(Contact contact) {
        // convert image to a byte array for storage
        byte[] imageData = {};
        if (contact.getImage() != null) {
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            contact.getImage().compress(Bitmap.CompressFormat.PNG, 0, stream);
            imageData = stream.toByteArray();
        }

        ContentValues values = new ContentValues();
        values.put(ContactTable.Cols.UUID, contact.getID().toString());
        values.put(ContactTable.Cols.NAME, contact.getName());
        values.put(ContactTable.Cols.EMAIL, contact.getEmail());
        values.put(ContactTable.Cols.FAVORITE, contact.isFavorite() ? "true" : "false");
        values.put(ContactTable.Cols.ADDRESS, contact.getAddress());
        values.put(ContactTable.Cols.IMAGE, imageData);

        return values;
    }

    private ContactCursorWrapper queryContacts(String whereClause, String[] whereArgs) {
        Cursor cursor = mDatabase.query(ContactTable.NAME, null, whereClause, whereArgs,
                null, null, null, null);

        return new ContactCursorWrapper(cursor);
    }

}