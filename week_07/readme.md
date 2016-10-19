# Week 7 - Views and Adapters

## Corresponding Text
*Android Programming*, pp. 167-191

## Creating a List of Items
Ultimately, we'd like to be able to create contacts, view a list of our 
contacts, and select a contact from the list to view it's details.  Right now, 
we have part of what's needed when viewing an existing contact or creating a 
new one.  As a next step, we'll focus on how we can list a collection of 
contacts.

### Updating the Model
As a first step, we'll need to update our model to include a means of storing 
a collection of *Contact* object, something like an address book.  Because 
we will only every need one instance of this new class, we'll create a 
singleton.  A **singleton** class has at most one instance.  In order to 
create a singleton, we'll create class and make its constructor private; this 
will prevent other objects from creating instances of the class.  So how can 
we create one instance of a class with a private constructor?  Because the 
constructor is private, only methods belonging to the class can use it.  We can 
create a method that checks to see if an instance exists and returns the 
existing instance if it exists or creates it and returns it if the instance 
does not exist.  Let's call the class *AddressBook*.  Here's the necessary code 
to make *AddressBook* a singleton.

```java
public class AddressBook {
    private static AddressBook sAddressBook;

    private AddressBook() {
    }

    public static AddressBook get() {
        if (sAddressBook == null) {
            sAddressBook = new AddressBook();
        }
        return sAddressBook;
    }

}
```

Here, the *AddressBook* class has a static field to store an instance of the 
class and a public, static method named *get()* that will return an instance of 
the class, creating it if necessary.  It's important that the field and method 
be static since we won't have an instance to begin with.

Now that we've made sure we can have at most one *AddressBook*, let's add the 
functionality required to store contacts.  Recall that one of the fields on the 
*Contact* class involved an ID; we'll add functionality to *AddressBook* to 
find a contact using it's ID.

```java
public class AddressBook {
    private static AddressBook sAddressBook;
    private List<Contact> mContacts;

    private AddressBook() {
        mContacts = new ArrayList<>();
    }

    public static AddressBook get() {
        if (sAddressBook == null) {
            sAddressBook = new AddressBook();
        }
        return sAddressBook;
    }

    public List<Contact> getContacts() {
        return mContacts;
    }

    public Contact getContact(UUID id) {
        for (Contact contact: mContacts) {
            if (contact.getID().equals(id)) {
                return contact;
            }
        }
        return null;
    }
}
```

We've added a private field *mContacts* to store a *List* of *Contact* 
instances and assigned a new *ArrayList* to it in the private constructor. 
We've also added two public methods: one to return all the stored contacts and 
one to search for contacts based on a specified ID.

Eventually, we'll add functionality for the user to create new contacts and 
these will be added to the list of contacts but for now, let's pre-populate 
the list with some made-up contacts.  We can do this by adding code to the 
constructor:

```java
    private AddressBook() {
        mContacts = new ArrayList<>();
        for (int i=0; i<100; i++) {
            Contact contact = new Contact();
            contact.setName("Person " + i);
            contact.setEmail("Person" + i + "@email.com");
            
            // set every 10th as a favorite
            if (i % 10 == 0) {
                contact.setFavorite(true);
            }
            
            mContacts.add(contact);
        }

    }
```  
This will create contacts with names like "Person 1", email addresses like 
"Person1@email.com", and every 10<sup>th</sup> contact marked as a favorite.

### Updating the Controller
We will create a fragment to display our list of contacts.  Just like before 
when we created a fragment to display contact information, we had to first 
create an activity to host the fragment.  If we look at the XML defining 
`activity_contact.xml`, we can see that it doesn't make use of any particular 
fragment:

```xml
<?xml version="1.0" encoding="utf-8"?>
<FrameLayout xmlns:android="http://schemas.android.com/apk/res/android"
             android:id="@+id/fragment_container"
             android:layout_width="match_parent"
             android:layout_height="match_parent">
</FrameLayout>
```

We can actually reuse this activity to host a new fragment we create. 
Right-click on `activity_contact.xml` in the **Project** view and select 
**Refactor -> Rename**; enter `activity_fragment.xml` as the new name.  Our 
code in the *ContactActivity* class should have automatically updated to use 
the fragment's new name.  

Looking at the code for the *ContactActivity* class, we can see that if we 
wanted to create a new activity to host a single fragment, we could reuse most 
of the code.  The exception is that code that creates an instance of 
*ContactFragment*:

```java
fragment = new ContactFragment();
```

Rather than copying and repeating most of this code for a new activity, it 
would be nice if we could create a class from which *ContactActivity* and the 
new activity class we will create could inherit from.  One way we can do this 
is by creating an abstract class with the same code that *ContactActivity* 
currently has but replaces `new ContactFragment()` with a call to method that 
creates a fragment.  If we make this new method abstract in the base class, we 
can force any class that extends the base class to implement the method.  

Let's create a new abstract class named `SingleFragmentActivity`; its code will 
be similar to the code already in *ContactActivity*:

```java
public abstract class SingleFragmentActivity extends FragmentActivity{
    public String getPackage(Context context) {
        return context.getPackageName();
    }

    protected abstract Fragment createFragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fragment);

        FragmentManager fm = getSupportFragmentManager();
        Fragment fragment = fm.findFragmentById(R.id.fragment_container);

        if (fragment==null) {
            fragment = createFragment();
            fm.beginTransaction()
                    .add(R.id.fragment_container, fragment)
                    .commit();
        }

    }
}
```

This new abstract class declares an abstract method, *createFragment()*, that 
will return an instance of the *Fragment* class.  Additionally, the 
*onCreate()* method now calls the *createFragment()* method to assign a value 
to the *fragment* field.  We can now rewrite *ContactActivity* to extend this 
abstract class:

```java
public class ContactActivity extends SingleFragmentActivity {
    @Override
    protected Fragment createFragment() {
        return new ContactFragment();
    }
}
```

Next, we can create the controller classes that will work with the fragment 
used to display a list of contacts.  Let's create two classes: 
*AddressBookFragment* and *AddressBookActivity*. Right now, we won't add any 
code to the *AddressBookFragment* class:

```java
public class AddressBookFragment extends Fragment {
}
```

*AddressBookActivity* will look similar to *ContactActivity* but the 
*createFragment()* will create an instance of *AddressBookFragment*:

```java
public class AddressBookActivity extends SingleFragmentActivity {
    @Override
    protected Fragment createFragment() {
        return new AddressBookFragment();
    }
}
```

In order to use the activity in our app, we have to declare it in the 
application's manifest located in the `app/manifests` folder in the Android 
project view. 

```xml
<?xml version="1.0" encoding="utf-8"?>
<manifest xmlns:android="http://schemas.android.com/apk/res/android"
          package="com.arthurneuman.mycontacts">

    <application
        android:allowBackup="true"
        android:icon="@mipmap/ic_launcher"
        android:label="@string/app_name"
        android:supportsRtl="true"
        android:theme="@style/AppTheme">
        <activity android:name=".ContactActivity">
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

Here, we added another **activity** element and moved the **intent-filter** 
element into the new **activity** element; this tells Android which activity 
should be used when the app starts. If we start the app now, we'll see 
the activity with its *FrameLayout* hosting an empty **AddressBookFragment**.



