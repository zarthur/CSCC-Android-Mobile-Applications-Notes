# Week 10 - Maps 

## Corresponding Text
*Android Programming*, pp. 571-584

## The Maps API
Because of Android's dependence on Google's mapping services, the Android maps 
functionality is not a a stand-alone component of Android.  This requires us to 
do several things before we can use maps in our app.  First, we need to add a 
dependency to our app.  Just as we added the support and recyclerview libraries 
to our app's dependencies, we'll have to add the Google Play Services.  When 
adding the library dependency, search for `play-services` and select the 
`com.google.android.gms:play-services` library.  If, after adding the 
dependency, Android Studio is unable to display layouts in the design view, try 
rebuilding the project.

The second thing we need to do is to grant our app permission to do certain 
things: accessing the Internet to download map data, get the device's network 
state, and to store temporary map data locally.  To give add these permissions, 
modify the app's manifest in `app/Manifests/AndroidManifest.xml` so it appears 
similar to the following:

```xml
<?xml version="1.0" encoding="utf-8"?>
<manifest xmlns:android="http://schemas.android.com/apk/res/android"
          package="com.arthurneuman.mycontacts">

    <uses-permission android:name="android.permission.INTERNET" />
    <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE"/> 
    <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE"/>    
    
    <application
        android:allowBackup="true"
        android:icon="@mipmap/ic_launcher"
        android:label="@string/app_name"
        android:supportsRtl="true"
        android:theme="@style/AppTheme">
        <activity android:name=".ContactPagerActivity"
            android:parentActivityName=".AddressBookActivity">
        </activity>
        <activity android:name=".AddressBookActivity">
            <intent-filter>
                <action android:name="android.intent.action.MAIN"/>
                <category android:name="android.intent.category.LAUNCHER"/>
            </intent-filter>
        </activity>
    </application>

</manifest>
```    

The last thing we'll need to use maps in our app is a Maps API key - a unique 
identifier authorizing the app to use Google's mapping services.  First, we'll 
need our app's signing key.  The easiest way to find the app's signing key 
is using the command prompt or console.  In Windows, start the command prompt, 
navigate to the directory of the project and run `gradlew.bat signingReport`. 
In macOS or Linux, navigate to the directory of the project and run 
`./gradlew signingReport`.  The output should include something similar to the 
following:

```
Variant: debug
Config: debug
Store: /Users/arthur/.android/debug.keystore
Alias: AndroidDebugKey
MD5: XX:XX:XX:XX:XX:XX:XX:XX:XX:XX:XX:XX:XX:XX:XX:XX
SHA1: XX:XX:XX:XX:XX:XX:XX:XX:XX:XX:XX:XX:XX:XX:XX:XX:XX:XX:XX:XX
Valid until: Thursday, April 26, 2046
```

We'll need the `SHA1` value.  Next, open a browser and load 
`https://developers.google.com/maps/documentation/android-api/signup`.  On the 
page, click **GET A KEY**, **Select or create a project**, and 
**Create a new project**.  

![new-api-key](images/new-api.png)

Enter the name of the project and click 
**Create and enable API**.  You should be presented with a new API key; copy 
it. Finally, we can add the new key to the `AndroidManifest.xml` file; the file 
should look similar to the following (but with your key):

```xml
<?xml version="1.0" encoding="utf-8"?>
<manifest xmlns:android="http://schemas.android.com/apk/res/android"
          package="com.arthurneuman.mycontacts">

    <uses-permission android:name="android.permission.INTERNET" />
    <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE"/>
    <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE"/>

    <application
        android:allowBackup="true"
        android:icon="@mipmap/ic_launcher"
        android:label="@string/app_name"
        android:supportsRtl="true"
        android:theme="@style/AppTheme">
        <activity android:name=".ContactPagerActivity"
            android:parentActivityName=".AddressBookActivity">
        </activity>
        <activity android:name=".AddressBookActivity">
            <intent-filter>
                <action android:name="android.intent.action.MAIN"/>
                <category android:name="android.intent.category.LAUNCHER"/>
            </intent-filter>
        </activity>
        <meta-data
            android:name="com.google.android.geo.API_KEY"
            android:value="XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX"/>
    </application>

</manifest>
```

Now, we can add mapping functionality to our app.

## Additional Contact Information
Our goal will be to display a map indicating the location of an address we 
assign to each of our contacts.  In order to assign an address, we'll need 
to update the *Contact* class, *ContactFragment*, and the contact fragment 
layout.  To keep the example simple, our app will store a free-form address 
like `550 E. Spring St, Columbus, OH 43215`.  

In the *Contact* class, we can add a new field and create the corresponding 
getter and setter:

```java
public class Contact {
    ...
    private String mAddress;
    ...
    public String getAddress() {
        return mAddress;
    }

    public void setAddress(String address) {
        mAddress = address;
    }
}
```

Before we modify the layout, let's add a string resource to the `strings.xml` 
resource file:

```xml
<string name="address_hint">Address</string>
```

To the layout, we can add another *EditText* widget with the following 
properties:hint we specified, 

| Property      | Value                |
|---------------|----------------------|
| ID            | contact_address      |
| layout_width  | match_parent         |
| layout_height | wrap_content         |
| InputType     | textPostalAddress    |
| hint          | @string/address_hint |
| padding       | 20dp                 |

The corresponding XML should look like this:

```xml
<EditText
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:inputType="textPostalAddress"
    android:id="@+id/contact_address"
    android:padding="20dp"
    android:hint="@string/address_hint"/>
```

Recall that we used *TextWatcher* to update instances of *Contact* when users 
typed values into the name and email fields.  We can do that same thing for the 
address field.  

```java
public class ContactFragment extends Fragment {
    ...
    private EditText mAddressField;
    ...
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ...
        mAddressField = (EditText)v.findViewById(R.id.contact_address);
        mAddressField.setText(mContact.getAddress());
        mAddressField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mContact.setAddress(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) { }
        });
        ...
    }
}
``` 
## Using Maps 
