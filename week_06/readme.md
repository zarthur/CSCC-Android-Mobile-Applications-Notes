# Week 6 - Layouts and Widgets

## Corresponding Text
*Android Programming*, pp. 149-166

## Adding Widgets and Updating Layouts
Let's start by adding some additional functionality to our app.  Eventually, 
we'll be able to view a list of contacts and it might be helpful to select 
some contacts as "favorites" and view only favorite contacts.  To enable the 
user to designate a contact as a favorite, we'll add a widget to the contact 
fragment layout, update the *Contact* class, and wire the new widget to 
make changes to the corresponding *Contact* instance when the widget's state 
changes.  

To begin, let's add a string resource to the `string.xml` resource file:

```xml
<string name="favorite">Favorite</string>
```

Next, let's add a *CheckBox* widget to the contact fragment layout.  We can 
add it after the email *EditText* in the existing *LinearLayout* by dragging 
and dropping the widget onto the preview of the layout and settting the 
following properties:

| Property | Value            |
|----------|------------------|
| ID       | contact_favorite |
| text     | @string/favorite |

While we're modifying the layout, set the following properties on the other 
widgets:

| Widget           | Property  | Value            |
|------------------|-----------|------------------|
| Name *EditText*  | inputType | textPersonName   |
| Email *EditText* | inputType | textEmailAddress |

Choosing the appropriate input type will affect which keyboard is displayed 
on screen, suggestions provided, and how input will be displayed (masked when 
passwords are entered, for example).

We'll examine the layout further later but for now, it should resemble this:

![layout](images/initial-layout.png)

The XML for the layout should be similar to the following

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

    <CheckBox
        android:text="@string/favorite"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:id="@+id/contact_favorite"/>
</LinearLayout>
```

To support designating a contact as a favorite, we'll have to update the 
*Contact* class to track this information.  We can do this by adding a 
private boolean field and the corresponding getter and setter.

```java
public class Contact {

    ...

    private boolean mIsFavorite;

    ...

    public boolean isFavorite() {
        return mIsFavorite;
    }

    public void setFavorite(boolean favorite) {
        mIsFavorite = favorite;
    }

}
```

In the code above, the ellipses, `...`, represent additional code left out 
for brevity.

Now we can add code that will toggle a contact's state of being a favorite 
based on the state of the widget we added earlier.  In the *ContactFragment* 
class, we can add a field to represent the favorite *CheckBox* and set an 
*OnCheckedChangeListener* to update the the contact's favorite status.

```java
public class ContactFragment extends Fragment {

    ...

    private CheckBox mFavoriteCheckBox;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_contact, container, false);

        ...

        mFavoriteCheckBox = (CheckBox)v.findViewById(R.id.contact_favorite);
        mFavoriteCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mContact.setFavorite(isChecked);
            }
        });

        return v;
    }
}
```

## Layout Attributes
Now that we've added a widget and code to do something when the widget is used, 
let's return to our layout `fragment_contact.xml`.  For each widget defined 
in the layout, there are some attributes worth noting beyond what we've made 
use of so far.

A *style* is a resource that contains attributes defining the appearance and 
behavior of a widget.  Styles, like layouts, are specified using XML.  For 
example, the following defines a style named **BigBoldStyle**. 

```xml
    <style name="BigBoldStyle">
        <item name="android:textSize">20sp</item>
        <item name="android:textStyle">bold</item>
    </style>
```

We can define styles in `res/values/styles.xml`.  Be careful when specifying 
attribute names - they are case sensitive.

We can use the style by setting a widget's property or by specifying it in 
the resource XML like this:

```xml
 <EditText style="@style/BigBoldStyle"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:id="@+id/contact_name"
        android:hint="@string/name_hint"
        android:padding="20dp"
        android:inputType="textPersonName"/>
```

Compare the text in the *EditText* widgets before and after changing the style.

![layout](images/style.png)

A **theme** is a collection of styles.  Like a style, a theme can be specified 
in the `styles.xml` file.