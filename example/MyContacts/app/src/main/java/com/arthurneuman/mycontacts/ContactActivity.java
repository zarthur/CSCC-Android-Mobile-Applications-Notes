package com.arthurneuman.mycontacts;

import android.support.v4.app.Fragment;

import java.util.UUID;

public class ContactActivity extends SingleFragmentActivity {
    private static final String EXTRA_CONTACT_ID =
            "com.arthurneuman.mycontacts.contact_id";

    @Override
    protected Fragment createFragment() {
        UUID contactID = (UUID) getIntent()
                .getSerializableExtra(EXTRA_CONTACT_ID);
        return ContactFragment.newInstance(contactID);
    }
}