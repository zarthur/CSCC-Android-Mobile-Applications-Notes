package com.arthurneuman.mycontacts.network;

import android.util.Log;

import com.arthurneuman.mycontacts.BuildConfig;
import com.arthurneuman.mycontacts.Contact;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URL;
import java.util.List;
import java.util.UUID;

public class APIWrapper {
    private String baseURL;
    private HttpRequest mHttpRequest;
    private Gson gson;

    private final static String LIST_CONTACTS_URL = "/contacts/api/v1.0/contacts";
    private final static String NEW_CONTACT_URL = "/contacts/api/v1.0/contact/create";
    private final static String LOGTAG = APIWrapper.class.getSimpleName();

    public APIWrapper(String baseURL, String username, String password) {
        this.baseURL = baseURL;
        mHttpRequest = new HttpRequest(username, password);
        GsonBuilder builder = new GsonBuilder();
        builder.registerTypeAdapter(Contact.class, new ContactSerializer());
        builder.registerTypeAdapter(Contact.class, new ContactDeserializer());
        gson = builder.create();
    }

    public List<Contact> getContacts() {
        try {
            URL url = new URL(baseURL + LIST_CONTACTS_URL);
            String serverData = mHttpRequest.get(url);
            ContactContainer contactContainer = gson.fromJson(serverData, ContactContainer.class);
            return contactContainer.contacts;
        }
        catch (IOException e) {
            if (BuildConfig.DEBUG) {
                Log.e(LOGTAG, "Unable to get contacts. " + e.getMessage());
            }
        }
        return null;
    }

    public void createContact(Contact contact) {
        try {
            URL url = new URL(baseURL + NEW_CONTACT_URL);
            String jsonData = gson.toJson(contact);
            mHttpRequest.post(url, "application/json", jsonData);
        }
        catch (IOException e) {
            if (BuildConfig.DEBUG) {
                Log.e(LOGTAG, "Unable to create contact. " + e.getMessage());
            }
        }

    }
}


class ContactContainer {
    public List<Contact> contacts;
}

class ContactSerializer implements JsonSerializer<Contact> {
    @Override
    public JsonElement serialize(Contact src, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject object = new JsonObject();
        object.addProperty("name", src.getName());
        object.addProperty("email", src.getEmail());
        object.addProperty("address", src.getAddress());
        object.addProperty("uuid", src.getID().toString());
        object.addProperty("favorite", src.isFavorite());
        return object;
    }
}

class ContactDeserializer implements JsonDeserializer<Contact> {
    @Override
    public Contact deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject object = (JsonObject) json;
        Contact contact = new Contact(UUID.fromString(object.get("uuid").getAsString()));
        contact.setName(object.get("name").getAsString());
        contact.setEmail(object.get("email").getAsString());
        contact.setAddress(object.get("address").getAsString());
        return contact;
    }
}