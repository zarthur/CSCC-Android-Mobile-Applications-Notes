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