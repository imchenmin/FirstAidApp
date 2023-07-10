package cse.SUSTC.ParkingApp;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.core.AllOf.allOf;

import androidx.test.espresso.ViewInteraction;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Random;

@RunWith(AndroidJUnit4.class)
public class RegisterActivityTest {

    @Rule
    public transient ActivityTestRule<RegisterActivity> registerActivityTestRule = new ActivityTestRule<>(RegisterActivity.class);

    @Test
    public void normalResTest() throws InterruptedException {
        registerActivityTestRule.getActivity();
        Random r = new Random();
        int usernameTst = r.nextInt(1000000);
        ViewInteraction inputUname = onView(withId(R.id.RegisterUsername));
        inputUname.perform(replaceText(String.valueOf(usernameTst)), closeSoftKeyboard());
        ViewInteraction inputUpass = onView(allOf(withId(R.id.RegisterPassword), isDisplayed()));
        inputUpass.perform(replaceText("123456"), closeSoftKeyboard());
        ViewInteraction inputTel = onView(allOf(withId(R.id.RegisterTel), isDisplayed()));
        inputTel.perform(replaceText("13530503757"), closeSoftKeyboard());
        ViewInteraction inputOtp = onView(allOf(withId(R.id.RegisterOpt), isDisplayed()));
        ViewInteraction otpButton = onView(allOf(withId(R.id.GetOptNum), isDisplayed()));
        otpButton.perform(click());
        inputOtp.perform(replaceText("745170"), closeSoftKeyboard());

        ViewInteraction loginButton = onView(withId(R.id.RegisterBtn));
        loginButton.perform(click());
        Thread.sleep(3000);
        onView(withId(R.id.GoRegisterBtn)).check(matches(isDisplayed()) );
    }

    @Test
    public void wrongResTest() throws InterruptedException {
        Random r = new Random();
        int usernameTst = r.nextInt(1000000);
        ViewInteraction inputUname = onView(withId(R.id.RegisterUsername));
        inputUname.perform(replaceText(String.valueOf(usernameTst)), closeSoftKeyboard());
        ViewInteraction inputUpass = onView(allOf(withId(R.id.RegisterPassword), isDisplayed()));
        inputUpass.perform(replaceText("123456"), closeSoftKeyboard());
        ViewInteraction inputTel = onView(allOf(withId(R.id.RegisterTel), isDisplayed()));
        inputTel.perform(replaceText("a@12esdag"), closeSoftKeyboard());
        ViewInteraction loginButton = onView(withId(R.id.RegisterBtn));
        loginButton.perform(click());
        Thread.sleep(3000);
        onView(withText("Register Fail")).check(matches(isDisplayed()) );
    }
    @Test
    public void wrongResTest2() throws InterruptedException {
        Random r = new Random();
        int usernameTst = r.nextInt(1000000);
        ViewInteraction inputUpass = onView(allOf(withId(R.id.RegisterPassword), isDisplayed()));
        inputUpass.perform(replaceText(String.valueOf(usernameTst)), closeSoftKeyboard());
        ViewInteraction inputTel = onView(allOf(withId(R.id.RegisterTel), isDisplayed()));
        inputTel.perform(replaceText("a@12esdag"), closeSoftKeyboard());
        ViewInteraction loginButton = onView(withId(R.id.RegisterBtn));
        loginButton.perform(click());
        Thread.sleep(3000);
        onView(withText("Register Fail")).check(matches(isDisplayed()) );
    }
    @Test
    public void wrongResTest3() throws InterruptedException {
        Random r = new Random();
        int usernameTst = r.nextInt(1000000);
        ViewInteraction inputUname = onView(withId(R.id.RegisterUsername));
        inputUname.perform(replaceText(String.valueOf(usernameTst)), closeSoftKeyboard());
        ViewInteraction inputUpass = onView(allOf(withId(R.id.RegisterPassword), isDisplayed()));
        inputUpass.perform(replaceText("123456"), closeSoftKeyboard());
        ViewInteraction loginButton = onView(withId(R.id.RegisterBtn));
        loginButton.perform(click());
        Thread.sleep(3000);
        onView(withText("Register Fail")).check(matches(isDisplayed()) );
    }
    @Test
    public void gobackTest() throws InterruptedException {
        ViewInteraction inputUname = onView(withId(R.id.GoLogBtn));
        inputUname.perform(click());
        Thread.sleep(3000);
        onView(withId(R.id.GoRegisterBtn)).check(matches(isDisplayed()) );
    }
}
