# Week 2 - MVC and Activities

## Corresponding Text
*Android Programming*, pp. 33-46, 57-71

Now that we have a simple app that allows us to quiz a user, we'd like to
expand it beyond a single question.  We'd like to have a collection of
questions we can present to the user.  The first step to doing this is to
create a new class to represent questions - we'll name it *Question*.  With
each question we'll associate question text, four possible answers, and a value
indicating the correct answer.  The question text will be stored as a string
resource, we we'll store the ID associated with the resource.  Similarly,
the possible answers will be stored in a string array resource, so we'll store
the ID associated with the string array resource.  Our code for the *Question*
class looks like this:

```java
package com.arthurneuman.triviaquiz;

public class Question {
    private int mQuestionResId;
    private int mAnswersResId;
    private int mCorrectAnswer;

    public Question(int questionResId, int answersResId, int correctAnswer) {
        mQuestionResId = questionResId;
        mAnswersResId = answersResId;
        mCorrectAnswer = correctAnswer;
    }

}
```

Notice that instance variables are prefixed with the letter `m`.  By convention
in Android, instance fields are prefixed with the letter `m` and class or
static fields are prefixed with the letter `s`.  While fields have these
prefixes, getters and setters do not.  If we want to automatically generate
getters and setters with Android Studio, we can configure it expect `m`s and
`s`s prefixed to field names.  To do this select **Editor -> Code Style ->
Java** in Android Studio's preferences window, select the **Code Completion**
tab, and type `s` and `m` in the **Name prefix** column and **Field** and
**Static Fields**, respectively.  

![code completion](images/preferences.png)

## Model-View-Controller
### Example

### Benefits

## Activities
### Activity Lifecycle
### Demonstration of the Activity Lifecycle
### Rotation
### Saving Data
