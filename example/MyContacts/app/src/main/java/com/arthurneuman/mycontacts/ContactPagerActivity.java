package com.arthurneuman.mycontacts;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import java.util.List;
import java.util.UUID;

public class ContactPagerActivity extends AppCompatActivity implements ContactFragment.Callbacks {
    private static final String EXTRA_CONTACT_ID =
            "com.arthurneuman.mycontacts.contact_id";

    private ViewPager mViewPager;
    private List<Contact> mContacts;

    public static Intent newIntent(Context packageContext, UUID contactID) {
        Intent intent = new Intent(packageContext, ContactPagerActivity.class);
        intent.putExtra(EXTRA_CONTACT_ID, contactID);
        return intent;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_pager);

        mContacts = AddressBook.get(this).getContacts();
        FragmentManager fragmentManager = getSupportFragmentManager();
        mViewPager = (ViewPager) findViewById(
                R.id.activity_contact_pager_view_pager);

        mViewPager.setAdapter(new FragmentStatePagerAdapter(fragmentManager) {
            @Override
            public Fragment getItem(int position) {
                Contact contact = mContacts.get(position);
                return ContactFragment.newInstance(contact.getID());
            }

            @Override
            public int getCount() {
                return mContacts.size();
            }
        });

        UUID contactID = (UUID) getIntent()
                .getSerializableExtra(EXTRA_CONTACT_ID);

        for (int index = 0; index < mContacts.size(); index++) {
            if (mContacts.get(index).getID().equals(contactID)) {
                mViewPager.setCurrentItem(index);
                break;
            }
        }


    }

    @Override
    public void onContactUpdated(Contact contact) {

    }
}