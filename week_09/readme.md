# Week 9 - The Toolbar, Menus, and Navigation

## Corresponding Text
*Android Programming*, pp. 235-254

## The AppCompat Library
We're going to be working with the toolbar.  The toolbar provides a means of 
providing the user with actions, an additional way of navigating through the 
app, and a way of consistently branding the app.  The toolbar was added to 
Android in version 5.0; prior to this, app made us of an action bar.  The 
toolbar builds on the action bar but is more flexible.  Android version 5.0 
corresponds to SDK version 21; since our app support SDK version 19, we won't 
be able to use the native tool bar from the Android library.  Instead, we can 
use a back-ported version from the AppCompat library.  We'll have to make 
a few changes to our app to make use of the AppCompat library and the toolbar 
provided by it.

First, let's add the AppCompat library as a dependency of our app.  In Android 
Studio, select **File -> Project Structure...** from the menus.  With **app**
selected, click the **Dependencies** tab.  Click the **+** button and 
**Add Library dependency**.  Type `com.android.support:appcompat-v7:23.4.0` and 
click **OK**.  Note that the version of the library might differ depending 
on the version of the SDK you are compiling against.  Click **OK** to 
close the **Project Structure** dialog.

Next, we need to make sure the app is using one of the AppCompat theme's.  Open 
`app/manifests/AndroidManifest.xml` and note the value of the *android:theme* 
attribute: `@style/AppTheme`.  The theme is defined in `res/values/styles.xml`. 
The file should contain something like this:

```xml
 <style name="AppTheme" parent="Theme.AppCompat.Light.DarkActionBar">
 ```

 If it doesn't change the parent attribute so it matches the value above.

 Finally, we have to update all our activities to extend *AppCompatActivity*. 
 So far, all our activities have extended *FragmentActivity* or a subclass of 
 *FragmentActivity*.  We won't lose any functionality by extending 
 *AppCompatActivity* instead of *FragmentActivity* because *AppCompatActivity* 
 itself extends *FragmentActivity*.  For our app, we'll need to update 
 *ContactPagerActivity* and *SingleFragmentActivity* to extend 
 *AppCompatActivity*.  We don't need to make any changes to *ContactActivity* 
 or *AddressBookActivity* since they extend *SingleFragmentActivity*. 

 At this point, we can run the app.  The only difference is the new toolbar at 
 the top of our app.      


## Menus

## Hierarchical Navigation