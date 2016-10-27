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
their details.


### Using Fragment Arguments
### Updating

## ViewPagers
