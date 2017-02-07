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
assign a new instance of the *Contact* class to a private *mCrime* field. Just 
like with an activity, we can pass saved instance data, in the form of a 
*Bundle* to the fragment when it's created. 
 
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

If we were to compare the code in *ContactFragment.onCreate()* to code in 
previous activities, we might notice that we haven't done anything with the 
view yet.  For fragments, we rely on the *onCreateView()* method to inflate the 
view.  Specifically, *ContactFragment.onCreateView()* will be defined as 
follows:

```java
@Override
public View onCreateView(LayoutInflater inflater, ViewGroup container, 
                          Bundle savedInstanceState) {
    View v = inflater.inflate(R.layout.fragment_contact, container, false);
    return v;
}
```

Similar to the parameters in the *onCreate()* method, the *LayoutInflater* is 
responsible for creating the view from a specified XML file, 
the *ViewGroup* specifies where the inflated view will be contained, 
and the *Bundle* contains any state information that can be used to recreate 
the view.  The arguments passed to the *LayoutInflater.inflate()* method 
specify the layout resource id, the *ViewGroup*, and a *boolean* that 
determines whether or not to add the view to the parent; we'll specify `false` 
because we'll add the view in the activity's code.  

In *onCreateView()*, we can also add listeners to our *EditText* widgets. 
We'll update the *Contact* instance, `mCrime`, when the text in the the 
widgets change.  To do this, we'll add a listener that implements the 
*TextWatcher* interface. The interface specifies three methods, but we're only 
interested in *onTextChanged()*, we won't define any behavior for the other 
methods.  

When we were working with activities, we could find view elements using the 
*Activity.findViewById()* method; this method was simply a convenience method 
for the activity's *View.findViewById()* method; fragments do not have this 
convenience method, so we have to have an instance of *View*.  Notice 
we do have an instance in the *onCreateView()* method.  The following code 
will update the name associated with `mCrime` by first finding the name 
*EditText* and assigning it to a field on on *ContactFragment* then getting 
the text as it's changed and calling *mCrime.setName()*.

```java
@Override
public View onCreateView(LayoutInflater inflater, ViewGroup container,
                          Bundle savedInstanceState) {
    View v = inflater.inflate(R.layout.fragment_contact, container, false);
    
    mNameField = (EditText)v.findViewById(R.id.contact_name);
    mNameField.addTextChangedListener(new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, 
                                      int after) {
            // No new behavior 
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, 
                                  int count) {
            mContact.setName(s.toString());
        }

        @Override
        public void afterTextChanged(Editable s) {
            // No new behavior
        }
    });
    
    return v;
}
```

We can do something similar with the email *EditText* and *mCrime.setEmail()* 
method.  

After we've added all the code to create the fragment, inflate its view, and 
add logic to the various widgets, `ContactFragment.java` should contain code 
similar to the following:

```java
package com.arthurneuman.mycontacts;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

public class ContactFragment extends Fragment {
    private Contact mContact;
    private EditText mNameField;
    private EditText mEmailField;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContact = new Contact();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_contact, container, false);

        mNameField = (EditText)v.findViewById(R.id.contact_name);
        mNameField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
                // No new behavior
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
                mContact.setName(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
                // No new behavior
            }
        });

        mEmailField = (EditText)v.findViewById(R.id.contact_email);
        mEmailField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
                // No new behavior
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
                mContact.setEmail(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
                // No new behavior
            }
        });

        return v;
    }
}

```

Though *ContactFragment* is not complete, if we start our app, we won't see 
its view; a fragment can't put its view on the screen on its own.  In order 
to see the fragment, we'll have to rely on an activity.

### The FragmentManager and the Fragment Lifecycle
Much like an activity, a fragment transitions between states such as running,
paused, and stopped and has corresponding methods for each state transition.

For a given activity, the *FragmentManager* is responsible for managing 
fragments and adding their views to the view hierarchy.  The FragmentManager 
handles a list of fragments and and a back stack of fragment transactions, 
providing functionality when a user presses the back button. The 
FragmentManager is also responsible for calling the lifecycle methods of a 
fragment.  

To use the Fragment manger, we can add the following to *ContactActivity*'s 
*onCreate()* method:

```java
FragmentManager fm = getSupportFragmentManager();
```  

With the FragmentManager, we can now create a fragment transaction.  Fragment 
transactions are used to add, remove, or replace fragments in the fragment 
list; we have to use a fragment transaction to add our fragment.  To create 
a new transaction we'll use *FragmentManager*'s *beginTransaction()*, *add()*, 
and *commit()* methods.  

```java
Fragment fragment = fm.findFragmentById(R.id.fragment_container);
        
if (fragment==null) {
    fragment = new ContactFragment();
    fm.beginTransaction()
            .add(R.id.fragment_container, fragment)
            .commit();
}
```

Here we first check to see if the fragment was previously added by asking 
the FragmentManager for the fragment identified by the *fragment_container*. 
If there is no existing fragment, we create a new fragment transaction and 
add a new *ContactFragment* with *fragment_container*.  It's important to check 
if the fragment is already part of the FragmentManager's fragment list. If 
*ContactActivity* is destroyed and reloaded due to rotation or Android's 
attempt to conserve memory, the Fragmentmanager will save it's list of 
fragments and this can be reloaded.

`ContactActivity.java` should now look similar to this:

```java
package com.arthurneuman.mycontacts;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;

public class ContactActivity extends FragmentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact);

        FragmentManager fm = getSupportFragmentManager();
        Fragment fragment = fm.findFragmentById(R.id.fragment_container);

        if (fragment==null) {
            fragment = new ContactFragment();
            fm.beginTransaction()
                    .add(R.id.fragment_container, fragment)
                    .commit();
        }

    }
}
```

If we run the app, we should now see *ContactFragment*.
