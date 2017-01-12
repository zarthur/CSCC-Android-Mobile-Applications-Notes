package com.arthurneuman.mycontacts.database;

import android.database.Cursor;
import android.database.CursorWrapper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.arthurneuman.mycontacts.Contact;
import com.arthurneuman.mycontacts.database.ContactDbSchema.ContactTable;

import java.util.UUID;


public class ContactCursorWrapper extends CursorWrapper {
    public ContactCursorWrapper(Cursor cursor) {
        super(cursor);
    }

    public Contact getContact() {
        String uuid = getString(getColumnIndex(ContactTable.Cols.UUID));
        String name = getString(getColumnIndex(ContactTable.Cols.NAME));
        String email = getString(getColumnIndex(ContactTable.Cols.EMAIL));
        String favorite = getString(getColumnIndex(ContactTable.Cols.FAVORITE));
        String address = getString(getColumnIndex(ContactTable.Cols.ADDRESS));
        byte[] imageData = getBlob(getColumnIndex(ContactTable.Cols.IMAGE));
        // convert the byte array into a Bitmap
        Bitmap image = BitmapFactory.decodeByteArray(imageData, 0, imageData.length);


        Contact contact = new Contact(UUID.fromString(uuid));
        contact.setName(name);
        contact.setEmail(email);
        contact.setFavorite(favorite.equals("true"));
        contact.setAddress(address);
        contact.setImage(image);

        return contact;
    }
}
