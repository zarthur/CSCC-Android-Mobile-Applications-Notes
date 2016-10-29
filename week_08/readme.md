# Week 8 - Fragment Arguments and ViewPager

## Corresponding Text
*Android Programming*, pp. 193-212

## Starting an Activity from a Fragment
Now that we have the ability to list all our contacts as well as a way of 
displaying contact details, it would be nice if we could view the details 
of a specific contact by selecting it from our list.  We'll look at two ways 
of accomplishing this: by using extras and by using fragment arguments. 

### Using an Intent Extra
Previously, we looked at a way of using intent extras to start an activity from 
another activity; starting an activity from a fragment works in a similar way.

To start, let's modify the behavior of the app when a list item is pressed.  
Previously, we defined the behavior in *ContactHolder.onClick()* in 
`AddressBookFragment.java`.  We were creating a toast to indicate that an item 
was pressed - let's replace that.  

```java
public class AddressBookFragment extends Fragment {
    ...
    private class ContactHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener{
        ...
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(getActivity(), ContactActivity.class);
            startActivity(intent);
        }
    }
    ...
}
```

When a list item is pressed, we'll create an *Intent* using the *Activity* 
returned by the *getActivity()* method inherited from *Fragment* and by 
specifying the class of the activity to start.  If we run the app now and 
tap on one of the listed contacts, we'll see that a blank *ContactFragment* is 
loaded.  We need to be able to indicated to the *ContactFragment* which item 
was tapped.  One way we can do this by adding an extra to the *Intent* that is 
used to start the new activity.  Recall that intent extras rely on name/value 
pairs so we'll need a unique name for any data we want to include as an extra.
Because we'll be using the intent to start the *ContactActivity* activity and 
because it will need to make use of the data in the intent extra, let's add 
a class method that can be used to create an intent for starting 
*ContactActivity* and that stores the ID of a contact.

```java
public class ContactActivity extends SingleFragmentActivity {
    public static final String EXTRA_CONTACT_ID =
            "com.arthurneuman.mycontacts.contact_id";

    public static Intent newIntent(Context packageContext, UUID contactID) {
        Intent intent = new Intent(packageContext, ContactActivity.class);
        intent.putExtra(EXTRA_CONTACT_ID, contactID);
        return intent;
    }
    
    @Override
    protected Fragment createFragment() {
        return new ContactFragment();
    }
}
```

We can now use this method in *ContactHolder.onClick()*:

```java
public class AddressBookFragment extends Fragment {
    ...
    private class ContactHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener{
       ...
        @Override
        public void onClick(View v) {
            Intent intent = ContactActivity.newIntent(getActivity(), 
                    mContact.getID());
            startActivity(intent);
        }
    }
    ...
}
```

This will start the new activity with the ID of the contact that was pressed.  
Next, we need to make use of the ID included with the *Intent* to load the 
appropriate contact and update the view.  First, we can use the ID to load 
the associated contact; in `ContactFragment.java`, modify the *onCreate()* 
method:

```java
public class ContactFragment extends Fragment {
    ...
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        UUID contactID = (UUID) getActivity().getIntent()
                .getSerializableExtra(ContactActivity.EXTRA_CONTACT_ID);
        mContact = AddressBook.get().getContact(contactID);
    }
    ...
}
```

First we get the *Activity* that loaded the *ContactFragment*, then get the 
*Intent* and extra from it.  Now that we have a *Contact*, we'll need to update 
the view to include it's data:

```java
public class ContactFragment extends Fragment {
    ...
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ...
        mNameField = (EditText)v.findViewById(R.id.contact_name);
        mNameField.setText(mContact.getName());
        ...
        mEmailField = (EditText)v.findViewById(R.id.contact_email);
        mEmailField.setText(mContact.getEmail());
        ...
        mFavoriteCheckBox = (CheckBox)v.findViewById(R.id.contact_favorite);
        mFavoriteCheckBox.setChecked(mContact.isFavorite());
        ...
    }
}
```

If we run the app now, we should be able to tap on individual contacts and view 
their details. Though having *ContactFragment* access the intent that is used 
to start *ContactActivity* resulted in straightforward code, the fragment is no 
longer usable with any activity that doesn't provide the appropriate intent. 

### Using Fragment Arguments
Rather than extracting information from an *Activity*, a *Fragment* can have 
a *Bundle* attached to it.  Like intent extras, a bundle contains key/value 
pairs known as arguments.  A bundle can be attached to a fragment using the 
*Fragment.setArguments()* method but it must be used after the fragment 
instance is created and before it is added to an activity.  To do this, we'll 
add a static method to *ContactFragment*:

```java
public class ContactFragment extends Fragment {
    ...
    private static final String ARG_CONTACT_ID = "contact_id";

    public static ContactFragment newInstance(UUID contactID) {
        ContactFragment contactFragment = new ContactFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_CONTACT_ID, contactID);
        contactFragment.setArguments(args);
        return contactFragment;
    }
    ...
}
``` 

*ContactActivity* should now call the new static method when creating the 
fragment:

```java
public class ContactActivity extends SingleFragmentActivity {
    private static final String EXTRA_CONTACT_ID =
            "com.arthurneuman.mycontacts.contact_id";
    ...
    @Override
    protected Fragment createFragment() {
        UUID contactID = (UUID) getIntent()
                .getSerializableExtra(EXTRA_CONTACT_ID);
        return ContactFragment.newInstance(contactID);
    }
}
```

We can also make *ContactActivity.EXTRA_CONTACT_ID* private.

At this point, we've gone from the fragment needing to know something specific 
about it's activity (the intent extra) to the activity needing to know 
something specific about the fragment it's creating (that it has a static 
method that can create an instance with a contact ID); this is an acceptable 
trade-off as hosting activities can be expected to know the specifics of 
how to host their fragments.

Finally, we have to use the contact ID to get an instance of *Contact* in 
*ContactFragment*.  To do this, we can replace the code responsible for 
accessing the hosting activity's intent in *ContactFragment.onCreate()*:

```java
public class ContactFragment extends Fragment {
    ...
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        UUID contactID = (UUID) getArguments().getSerializable(ARG_CONTACT_ID);
        mContact = AddressBook.get().getContact(contactID);
    }
}
```

If we run the app, it should behave as it did before but now *ContactFragment* 
isn't so dependent on its hosting activity.

### Updating
Recall that our code in *contactFragment* included event listeners that updated 
the model when changes were made.  If we tap a contact from the list, change 
it, then go back to the list, we don't see the changes.  In order for the 
changes to appear, we have to modify the *RecyclerView* *Adapter* to expect 
changes.  

The *ActivityManager* maintains a stack known as the "back stack".  As 
activities are created, they are added to the back stack so that a user can use 
the back button to return to a previous activity.  When our app starts, the 
back stack contains *AddresBookActivity*.  When a row is pressed and an 
instance of *ContactActivity* is created, that new instances is added to the 
back stack.  When the back button is pressed, the top activity is removed and 
the next activity is displayed.  

When an activity is no longer at the top of the back stack, it is paused and 
usually stopped.  When it returns to the top, it is started, if necessary, and 
resumed.  When *AddresBookActivity* is resumed, the OS calls *onResume()* on 
any fragments hosted by the activity.  We can use 
*AddressBookFragment.onResume()* to update the list by calling 
*AddresBookFragment.updateUI()*.  We have to be careful to not create a new 
*ContactAdapter* if it already exists; we can call 
*ContactAdapter.notifyDataSetChanged()* instead.

```java
public class AddressBookFragment extends Fragment {
    ...
    @Override
    public void onResume() {
        super.onResume();
        updateUI();
    }

    private void updateUI() {
        AddressBook addressBook = AddressBook.get();
        List<Contact> contacts = addressBook.getContacts();
        if (mContactAdapter == null) {
            mContactAdapter = new ContactAdapter(contacts);
            mAddressBookRecyclerView.setAdapter(mContactAdapter);
        }
        else {
            mContactAdapter.notifyDataSetChanged();
        }
    }
    ...
}
``` 


## ViewPagers
