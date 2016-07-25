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

### Fragment Lifecycle

### Hosting a Fragment

### Creating a Fragment

### The FragmentManager

## Application Architecture with Fragments
