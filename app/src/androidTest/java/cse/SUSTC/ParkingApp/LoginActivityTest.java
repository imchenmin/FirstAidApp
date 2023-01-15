package cse.SUSTC.ParkingApp;
import androidx.test.espresso.ViewInteraction;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import androidx.test.rule.ActivityTestRule;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.assertion.ViewAssertions.doesNotExist;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.core.AllOf.allOf;

@RunWith(AndroidJUnit4.class)
public class LoginActivityTest {

    @Rule
    public transient ActivityTestRule<LogInActivity> mActivityTestRule = new ActivityTestRule<>(LogInActivity.class);

    @Test
    public void normalLoginTest() throws InterruptedException {
        mActivityTestRule.getActivity();
        ViewInteraction inputUname = onView(withId(R.id.LoginUsername));
        inputUname.perform(replaceText("13530544395"), closeSoftKeyboard());
        ViewInteraction inputUpass = onView(allOf(withId(R.id.LoginPassword), isDisplayed()));
        inputUpass.perform(replaceText("abc"), closeSoftKeyboard());
        ViewInteraction loginButton = onView(withId(R.id.LoginBtn));
        loginButton.perform(click());
        Thread.sleep(3000);
        onView(withId(R.id.MainMenu)).check(matches(isDisplayed()) );
    }
    @Test
    public void wrongPassLoginTest() throws InterruptedException {
        ViewInteraction inputUname = onView(withId(R.id.LoginUsername));
        inputUname.perform(replaceText("chen"), closeSoftKeyboard());
        ViewInteraction inputUpass = onView(allOf(withId(R.id.LoginPassword), isDisplayed()));
        inputUpass.perform(replaceText("1233"), closeSoftKeyboard());
        ViewInteraction loginButton = onView(withId(R.id.LoginBtn));
        loginButton.perform(click());
        Thread.sleep(3000);
        onView(withText("Login Fail")).check(matches(isDisplayed()) );
    }
    @Test
    public void wrongPassLoginTest2() throws InterruptedException {
        ViewInteraction inputUpass = onView(allOf(withId(R.id.LoginPassword), isDisplayed()));
        inputUpass.perform(replaceText("1233"), closeSoftKeyboard());
        ViewInteraction loginButton = onView(withId(R.id.LoginBtn));
        loginButton.perform(click());
        Thread.sleep(3000);
        onView(withText("Login Fail")).check(matches(isDisplayed()) );
    }
    @Test
    public void wrongPassLoginTest3() throws InterruptedException {
        ViewInteraction inputUname = onView(withId(R.id.LoginUsername));
        inputUname.perform(replaceText("chen"), closeSoftKeyboard());
        ViewInteraction loginButton = onView(withId(R.id.LoginBtn));
        loginButton.perform(click());
        Thread.sleep(3000);
        onView(withText("Login Fail")).check(matches(isDisplayed()) );
    }

    @Test
    public void goRegisterTest() throws InterruptedException {
        ViewInteraction inputUname = onView(withId(R.id.GoRegisterBtn));
        inputUname.perform(click());
        Thread.sleep(3000);
        onView(withId(R.id.RegisterBtn)).check(matches(isDisplayed()) );
    }
}
