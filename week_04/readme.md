# Week 4 - Fragments

## Corresponding Text
*Android Programming*, pp. 121-147


## UI Flexibility
We're going to start working on a new application, one that will allow us to
store contact information. To begin, our app will consist of two parts: a
list of all contacts and details of individual contacts. We could create two
activities, one for each of the parts. When that app starts, we'd see a list
of contacts and tapping one of the contacts would start the details activity.
We can switch back to the list using the back button.

While this seems reasonable for a phone, it doesn't seem like the best way
of presenting contact information on a tablet. Given the larger screen of a
tablet compared to a phone, displaying the list and details simultaneously
would provide the user with a better experience. When viewed on a phone, we
might want the user to be able to swipe left and right when viewing details
to move from one contact to another.

Unfortunately, this sort of interface flexibility is not possible with
activities.

## Fragments
We can create the type of interface described above using fragments. A
**fragment** represents a behavior or part of an activity. A fragment can be
be combined with other fragments and added to an activity and can be used by
different activities. While fragments are useful for creating flexible
interfaces, their views cannot be displayed on screen without an activity to
host the fragments. Like activities, fragments are part of the controller layer
in an application.

Before we create fragments, let's create a new application. In Android Studio,
select **File -> New... -> New Project**. Enter a name and domain and choose a
project location. On the next screen, choose "Phone and Tablet" and select
an API level, in class we'll use API 19. Next, choose "Empty Activity".
Finally, name the activity something like "ContactActivity".

Fragments were introduced as part of API level 11 when Android tablets were
first introduced. At the time many developers were supporting API level 8
and newer. Rather than configure their projects to support API level 11 at a
minimum, developers were able to take advantage of fragments using Android's
support library. The support library offers backward-compatible versions of
new features - allowing developers to cater to a majority of devices while
being able to utilize new features.

We'll make use of two classes from the support library: *Fragment* and
*FragmentActivity*; in addition to using fragments, we'll need to make use
of activities that are designed to interact with fragments. In order to use
the support library and these classes, we'll have to add the support library
to the list of libraries our project depends upon. While we could modify
the `build.gradle` file to add the necessary information, Android Studio
provides an easier way of adding dependencies. Choose **File -> Project
Structure** from the menu bar, select **app**, and click the **Dependencies**
tab. Here's we can see all the libraries our application depends on. We can
add a dependency by clicking the **+** button and selecting **Library
Dependency**. Begin typing `support` and choose the `support-v4` library.
Click **OK** to close the open dialog boxes. Gradle should automatically sync
to include our change.

The next step to using fragments is to modify our activity so that it extends
the *FragmentActivity* class rather than *AppCompatActivity*. The code in
the activity Java file should look similar to the following:

```Java
package com.arthurneuman.mycontacts;

import android.support.v4.app.FragmentActivity;
import android.os.Bundle;

public class ContactActivity extends FragmentActivity {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_contact);
  }
}

```

Next, we'll create the model layer for our application consisting of one class
representing contact information. This class will have three fields: one for
the contact's name, one for an email address, and one that represents a
unique ID for the contact. We'll also create the appropriate getters and
setters. We won't have a setter for the ID but will assign a value using the
class's constructor instead. To create the file, right-click on your package
in the *app/java* directory in the project view and select
*New->Java class...*. Choose a name like `Contact`.

```Java
package com.arthurneuman.mycontacts;

import java.util.UUID;

public class Contact {
  private UUID mID;
  private String mName;
  private String mEmail;

  public Contact() {
    mID = UUID.randomUUID();
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
}
```
Our next step will be to begin working with fragments. Recall, that our
application has to have an activity and, in order to make use of fragments, the
activity has to host the fragment. In addition to hosting fragments, an
activity must also manage the lifecycle of fragment instances.

### Hosting a Fragment
There are two approaches to hosting a fragment in an activity: by adding the
fragment to the activity's layout or by adding the fragment in the activity's
code. A fragment added to an activity's layout is a *layout fragment* and
while simple to add, it does not aid in flexibility. Once added to an
activity's view, a layout fragment cannot be swapped with another fragment
during the activity's lifetime.

Adding the fragment in the activity's code provides us with more flexibility,
allow us to replace it during runtime. This is the approach we'll use.
Eventually, we'll create a fragment named *ContactFragment* but first we'll
need to create a spot for its view in *ContactActivity*'s view hierarchy.
Creating a place for the fragment's view is not the same as adding the fragment
to the activity's layout.

To add a place in the activity's view, we'll replace the default
**RelativeLayout** with a **FrameLayout**. Because this is the top-level
layout, we can't use the design view to make the change; we'll have to use the
text view. Replace the content of `activity_contact.xml` with the following:

```xml
<?xml version="1.0" encoding="utf-8"?>
<FrameLayout xmlns:android="http://schemas.android.com/apk/res/android"
             android:id="@+id/fragment_container"
             android:layout_width="match_parent"
             android:layout_height="match_parent">
</FrameLayout>
```  

### Creating a Fragment
Before we begin working with a new fragment, let's add some strings to our
string resource file, `app/res/values/strings.xml` for text hints:

```xml
<string name="name_hint">Name</string>
<string name="email_hint">Email</string>
```

Creating a new UI fragment is similar to creating a new activity.  To begin,
we'll work on it's view but adding a new layout file for it.  To create the new
layout file, right-click on the `res/layout` folder (the same folder that
contains `activity_contact.xml`) and select **New -> Layout resource file**.
Use the following to create the new fragment.

| Field        | Value                |
|:-------------|:---------------------|
| File name    | fragment_contact.xml |
| Root element | LinearLayout         |


To the *LinearLayout*, we'll add two *EditText* widgets by dragging and
dropping "Plain Text" objects onto the layout.  Set the following properties.

| View Object       | Property     | Value              |
|:------------------|:-------------|:-------------------|
| First *EditText*  | layout_width | match_parent       |
| First *EditText*  | id           | contact_name       |
| First *EditText*  | hint         | @string/name_hint  |
| First *EditText*  | padding, all | 20 dp              |
| Second *EditText* | layout_width | match_parent       |
| Second *EditText* | id           | contact_email      |
| Second *EditText* | hint         | @string/email_hint |
| Second *EditText* | padding, all | 20 dp              |

The fragment's layout XML should look similar to the following:

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
        android:padding="20dp"/>

    <EditText
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:id="@+id/contact_email"
        android:hint="@string/email_hint"
        android:padding="20dp"/>
</LinearLayout>
```

Now that we have a view element for our new fragment, we can begin working
on a the controller.  We can create a new Java class file,
`ContactFragment.java` in the same way we created `Contact.java`.  

The first thing to do is to extend the *Fragment* class from the
*android.support.v4.app* package. Just like activities, fragments have an
*OnCreate()* method that is called when the fragment is created.  When our
*ContactFragment* is created, we'll call the parent's *onCreate()* method and
assign a new instance of the *Contact* class to a private *mCrime* field.

```java
package com.arthurneuman.mycontacts;


import android.os.Bundle;
import android.support.v4.app.Fragment;

public class ContactFragment extends Fragment {
    private Contact mContact;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContact = new Contact();
    }
}
```



### The FragmentManager and the Fragment Lifecycle
Much like an activity, a fragment transitions between states such as running,
paused, and stopped and has corresponding methods for each state transition.


## Application Architecture with Fragments
