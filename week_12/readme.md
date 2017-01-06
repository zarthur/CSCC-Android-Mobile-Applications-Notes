# Week 12 - Data Persistence

## Corresponding Text
*Android Programming*, pp. 257-271

So far, all the data in our model has been automatically generated when we 
start the application and any changes we make while using the app are lost when 
the app is restarted.  Ideally, we'd like to have any new contacts the user 
creates or changes made to existing contacts persist.  One way to do this is 
to serialize our data, save it to a file, and load the file when our app 
starts.  The biggest drawback to this approach is that as the amount of data 
we need to store/load increases, the worse performance will become.  An 
alternative to serializing the data to a file is to use a database.  

The database we'll create will rely on the SQLite relational database 
management system.  With SQLite, we'll be able to create relational databases 
like with other database management systems such as SQL Server or MySQL but 
SQLite uses a much simpler file structure for saving the data.  Thanks to 
Android and Java libraries, we won't have to worry too much about the 
underlying structure.  

## Defining a Schema and Creating a Database
Before we create a database and start storing data, we need to determine what 
data we'll store in the database and thus what the structure of our database 
will be.  Any database could contain many table with each table storing some 
set of related data.  If you haven't worked with databases before, you can 
think of a table as a spreadsheet: each record or entry in the table would 
correspond to a row in the spreadsheet and each column or field in the table 
would be a column in the spreadsheet.  

For our app, we're interested in storing contact details and nothing else so 
our database will consist of a single table.  The fields for each record in 
the table will correspond to data we're storing in private fields in the 
*Contact* class: name, email,  etc.

| _id | uuid           | name | email         | favorite | address                   | image    |
|-----|----------------|------|---------------|----------|---------------------------|----------|
| 0   | 13090636733242 | Bob  | bob@email.com | false    | 123 Main St, Columbus, OH | \<data\> |
| 1   | 13090732131909 | Sue  | sue@email.com | true     | 456 High St, Columbus, OH | \<data\> |

The table above is an example data in the we'll store in the database table we 
create.  In additional to the id we're currently storing for each contact, a 
*UUID*, the database will also store a sequential id for each record. 

To start, we'll need to create a new Java class to represent the database 
schema.  When creating a new class, use the name `database.ContactDbSchema`. 
This will store the database class in its own package, keeping our database 
code separate from the rest of our code.  

The newly created class will represent the database in its entirety, including 
its tables.  Each table will be represented by a nested class. 

```java
public class ContactDbSchema {
    public static final class ContactTable {
        public static final String NAME = "contacts";
    }
}
```

Here, we're using the *NAME* field to denote the table name.  Next, we'll 
create another nest class to store the column names:

```java
package com.arthurneuman.mycontacts.database;

public class ContactDbSchema {
    public static final class ContactTable {
        public static final String NAME = "contacts";

        public static final class Cols {
            public static final String UUID = "uuid";
            public static final String NAME = "name";
            public static final String EMAIL = "email";
            public static final String ADDRESS = "address";
            public static final String FAVORITE = "favorite";
            public static final String IMAGE = "image";
        }
    }
}
```

The advantage to defining the database schema in this way is that we will be 
able to access column names in Java using something like 
`ContactTable.Cols.EMAIL` rather than repeatedly using the string `"email"`.  

Whenever we use a database there are a few steps we should always go through: 
1. Check to see if the database exists.
2. If it does not, create it and any initial data.
3. If the database exists, check the version (keeping track of a version will 
   allow us to make changes to the database as we make changes to the app).
4. If the database is an old version, run code to upgrade it.

Fortunately, Android provides us with the *SQLiteOpenHelper* class to help us 
with this.  Let's create a new class, *ContactBaseHelper* in the *database* 
package that extends the helper class.

```java
public class ContactBaseHelper extends SQLiteOpenHelper{
    private static final int VERSION = 1;
    private static final String DATABASE_NAME = "contactBase.db";

    public ContactBaseHelper(Context context) {
        super(context, DATABASE_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
```

Here, we've defined two constants representing the database version and the 
database filename.  The base class does not have a default constructor so we 
have to provide a constructor.  We'll ultimately make use of the base class 
contstructor that requires a *Context*, a *String* for a name, a 
*CursorFactory* (which we won't need), and an *int* for the version.

Because the code necessary for *onCreate()* and *onUpgrade()* are unique to the 
application and the database being used, Android can't provide any default 
functionality for us so we'll have to add our own code.  For now, we won't 
worry about changes database versions so we won't add any code for the 
*onUpgrade()* method.  For the *onCreate()* method, we will have to add some 
code that includes a SQL statement to create our table.

```java
public class ContactBaseHelper extends SQLiteOpenHelper{
    private static final int VERSION = 1;
    private static final String DATABASE_NAME = "contactBase.db";

    public ContactBaseHelper(Context context) {
        super(context, DATABASE_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String sql = "create table " + ContactTable.NAME + "( " +
                "_id integer primary key autoincrement, " +
                ContactTable.Cols.UUID + ", " +
                ContactTable.Cols.NAME + ", " +
                ContactTable.Cols.EMAIL + ", " +
                ContactTable.Cols.FAVORITE + ", " +
                ContactTable.Cols.ADDRESS + ", " +
                ContactTable.Cols.IMAGE + 
                ")";
                sqLiteDatabase.execSQL(sql);

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
```

Note that to avoid having to type names like `ContactDbSchema.ContactTable...`, 
you can add an import statement for the *ContactDbSchema* class.

We're now at a point where we can begin using the database in our app. Recall 
that the *AddressBook* class is responsible for keeping track of all our 
contacts; this is where we will make our changes.  To start, let's remove 
the *mContacts* field and any code that relies on it.  Next, 

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

    public void add(Contact contact) {
    }

    public List<Contact> getContacts() {
        return new ArrayList<>();
    }

    public Contact getContact(UUID id) {
        return null;
    }

    public List<Contact> getFavoriteContacts() {
        return new ArrayList<>();
    }
}
```

To *AddressBook*, we'll add a private field, *mDatabase*, and assign a value 
to it in the constructor. Because the *ContactBaseHelper* constructor requires 
a *Context* object, we'll have to add *Context* as a parameter to 
*AddressBook*'s constructor and *get()* method.  Be sure to update other 
locations that call *AddressBook.get()*.

```java
public class AddressBook {
    private static AddressBook sAddressBook;
    private SQLiteDatabase mDatabase;

    private AddressBook(Context context) {
        mDatabase = new ContactBaseHelper(context).getWritableDatabase();
    }

    public static AddressBook get(Context context) {
        if (sAddressBook == null) {
            sAddressBook = new AddressBook(context);
        }
        return sAddressBook;
    }
    
    ...
}
```

## Writing Data
Writes and updates to the database can be done with the assistance of a the 
*ContentValues* class.  Like *HashMap* and *Bundle*, *ContentValues* is a 
key-value store but it is designed to store the types of data SQLite can hold.

Let's add a helper method to *AddressBook* to store data from a *Contact* in 
an instance of *ContentValues*:

```java
public class AddressBook {
    ...
    private static ContentValues getContentValues(Contact contact) {
        // convert image to a byte array for storage
        byte[] imageData = {};
        if (contact.getImage() != null) {
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            contact.getImage().compress(Bitmap.CompressFormat.PNG, 0, stream);
            imageData = stream.toByteArray();
        }

        ContentValues values = new ContentValues();
        values.put(ContactTable.Cols.UUID, contact.getID().toString());
        values.put(ContactTable.Cols.NAME, contact.getName());
        values.put(ContactTable.Cols.EMAIL, contact.getEmail());
        values.put(ContactTable.Cols.FAVORITE, contact.isFavorite() ? "true" : "false");
        values.put(ContactTable.Cols.ADDRESS, contact.getAddress());
        values.put(ContactTable.Cols.IMAGE, imageData);

        return values;
    }

}
```
Note that keys correspond to the column names - if we had misspelled the column 
name, adding/updating the database would fail.  Also, we have to convert our 
image data to a byte array before we can store it in the database.  

Now that we have the *getContentValues()* helper method, we can add code to add 
and update contacts:

```java
public class AddressBook {
    ...
    
    public void add(Contact contact) {
        ContentValues values = getContentValues(contact);
        mDatabase.insert(ContactTable.NAME, null, values);
    }


    public void updateContact(Contact contact) {
        String uuidString = contact.getID().toString();
        ContentValues values = getContentValues(contact);
        mDatabase.update(ContactTable.NAME, values, 
                ContactTable.Cols.UUID  + " = ?", 
                new String[] { uuidString });
    }

    ...
}
```

In our *add()* method, we are using the *SQLiteDatabase.insert()* method.  This 
method takes three arguments: the first is the table name and the third is an 
instance of *ContentValues* with data to add to the database; the second column 
is used when *add()* is called with an instance of *ContentValues* without any 
data.

Simiarly, in *updateContact()*, we use the *SQLiteDatabase.update()* method.  
This method takes four parameters: the name of the table to update, the new 
data, a SQL string used to specify which rows to update, and an array of 
strings with values used when specifying rows.  

To update a contact, we'll rely on the lifecycle of *ContactFragment* - when it 
goes out of view, we'll update the corresponding database record:

```java
public class ContactFragment extends Fragment {
    ...

    @Override
    public void onPause() {
        super.onPause();
        AddressBook.get(getContext()).updateContact(mContact);
        mMapView.onPause();
    }

    ...
}
```

## Reading Data
To retrieve data from the database, we'll use the *sqLiteDatabase.query()* 
method.  This method is overloaded and will return a *Cursor* object.  The 
version of the overloaded method that we'll be interested in is 

```
public Cursor query(String table, String[] columns, String where, 
                    String[] whereArgs, String groupBy, String having, 
                    String orderBy, string limit)
```

which allows us to specify the table name, the columns we are interested in 
(specifying null indicates that we want all columns), and where/whereArgs to 
filter the results. We won't use the remaining parameters.  We can create 
a helper method for working with *query()* in the *AddressBook* class.

```java
public class AddressBook {
    ...

    private Cursor queryContacts(String whereClause, String[] whereArgs) {
        return mDatabase.query(ContactTable.NAME, null, whereClause, whereArgs, 
                null, null, null, null);
    }
}
```

A *Cursor* can be used to retrieve the data we need but it's often cumbersome 
to work with. Instead, we can use a *CursorWrapper* that will allow us to add 
additional methods.

Before we create a subclass of *CursorWrapper*, let's first make a change to 
the *Contact* class.  We'll need to be able to specify a UUID when creating a 
new instance of the class.



```java
public class Contact {
    ...
    public Contact() {
        this(UUID.randomUUID());        
    }
    
    public Contact(UUID id) {
        mID = id;
    }

    ...
}
```

In the database package create a new *ContactCursorWrapper* class.  

```java
public class ContactCursorWrapper extends CursorWrapper {
    public ContactCursorWrapper(Cursor cursor) {
        super(cursor);
    }

    public Contact getContact() {
        String uuid = getString(getColumnIndex(ContactTable.Cols.UUID));
        String name = getString(getColumnIndex(ContactTable.Cols.NAME));
        String email = getString(getColumnIndex(ContactTable.Cols.EMAIL));
        String favorite = getString(getColumnIndex(ContactTable.Cols.FAVORITE));
        String address = getString(getColumnIndex(ContactTable.Cols.ADDRESS));
        byte[] imageData = getBlob(getColumnIndex(ContactTable.Cols.IMAGE));
        // convert the byte array into a Bitmap
        Bitmap image = BitmapFactory.decodeByteArray(imageData, 0, imageData.length);


        Contact contact = new Contact(UUID.fromString(uuid));
        contact.setName(name);
        contact.setEmail(email);
        contact.setFavorite(favorite.equals("true");
        contact.setImage(image);

        return contact;
    }
}
```

When we call *getContact()*, the method will use the wrapped cursor object to 
retrieve data from the database then use that data to create a new *Contact* 
object.

Now that we have a *ContactCursorWrapper*, update the 
*AddressBook.queryContacts()* method to return an instance of the new class 
rather than an instance of *Cursor*:

```java
public class AddressBook {
    private ContactCursorWrapper queryContacts(String whereClause, String[] whereArgs) {
        Cursor cursor = mDatabase.query(ContactTable.NAME, null, whereClause, whereArgs,
                null, null, null, null);
        return new ContactCursorWrapper(cursor);
    }
}
```
 We're now ready to update the *getContact()*, *getContacts()*, and 
 *getFavoriteContacts()* methods.

 ```java
public class AddressBook {
    public List<Contact> getContacts() {
        List<Contact> contacts = new ArrayList<>();
        // get all contacts
        ContactCursorWrapper cursorWrapper = queryContacts(null, null);
        try {
            cursorWrapper.moveToFirst();
            while (!cursorWrapper.isAfterLast()) {
                contacts.add(cursorWrapper.getContact());
                cursorWrapper.moveToNext();
            }
        }
        finally {
            cursorWrapper.close();        
        }
        return contacts;
    }

    public Contact getContact(UUID id) {
        // get only contacts with matching UUID
        ContactCursorWrapper cursorWrapper = queryContacts(
                ContactTable.Cols.UUID + " = ?", 
                new String[] { id.toString() }
        );
        try {
            if (cursorWrapper.getCount() == 0) {
                return null;
            }
            
            cursorWrapper.moveToFirst();
            return cursorWrapper.getContact();
        }
        finally {
            cursorWrapper.close();
        }
        
    }

    public List<Contact> getFavoriteContacts() {
        List<Contact> contacts = new ArrayList<>();
        // get only contacts with favorite == 1
        ContactCursorWrapper cursorWrapper = queryContacts(
                ContactTable.Cols.FAVORITE + " = ?", 
                new String[] { "true" }
        );
        try {
            if (cursorWrapper.getCount() == 0) {
                return contacts;
            }
            
            cursorWrapper.moveToFirst();
            while (!cursorWrapper.isAfterLast()) {
                contacts.add(cursorWrapper.getContact());
                cursorWrapper.moveToNext();
            }
        }
        finally {
            cursorWrapper.close();
        }
        return contacts;
    }
}
```

You can think of a cursor as a placeholder always pointing to a specific place. 
To pull data out a of query with a cursor, we have to explicitly move the 
cursor, calling *moveToFirst()* and *moveToNext()*.

In the *getContacts()* method, we iterate through the results by moving the 
cursor from one result to the next, each time converting the data to a 
*Contact* object and storing it in a list.  The *getContact()* method uses the 
*whereClause* and *whereArgs* parameters of the *queryContacts()* method to 
return results with the specified UUID.  The *getFavoriteContacts()* method 
uses the *whereClause* and *whereArgs* parameters and iterates through the 
results.  I

Finally, we need to update the code associated with the *ContactAdapter* class 
in `AddressBookFragment.java`.  Right now, if we add a contact, then press the 
back button, the new contact will not appear in our list of contacts even 
though it's been saved to the database.  We can create a new method in 
*ContactAdapter* to update the its list of crimes and then call that method 
in *AddressBookFragment.updateUI()*.

```java
public class AddressBookFragment extends Fragment {
    ...

    private void updateUI() {
        AddressBook addressBook = AddressBook.get(getContext());
        List<Contact> contacts = addressBook.getContacts();
        if (mContactAdapter == null) {
            mContactAdapter = new ContactAdapter(contacts);
            mAddressBookRecyclerView.setAdapter(mContactAdapter);
        }
        else {
            mContactAdapter.setContacts(contacts);
            mContactAdapter.notifyDataSetChanged();
        }
    }

    ...

    private class ContactAdapter extends RecyclerView.Adapter<ContactHolder> {
        ...

        public void setContacts(List<Contact> contacts) {
            mContacts = contacts;
        }
    }
}
```