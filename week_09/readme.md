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
![stars](images/stars) 

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

### Handling Touch Events

