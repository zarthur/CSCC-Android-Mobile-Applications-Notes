# Week 9 - Custom Views 

## Corresponding Text
*Android Programming*, pp. 527-537

While Android includes a wide variety of widgets for us to choose from, the 
widgets that are available might not suite our needs completely.  Fortunately, 
we can create custom views and widgets as desired.  Right now, we are using 
a checkbox in the UI to allow a user to choose whether a contact is a favorite 
or not.  While the checkbox is certainly functional, it defintely isn't 
visually appealing.  Perhaps we'd like to replace the checkbox with a star icon 
that is a solid color if a contact is a favorite and only an outline of a star 
when the contact is not a favorite.  To do this, we'll create a custom view.

## Prep
Our custom view will make use of at least two resources: an image of a solid 
star and and image of the outline of a star.  We can create these images 
ourselves or we could use the 
[Android Asset Studio](https://romannurik.github.io/AndroidAssetStudio).  

Below is an example of two star images.

![stars](images/stars.png) 

When using Android Asset Studio, notice that the zip file includes several 
folders.  Each folder contains the generated image at a different resolution. 
Android will use the appropriately-sized image depending on the device in an 
attempt to make the UI appear consistent across different devices. Copy these 
folders and their contents into the app's `res` folder in Android Studio.  Be 
sure that both the filled and outlined version are in each folder.

## Creating a Custom View
Views can be grouped into two categories: simple and composite.  Simple views 
have no child views and will usually perform custom rendering.  Composite 
views are composed of other view objects that it manages and usually delegates 
rendering ot the child objects. For our example, we'll create a simple custom 
view.

To create a custom view we'll need to do three things. First, we'll choose a 
parent class to base our view on.  For a simple custom view, we can use the 
*View* class which will provide us with a blank canvas.  Composite views are 
often subclasses of a layout class.  Next, we'll create a subclass from the 
superclass, overriding the constructor as necessary.  Finally, we'll override 
other methods in order to provide the functionality our custom view needs.

### Custom View Class
Let's create a new class, *FavoriteView*, to represent our new widget.  This 
class will be a subclass of *android.view.View*.  The *View* class doesn't 
provide a default constructor, so we'll need to add a constructor.  Typically, 
we'll add two constructors: one used when creating the view in code and one 
used when inflating the view from XML.  

```java
public class FavoriteView extends View {
    public FavoriteView(Context context) {
        super(context);
    }

    public FavoriteView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
}
```

We're now able to use the view in our app (thought it won't appear as anything 
yet).  Update the *fragment_contact.xml* layout file by replacing the 
*CheckBox* widget with the new *FavoriteView*; note that we have to use the 
fully qualified class name.  

```xml
<LinearLayout ...>
    ...
    <com.arthurneuman.mycontacts.FavoriteView
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:id="@+id/contact_favorite"/>
    ...
</LinearLayout>
```

As a start, we'd like our new widget to display an outlined start.  To enable 
this functionality, we'll need to override *View.onDraw(Canvas canvas)*. A
*Canvas* instance allows us to draw on it.  

There are multiple ways to add a drawable resource to a canvas, we'll make use 
of a method that first loads the resource as a *Bitmap* then draws the bitmap 
on the canvas.  To draw the bitmap, we'll also need an instance of the *Paint* 
class.  Instances of *Canvas* provide us a place to draw and instances of 
*Paint* determine how the drawing is done.  Rather than create instances of 
*Paint* and *Bitmap* in the *onDraw()* method, we can create a method that the 
constructors use.

```java
public class FavoriteView extends View {
    private Paint mPaint;
    private Bitmap mStarEmpty;
    private Bitmap mStarFull;

    public FavoriteView(Context context) {
        super(context);
        preallocate();

    }

    public FavoriteView(Context context, AttributeSet attrs) {
        super(context, attrs);
        preallocate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawBitmap(mStarEmpty, 0, 0, mPaint);
    }

    private void preallocate() {
        mPaint = new Paint();

        mStarEmpty = BitmapFactory.decodeResource(getResources(), R.drawable.ic_star_empty);
        mStarFull = BitmapFactory.decodeResource(getResources(), R.drawable.ic_star_full);
    }
}
```

We've added three new fields to the *FavoriteView* class: one for an instance 
of *Paint* and two *Bitmap* instances for our empty and filled stars.  We 
assign values to these fields in the *preallocate()* method and draw to the 
canvas in the *onDraw()* method.

We could add an additional private, boolean field, *isSelected* to support 
toggling between a selected and unselected state.  We would also add a getter 
and setter will that would us to use the widget in much the same way we used 
the *CheckBox* widget. The *onDraw()* method would check the value of the 
field to determine which image to show.  Fortunately, *View* already implememts 
the *isSelected()* and *setSelected()* methods, so we only need to update the 
*onDraw()* method.

```java
public class FavoriteView extends View {
    ...

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Bitmap image = isSelected() ? mStarFull : mStarEmpty;
        canvas.drawBitmap(image, 0, 0, mPaint);
    }


    ...    
}
```

We can now update *ContactFragment* to set the state of the new widget based 
on whether or not a contact is a favorite. 

```java
public class ContactFragment extends Fragment {
    ...
    private FavoriteView mFavoriteView;
    ...

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ...

        mFavoriteView = (FavoriteView) v.findViewById(R.id.contact_favorite);
        mFavoriteView.setSelected(mContact.isFavorite());

        ...
    }

    ...
}
```

### Handling Touch Events

