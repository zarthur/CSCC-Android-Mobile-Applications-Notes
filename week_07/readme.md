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
