# Week 11 - Intents

## Corresponding Text
*Android Programming*, pp. 273-289

## Implicit Intents
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

To start, let's add a string resource for a "Send Email" menu item and for a 
message we'll display if there is a problem starting an email app to the 
`strings.xml` resource file: 

```xml
    <string name="send_email">Send Email</string>
    <string name="email_app_error">No Email App Configured</string>
```

Next, we'll create a menu for the contact fragment similar to the menu we 
created for the address book fragment.  In the menu resource folder, create a 
new menu resource file named `fragment_contact.xml` and add a menu item using 
the `send_email` string resource as the text and `menu_item_send_email` as the 
id.  The file should contain the following:

```xml
<?xml version="1.0" encoding="utf-8"?>
<menu xmlns:android="http://schemas.android.com/apk/res/android">
    <item android:title="@string/send_email"
        android:id="@+id/menu_item_send_email"
        />
</menu>
```

Next, we can add code to provide functionality to the new menu item.  To do 
this, we'll need to create an implicit intent.  Like explicit intents we created 
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
intents, we'll describe the action we want to perform, typically with constants 
from the *Intent* class.  For example, we can use *Intent.ACTION_IMAGE_CAPTURE* 
if we wanted to use the camera or *Intent.ACTION_SEND* if we wanted to send 
the specified data.  To send an email to a specific address without any 
attachments, we'll use the *Intent.ACTION_SENDTO* action.  

There are additional pieces of information that we can provide with an intent. 
One of these is bundle extras that can be used provide additional information 
to the target activity.  One example of using extras is to specify the 
destination email address when sending email.

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
action.  To respond to implicit intents, we would have to specify the default 
category.  When looking for volunteer applications to perform some action, 
Android uses the default category.  We can also specify the scheme that can 
be compared with the intent data to ensure our app is cable of handling the 
intent.  Here, the scheme specifies "mailto" as the URI for email addresses is 
of the form "mailto:address@domain.com".  

Now, let's add code to create the menu and provide functionailty to the send 
email menu item by creating a new intent and calling *startActivity()*:

```java
public class ContactFragment extends Fragment {
    ...
    @Override
    public void onCreate(Bundle savedInstanceState) {
        ...
        setHasOptionsMenu(true);
        ...
    }

    ...

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_contact, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item_send_email:
                if (mContact.getEmail() == null) {
                    return true;
                }
                // addresses are specified using a String array
                String[] addresses = {mContact.getEmail()};
                Intent intent = new Intent(Intent.ACTION_SENDTO);

                // specify the mailto URI as data to indicate email
                intent.setData(Uri.parse("mailto:"));
                intent.putExtra(Intent.EXTRA_EMAIL, addresses);

                // check that an application is capable of handling email
                ComponentName emailApp = intent.resolveActivity(getContext().getPackageManager());
                ComponentName unsupportedAction =
                        ComponentName.unflattenFromString("com.android.fallback/.Fallback");
                if (emailApp != null && !emailApp.equals(unsupportedAction)) {
                    startActivity(intent);
                }
                else {
                    Toast.makeText(getContext(), R.string.email_app_error,
                            Toast.LENGTH_SHORT).show();
                }
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    ...
    
}
```

The code we've added to the *onCreate()* and *onCreateOptionsMenu()* methods 
are similar to the code we added to *AddressBookFragement* when we added a menu 
previously.  Similarly, we have to add code to the *onOptionsItemSelected()* 
method to support selection of menu items.

First, we check if the contact has an email address; if 
not, the method returns true and does nothing.  Next we create an intent with 
the action *Intent.ACTION_SENDTO*, indicating that we'd like to send an email 
without any attachments.  The intent data is then set to the "mailto:" URI, 
indicating that we'd like to send an email.  To specify the address, we use an 
intent extra. Note that using the intent extra with the *Intent.EXTRA_EMAIL* 
key, the value is expected to be a string array, allowing us to specify 
multiple addresses. An alternative to this would be to combine "mailto:" and 
the email address when setting the intent data:

```java
intent.setData(Uri.parse("mailto:" + mContact.getEmail()));
```

Next, we use the package manager to check if an app capable of handling the 
intent is installed using *Intent.resolveActivity()*.  When using the emulator, 
it's not enough to have an email app installed - the app must be configured 
with an email account.  If this is not the case, and we try to start the email 
activity, Android will return a message indicating that the action is 
unsupported.  The message is displayed using the 
*com.android.fallback.Fallback* activity.  So, we check that both an email app 
is installed and that it is properly configured.  If not, we'll display a 
toast indicating that there is no properly configured email app.  In order 
to demonstrate the functionality of the send email menu item when using the 
emulator, configure the email app before selecting the send email menu item.

## Getting Data Back
What if we wanted to add a picture for each contact?  We can use an implicit 
intent to start the camera app to take a picture but how do we get the result 
into our app? When the activity that we call is done executing and returns 
data, Android will call the *onActivityResult()* method of the original 
activity with an intent containing the result.  

If our app requires a camera, we should specify that requirement in the app's 
manifest:

```xml
<manifest ... >
    <uses-feature android:name="android.hardware.camera"
                  android:required="true" />
    ...
</manifest>
```

Now, when we add our app to Google Play, it will only be available for devices 
with cameras.

To accommodate an image, we'll have to modify our model and view.  First,
let's add a private field to the *Contact* class:

```java
public class Contact {
    
    ...
    
    private Bitmap mImage;

    ...

    public Bitmap getImage() {
        return mImage;
    }

    public void setImage(Bitmap mImage) {
        this.mImage = mImage;
    }
}
```

Next, add an *ImageView* widget to the `fragment_contact.xml` layout resource. 
When you add it, you'll be prompted to choose an image - pick any image to use 
as the default image. Set the id to `contact_image`, layout width and 
height to wrap content, padding to 20dp. The layout file should now include 
something like the following:

```xml
    <ImageView
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:padding="20dp"
        app:srcCompat="@mipmap/ic_launcher"
        android:id="@+id/contact_image" />
```

Now, we can add code to the *ContactFragment* class that will allow us to set 
an image for a contact.

```java
public class ContactFragment extends Fragment {
    ...
    private ImageView mImageView;

    ...

    private static final int REQUEST_IMAGE_CAPTURE = 1;

    ...

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        
        ...

        mImageView = (ImageView)v.findViewById(R.id.contact_image);
        if (mContact.getImage() != null) {
            mImageView.setImageBitmap(mContact.getImage());
        }
        mImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (intent.resolveActivity(getContext().getPackageManager()) != null) {
                    startActivityForResult(intent, REQUEST_IMAGE_CAPTURE);
                }
            }
        });

        ...

    }

    ...

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            mContact.setImage(imageBitmap);
            mImageView.setImageBitmap(imageBitmap);
        }
    }
}
```

We start by adding a field for the *ImageView*.  In the fragment's 
*onCreateView()* method, we assign the *ImageView* to the field and then set 
its image if the associated contact has an image. We will require a user to tap 
on the *ImageView* widget to update the contact's image, so we assign an 
*OnClickListener*.  In *onClick()* method the new listener, 
we create a new intent using the *MediaStore.ACTION_IMAGE_CAPTURE* action.  
When we start the activity, using the *startActivityForResult()* method, we 
must specify both the intent and the request code; the request code will be 
later to process the result returned by the new activity.  Since the OS will 
rely on the *onActivityResult()* method when the new activity is complete 
(regardless of the activity), using a request code allows us process results 
returned by different activities in different ways.

We must specify what is to be done after the user captures an image with the 
camera.  In the *onActivityResult()* method, we first check if the request code 
corresponds to the image capture activity and that the result code indicates 
success.  If these conditions are met, we extract the thumbnail of the image 
from the intent extra with key "data" and use the object to update 
contact and the *ImageView* widget.

Note that we have not added any code to persist our data - any data we add, 
including contact pictures, will be lost if our app is closed.
                