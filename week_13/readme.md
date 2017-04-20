# Week 13 - Consuming RESTful Services

## Corresponding Text
https://developer.android.com/guide/topics/ui/settings.html; 
*Android Programming*, pp. 405-428

Previously, we worked on saving contact information so that it would be 
available after the app was restarted.  What if we wanted to allow users to 
view their data from a website or from another device?  One way to do that is 
to copy the data to a remote server.  We'll look at how we can use a RESTful 
service to sync our contact data with a remote location.  

For a review of JSON, HTTP, and REST, see the 
[week 14](https://github.com/zarthur/CSCC-Fundamentals-Android-Notes/blob/master/week_14/readme.md) 
and 
[week 15](https://github.com/zarthur/CSCC-Fundamentals-Android-Notes/blob/master/week_15/readme.md) 
lectures from the 
[Fundamentals of Android Development](https://github.com/zarthur/CSCC-Fundamentals-Android-Notes/blob/master/README.md) 
course.

## Preferences
Before we work on connecting to a remote server to upload/download our contact 
data, we need to provide the user with a way of specifying connection details: 
the address of the server, a username, and a password.  Android provides the 
Preferences API as a means of storing preferences and presenting the user with 
an interface to make changes to it.  

Let's add some string resources to the strings resource file:

```xml
    <string name="settings_sync_category">Sync Settings</string>
    <string name="settings_sync_server">Server Address</string>
    <string name="settings_sync_username">Username</string>
    <string name="settings_sync_password">Password</string>
```

We need to define the preferences that a user will be able to specify 
using an XML file.  In Android Studio, right-click the `res` resource folder 
and select **New Resource File**; enter `preferences.xml` for the file name and 
select `XML` for the resource type.  In the design view, drag the 
*PreferenceCategory* object to the view - this will allow us to group the sync 
settings together; set its title to `@string/settings_sync_category`.  Next, 
drag three *EditTextPreference* objects to the view and set their titles to the 
remaining strings we added to the string resource file. Use an empty string for 
their default values.  

Preference data is stored in a key-value store. To access a specific setting, 
we'll need to use a predfined key.  For each *EditTextPreference* widget, 
we can specify the key.  Set the keys as follows:

- `pref_key_sync_server`
- `pref_key_sync_username`
- `pref_key_sync_password`

Finally, set the **password** property on the *EditTextPreference* widget used 
to store the password to true and the **inputType** to `textPassword` - this 
will mask the characters that are entered.

The XML in `preferences.xml` should look similar to the following:

```xml
<?xml version="1.0" encoding="utf-8"?>
<PreferenceScreen xmlns:android="http://schemas.android.com/apk/res/android">

    <PreferenceCategory
        android:title="@string/settings_sync_category">

        <EditTextPreference
            android:defaultValue=""
            android:selectAllOnFocus="true"
            android:singleLine="true"
            android:title="@string/settings_sync_server"
            android:key="pref_key_sync_server"/>
        <EditTextPreference
            android:defaultValue=""
            android:selectAllOnFocus="true"
            android:singleLine="true"
            android:title="@string/settings_sync_username"
            android:key="pref_key_sync_username"/>
        <EditTextPreference
            android:defaultValue=""
            android:selectAllOnFocus="true"
            android:singleLine="true"
            android:title="@string/settings_sync_password"
            android:key="pref_key_sync_password"
            android:password="true"
            android:inputType="textPassword"/>
    </PreferenceCategory>
</PreferenceScreen>
```

To display the preferences interface, we could create a subclass of 
*PreferenceFragment*. Because we are using the compatibility support library 
version of *Fragment* elsewhere in our code, we should, however, use 
*PreferenceFragmentCompat*. For this, we'll need to add a dependency to the 
app's `build.gradle` file:

```
compile 'com.android.support:preference-v7:25.0.1'
```

For now, our new class will simply handle inflating the layout and 
making the preferences available to the rest of the app.  Create a new Java 
class named *SettingsFragment* and add the following code:

```java
public class SettingsFragment extends PreferenceFragmentCompat{
    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.preferences, rootKey);
    }
}
```

Because we are using the compatibility support library, we will also have to 
specify a theme for use with the preference screen.  Add the following to the 
**style** element in `res/values/styles.xml`:

```
<item name="preferenceTheme">@style/PreferenceThemeOverlay</item>
```

We'll host the new settings fragment in a way similar to *AddressBookFragment*, 
we'll create a new subclass of *SingleFragmentActivity* named 
*SettingsActivity*:

```java
public class SettingsActivity extends SingleFragmentActivity {
    @Override
    protected Fragment createFragment() {
        return new SettingsFragment();
    }
}
```

Be sure to add the activity to the app's manifest:

```xml
<manifest ...>
    ...

    <activity android:name=".SettingsActivity"
        android:parentActivityName=".AddressBookActivity">
    </activity>

    ...
</manifest>
```

To access the new fragment, we'll add a menu item to an existing menu. Before 
we do that, let's add another string resource to `res/values/strings.xml`:

```xml
    <string name="settings">Settings</string>
```

Next, add a menu item to the *AddressBookFragment* menu, 
`res/menu/fragment_address_book.xml`. Set the title of the new item to 
the `@string/settings` string resource and the id to `menu_item_sync_settings`. 

The menu XML should look like this:

```xml
<?xml version="1.0" encoding="utf-8"?>
<menu xmlns:app="http://schemas.android.com/apk/res-auto"
    xmlns:android="http://schemas.android.com/apk/res/android">

    <item android:title="@string/new_contact"
        android:id="@+id/menu_item_create_contact"
        android:icon="@android:drawable/ic_menu_add"
        app:showAsAction="ifRoom|withText"/>
    <item android:title="@string/show_favorites"
        android:id="@+id/menu_item_toggle_favorites"
        />
    <item android:title="@string/settings"
          android:id="@+id/menu_item_sync_settings"/>
</menu>
```

We'll also need to add code to display the preferences screen when the menu 
item is selected to *AddressBookFragment*: 

```java
public class AddressBookFragment extends Fragment {

    ...

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item_create_contact:
                Contact contact = new Contact();
                AddressBook.get(getContext()).add(contact);
                mCallbacks.onContactSelected(contact);
                return true;
            case R.id.menu_item_toggle_favorites:
                mShowFavoritesOnly = !mShowFavoritesOnly;
                if (mShowFavoritesOnly) {
                    item.setTitle(R.string.show_all);
                    mContactAdapter.mContacts =
                            AddressBook.get(getContext()).getFavoriteContacts();
                }
                else {
                    item.setTitle(R.string.show_favorites);
                    mContactAdapter.mContacts =
                            AddressBook.get(getContext()).getContacts();
                }
                mContactAdapter.notifyDataSetChanged();
                return true;
            case R.id.menu_item_sync_settings:
                Intent settingsIntent = new Intent(getContext(), SettingsActivity.class);
                startActivity(settingsIntent);
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    
    ...
}
```
## Syncing Data
Now that we've provided that user with a way of specifying the URL of the 
remote server and a username and password to use to connect to that server, 
we're ready to add code.  

Before we add the code to our app, we'll need to add the Gson library as a 
dependency. Add the following to the app's build.gradle file:

```
compile 'com.google.code.gson:gson:2.8.0'
```

First, let's create a new package named `network` to store our code 
responsible for connecting to a remote server and processing the request and 
response data.  

Next, create an *HttpRequests* class; this will store the code used to make 
GET and POST HTTP request.  The code for the class is as follows:

```java
public class HttpRequest {
    // set username and password for basic auth with HttpURLConnection
    public HttpRequest(final String username, final String password) {
        Authenticator.setDefault(new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password.toCharArray());
            }
        });
    }

    // process the response from an HttpURLConnection and return a string
    private String processResponse(HttpURLConnection connection) throws IOException {
        try {
            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                throw new IOException(connection.getResponseMessage() + ", url: "
                        + connection.getURL());
            }

            InputStreamReader inputStreamReader
                    = new InputStreamReader(connection.getInputStream());
            BufferedReader input = new BufferedReader(inputStreamReader);
            String response = "";
            String line;
            while ((line = input.readLine()) != null) {
                response += line;
            }
            return response;
        }
        finally {
            connection.disconnect();
        }
    }

    // http get
    public String get(URL url) throws IOException{
        HttpURLConnection connection = (HttpURLConnection)url.openConnection();
        return processResponse(connection);
    }

    // http post
    public String post(URL url, String contentType, String content) throws IOException{
        HttpURLConnection connection = (HttpURLConnection)url.openConnection();
        connection.setDoOutput(true);
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", contentType);

        OutputStream os = connection.getOutputStream();
        os.write(content.getBytes());
        os.flush();

        return processResponse(connection);
    }
}
```

Note that we did not use the Apache HttpClient library.  While it was 
previously included with Android, it has been deprecated in favor of the Java 
standard library.

Next, we'll create another class, *APIWrapper*, that will be responsible for 
sending contact data to the remote server and processing the data returned. The 
file containting *APIWrapper* will also contain some helper classes that we'll 
need for serializing and deserializing the data.

```java
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
            Log.e(LOGTAG, "Unable to get contacts. " + e.getMessage());
        }
        return null;
    }

    public void createContact(Contact contact) {
        try {
            URL url = new URL(baseURL + NEW_CONTACT_URL);
            String jsonData = gson.toJson(contact);
            Log.d("create contact", url + ": " + jsonData);
            mHttpRequest.post(url, "application/json", jsonData);
        }
        catch (IOException e) {
            Log.e(LOGTAG, "Unable to create contact. " + e.getMessage());
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
```

We'll create one more class in the root package that will handle the logic 
of synchronizing the database with the remote server.  This new class will 
extend the *AsyncTask* class - this will allow the code to execute in a 
background thread that won't interfere with the main thread while it executes. 
*AsyncTask* is a generic class that supports three generic type parameters used 
to specify the type of parameters supplied when the task is executed, the type 
of data published during execution to indicate progress, and the type of the 
result; we won't use any of those.  


```java
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
```

Here, we first gain access to shared preferences using the *PreferenceManager*. 
Once we have the shared preferences, we retrieve the remote server URL, 
username, and password.  Using this data, we create an an instance of 
*APIWrapper* and use it to get a list of contacts from the remote server. 
Similarly, we get a list of local contacts from *AddressBook*.  For each list 
of contacts, we collect the UUIDs.  

Next, we iterate through the list of local contacts and check to see if the 
local contact's UUID is in the list of server contact UUIDs; if not, we add the 
contact to the server.  In the same way, we then iterate through the remote 
contacts and compare their UUIDs with the local UUIDs, updating the 
*AddressBook* as needed.  

For simplicity, this code only syncs new contacts.  If we were to support 
updating contacts, we could compare names, email addresses, address, and 
favorite status to determine if the contacts differed.  The problem with this 
approach, however, is that we would not know what the updated contact is - the 
local contact or the remote contact.  One way to handle this is to track a 
"last updated" date and time; we could then compare the datetime between the 
remote and local contact to determine which is newer and which needs to be 
updated.

The final step is to add code to execute synchronization.  In the 
*AddressBookFragment*, we'll add this code to the *onCreateView()* and 
*onStop()* methods - this will ensure that the data is synchronized when 
the app starts and when the user navigates away from the app. 

```java
public class AddressBookFragment extends Fragment {
    ...

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ...
        new SyncData(getContext()).execute();
        updateUI();
        ...
    }

    @Override
    public void onStop() {
        super.onStop();
        new SyncData(getContext()).execute();
    }

    ...
}
```

We could also optionally add a menu item to allow the user to manually start 
the sync process.
