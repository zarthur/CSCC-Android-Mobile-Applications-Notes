# Week 3 - Debugging

## Corresponding Text
*Android Programming*, pp. 75-85

## Logging
As we develop applications, we're likely to run into unexpected behavior
including thrown exceptions and errors. When this happens, we can debug our
application.  At the most basic level, we can log messages about our
application's state to help determine what the problem is.  We can also use
more advanced tools to find and address issues.

To begin, let's modify our existing code.  If we comment out the following line
in *QuizActivity*, our app will crash.

```java
mQuestionTextView = (TextView) findViewById(R.id.question_text_view);
```

After commenting out the code, try running the application.  The application
should crash.

![crash](images/crash.png)

If you look at the log window in Android Studio you should see a stack trace
that indicates a *RuntimeException* was thrown and was caused by a
*NullPointerException*.  The details of the *NullPointerException* indicate that
we tried to call *setText()* on a *Null* objects.  *mQuestionTextView* is null
because we commented out the code that assigned a value to that field.  Here,
the stack trace is helpful in finding the bug.  Let's uncomment the code we
previously commented out and next comment out the following line in one of
the listeners in *onCreate()*:

```java
mCurrentIndex = (mCurrentIndex + 1) % mQuestions.length;
```

Now, the app shouldn't crash when we run it but the **Next** button doesn't do
anything.  Let's pretend we don't know why the button isn't working.  One thing
we could do is add code to log relevant information when the button is pressed.  

Modify *onCreate()* to include the following code for the the *mNextButton*'s
listener:

```java
mNextButton.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
        // mCurrentIndex = (mCurrentIndex + 1) % mQuestions.length;
        Log.d(TAG, "Next clicked: mCurrentIndex = " + mCurrentIndex);
        displayQuestion();
    }
});
```

Now if we run the app and click the **Next** button, we should see log messages
similar to the following:

```
06-30 18:45:24.450 9681-9681/com.arthurneuman.triviaquiz D/QuizActivity: Next clicked: mCurrentIndex = 0
06-30 18:45:25.265 9681-9681/com.arthurneuman.triviaquiz D/QuizActivity: Next clicked: mCurrentIndex = 0
06-30 18:45:25.993 9681-9681/com.arthurneuman.triviaquiz D/QuizActivity: Next clicked: mCurrentIndex = 0
```

As indicated by the log messages, the value of *mCurrentIndex* is not changing.
We know why (because we commented out the code that increments
*mCurrentIndex*) but often the reason isn't obvious and adding log messages
can be helpful in determining what is causing unexpected behavior.  We can
uncomment the code to increment *mCurrentIndex* to fix the bug.

## Breakpoints

## Android Lint

## Issues with the *R* class
