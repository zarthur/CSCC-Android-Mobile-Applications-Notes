package com.arthurneuman.mycontacts;

import org.junit.Test;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertNotNull;

public class ContactTest {
    @Test
    public void contactAccessorTest() {
        String name = "Test Name";
        String email = "name@test.com";
        Contact contact = new Contact();
        contact.setName(name);
        contact.setEmail(email);
        assertTrue((contact.getName().equals(name)
                && contact.getEmail().equals(email)));
    }

    @Test
    public void UUIDcreationTest() {
        Contact contact = new Contact();
        assertNotNull(contact.getID());
    }
}
