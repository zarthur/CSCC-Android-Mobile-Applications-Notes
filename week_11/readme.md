# Week 11 - Intents

## Corresponding Text
*Android Programming*, pp. 273-289

With Android, we can start an activity in another application using implicit 
intents.  We've used intents before to start another activity within 
the same application; when we did this we had to specify the class of the 
activity to start - these were explicit intents.  With implicit intents, we 
simply describe what needs to be done and Android will start an activity in 
the appropriate application.  

While we could add the functionality to our apps, it's easier and often more 
user friendly to rely on an another app the user is used to using.  For 
example, rather than add the necessary functionality to compose and send email, 
we could instead rely on another email app - it's unlikely that the user does 
not have an email app installed.

In our contacts app, we'll use implicit intents to allow us to send an email 
a contact.  Before we add the intent-specific code, let's update the view.

To start, let's add a string resource for a "Send Email" button to the 
`strings.xml` resource file: 

```xml
    <string name="send_email">Send Email</string>
```

Next, we'll add a *Button* widget to the `fragment_contact.xml` layout above 
the *MapView* widget:

```xml
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:orientation="vertical"
    android:layout_width="match_parent"
    android:layout_height="match_parent">
    <EditText
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:id="@+id/contact_name"
        android:hint="@string/name_hint"
        android:padding="20dp"
        android:inputType="textPersonName"/>

    <EditText
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:id="@+id/contact_email"
        android:hint="@string/email_hint"
        android:padding="20dp"
        android:inputType="textEmailAddress"/>

    <EditText
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:inputType="textPostalAddress"
        android:id="@+id/contact_address"
        android:padding="20dp"
        android:hint="@string/address_hint"/>

    <CheckBox
        android:text="@string/favorite"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:id="@+id/contact_favorite"/>

    <Button
        android:text="@string/send_email"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:padding="20dp"
        android:id="@+id/send_email" />

    <com.google.android.gms.maps.MapView
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:id="@+id/contact_map" />
    
</LinearLayout>  
```

Note that we've set the id of the new *Button* to `send_email`.

Next, we can add code to provide functionality to the new button.  To do this,
we'll need to create an implicit intent.  Like explict intents we created 
previously, we'll use the *Intent* class to do this.  For each intent, the 
primary information we have to provide are the intent's action and the data 
to operate on.  When we used an explicit intent previously, we added code 
similar to the following: 

```java
    Intent intent = new Intent(getActivity(), ContactActivity.class);
    startActivity(intent);
```

Here, the action is `getActivity()` and the data to be operated on is the 
*ContactActivity* class; this explicitly starts an activity.  With implicit 
intents, we'll desribe the action we want to perform, typically with constants 
from the *Intent* class.  For example, we can use *Intent.ACTION_IMAGE_CAPTURE* 
if we wanted to use the camera or *Intent.ACTION_SEND* if we wanted to send 
the specified data.  To send an email to a specific address without any 
attachments, we'll use the *Intent.ACTION_SENDTO* action.  

If we were writing an email app, we would want our app to indicate that it's 
able to handle sending emails.  To do this, we would add the following to 
app's manifest:

```xml
<intent-filter>
        <action android:name="android.intent.action.SENDTO" />
        <data android:scheme="mailto" />
        <category android:name="android.intent.category.DEFAULT" />
</intent-filter>
```

By specifying the action, the OS knows the app is capable of handling the 
action.  To respond to implict intents, we would have to specify the default 
category.  When looking for volunteer applications to perform some action, 
Android uses the default category.  We can also specify the scheme that can 
be compared with the intent data to ensure our app is cable of handling the 
intent.  Here, the scheme specifies "mailto" as the URI for email addresses is 
of the form "mailto:address@domain.com".  


