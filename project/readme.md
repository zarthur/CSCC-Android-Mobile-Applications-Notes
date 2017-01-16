# Project

During the semester, teams will work on developing a to-do app capable of 
tracking tasks. As new topics are introduced during the semester, teams will be 
expected to implement similar functionality in their apps.  The apps will be 
reviewed at sever points during the semester. The features that should be 
implemented prior to each review are listed below; see milestones.  In addition 
to submitting the app developed by the team, each team member is expected to 
submit a brief summary of his/her contributions since the last review.  During 
finals week, each team will present their app to the class.

## Description
The to-do app will allow users to keep track of tasks.  Users will be able to 
store the following details for each task:

- Task name
- Task description
- Task incomplete or complete
- Task due date
- Task location
- Task category

When the app is started, the user should be presented with a scrollable list of 
tasks. The same view should contain a summary of the number of tasks to 
complete vs the number done and a menu with the option to hide/show completed 
items, to configure categories, and an option to view other settings (if 
necessary). The first screen should also allow the user to create a new task.  

When a user taps on an existing task, the screen should change to present the 
user with details for the task (listed above).  In addition to displaying an 
address for the task's location, a map with a marker of the location should be 
displayed.  The user should be able to delete the task from this screen.  The 
user should also be able to swipe left and right to browse through task details. 
From this screen, the user should also be able to share details of the task via 
text message, email, or another app.

When selecting the option to configure categories from the menu of the first 
screen, the app should display a scrollable list of categories. When the user 
clicks a category, the categories details should be displayed including the 
category's name and any color/icon used to symbolize that category.  The user 
should be able to modify the category's name from the details screen.

Task data should be stored to a local database, allowing data to be reloaded 
when the app restarts.  Additionally, the app should allow the user to specify 
the URL, username, and password of remote server to which task data can be 
synced; the app should either automatically sync data or allow the user to 
manually sync the data with the remote server.

## Milestones
Development of the project app will occur throughout the semester.  Several 
times during the semester, progress will be evaluated. Each evaluation will 
correspond to a milestone and for each milestone a specific set of features 
must be implemented in the project app.  Groups will be evaluated on both the 
app (as a group) and on individual contributions to the app; individuals will 
be responsible for documenting their contributions and submitting the 
documentation for a milestone review.

There will be four milestones.  The set of features to be implemented for each 
milestone appears below.  See the syllabus for the due dates of each milestone.

- **Milestone 1 - week 1 through week 4**
    - Model classes representing tasks and categories
    - One Activity to host a single Fragment
    - A Fragment with widgets to store task name and description
    - Code to save task name and description as they are entered
    - Log messages indicating updated task name and description
- **Milestone 2 - week 5 through week 7**
    - Unit tests of model and controller functionality
    - Task fragment includes fields for name, description, status 
      (complete/incomplete), due date, category, and address
    - Class representing a collection of tasks and a class representing a 
      collection of categories; these should be pre-populated with data
    - Activity hosting a fragment with a RecyclerView to display the list 
      of tasks and an Adapter to provide data to the view
    - Tapping a task loads the activity with task's details
    - Display number of completed and incomplete tasks
- **Milestone 3 - week 8 through week 11**
    - Use of a ViewPager to enable swiping between task details
    - Menu items to support creating a new task, viewing categories, and hiding 
      completed tasks
    - Menu item to delete a task
    - RecyclerView to display catagories and a ViewPager to display category 
      details; ability to modify category name.
    - Toolbars supporting hierarchical navigation.
    - Task details include a map indicating task location
    - Ability to share task details via another app (text message, email, etc.)
- **Milestone 4 - week 12 through week 15**
    - Store task and category data to a database in order to persist data
    - Provide a settings interface to allow the user to store the URL and 
      credentials of a remote server that can be used to sync task data
    - Implement code necessary to sync task data with a remote server using 
      JSON and a RESTful API
    - Either automatically sync task data with a remote server periodically or 
      provide the user with the means of manually starting the sync process
    - Provide a two-pane layout for use with tablet devices
    - Generate a signed APK that can be used for app distribution

## Example
A mocked-up example of a to-do app can be found 
[here](https://github.com/zarthur/CSCC-Android-Mobile-Applications-Notes/raw/master/project/files/todo-prototype.zip). 
After downloading and unzipping the file, load the `frame.html` file. This 
example only demonstrates some functionality of the app to be developed; see 
the description and milestones above for more details.