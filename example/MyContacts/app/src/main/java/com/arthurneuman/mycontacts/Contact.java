package com.arthurneuman.mycontacts;

import android.graphics.Bitmap;

import java.util.UUID;

public class Contact {
    private UUID mID;
    private String mName;
    private String mEmail;
    private boolean mIsFavorite;
    private String mAddress;
    private Bitmap mImage;

    public Contact() {
        this(UUID.randomUUID());
    }

    public Contact(UUID id) {
        mID = id;
    }

    public UUID getID() {
        return mID;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public String getEmail() {
        return mEmail;
    }

    public void setEmail(String email) {
        mEmail = email;
    }

    public boolean isFavorite() {
        return mIsFavorite;
    }

    public void setFavorite(boolean favorite) {
        mIsFavorite = favorite;
    }

    public String getAddress() {
        return mAddress;
    }

    public void setAddress(String address) {
        mAddress = address;
    }

    public Bitmap getImage() {
        return mImage;
    }

    public void setImage(Bitmap mImage) {
        this.mImage = mImage;
    }
}