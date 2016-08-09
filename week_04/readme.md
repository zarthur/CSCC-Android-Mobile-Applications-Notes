# Week 4 - Fragments

## Corresponding Text
*Android Programming*, pp. 121-147


## UI Flexibility
We're going to start working on a new application, one that will allow us to
store contact information.  To begin, our app will consist of two parts: a
list of all contacts and details of individual contacts.  We could create two
activities, one for each of the parts.  When that app starts, we'd see a list
of contacts and tapping one of the contacts would start the details activity.
We can switch back to the list using the back button.  

While this seems reasonable for a phone, it doesn't seem like the best way
of presenting contact information on a tablet.  Given the larger screen of a
tablet compared to a phone, displaying the list and details simultaneously
would provide the user with a better experience.  When viewed on a phone, we
might want the user to be able to swipe left and right when viewing details
to move from one contact to another.

Unfortunately, this sort of interface flexibility is not possible with
activities.  

## Fragments
We can create the type of interface described above using fragments.  A
**fragment** represents a behavior or part of an activity.  A fragment can be
be combined with other fragments and added to an activity and can be used by
different activities.  While fragments are useful for creating flexible
interfaces, their views cannot be displayed on screen without an activity to
host the fragments. Like activities, fragments are part of the controller layer
in an application.

Before we create fragments, let's create a new application.  In Android Studio,
select **File -> New... -> New Project**.  Enter a name and domain and choose a
project location.  On the next screen, choose "Phone and Tablet" and select
an API level, in class we'll use API 19.  Next, choose "Empty Activity".
Finally, name the activity something like "ContactActivity".

Fragments were introduced as part of API level 11 when Android tablets were 
first introduced.  At the time many developers were supporting API level 8 
and newer.  Rather than configure their projects to support API level 11 at a 
minimum, developers were able to take advantage of fragments using Android's 
support library.  The support library offers backward-compatible versions of 
new features - allowing developers to cater to a majority of devices while 
being able to utilize new features.

We'll make use of two classes from the support library: *Fragment* and 
*FragmentActivity*; in addition to using fragments, we'll need to make use 
of activities that are designed to interact with fragments.  In order to use 
the support library and these classes, we'll have to add the support library 
to the list of libraries our project depends upon.  While we could modify 
the `build.gradle` file to add the necessary information, Android Studio 
provides an easier way of adding dependencies.  Choose **File -> Project 
Structure** from the menu bar, select **app**, and click the **Dependencies** 
tab.  Here's we can see all the libraries our application depends on.  We can 
add a dependency by clicking the **+** button and selecting **Library 
Dependency**.  Begin typing `support` and choose the `support-v4` library. 
Click **OK** to close the open dialog boxes.  Gradle should automatically sync
to include our change.

The next step to using fragments is to modify our activity so that it extends
the *FragmentActivity* class rather than *AppCompatActivity*.  The code in 
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

Next, we'll create the model layer for our application.  

### Fragment Lifecycle

### Hosting a Fragment

### Creating a Fragment

### The FragmentManager

## Application Architecture with Fragments
