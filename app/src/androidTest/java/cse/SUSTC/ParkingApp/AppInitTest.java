package cse.SUSTC.ParkingApp;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import androidx.test.espresso.ViewInteraction;
import androidx.test.rule.ActivityTestRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
@RunWith(AndroidJUnit4.class)
public class AppInitTest {
    @Rule
    public transient ActivityTestRule<AppInit> appInitTestRule = new ActivityTestRule<>(AppInit.class);

    @Test
    public void goLoginTest() throws InterruptedException {
        appInitTestRule.getActivity();
        ViewInteraction inputUname = onView(withId(R.id.GoLogBtn));
        inputUname.perform(click());
        Thread.sleep(3000);
        onView(withText("or create a account")).check(matches(isDisplayed()) );
    }

    @Test
    public void goReisterTest() throws InterruptedException {
        ViewInteraction inputUname = onView(withId(R.id.GoRegisterBtn));
        inputUname.perform(click());
        Thread.sleep(3000);
        onView(withText("back to sign in")).check(matches(isDisplayed()) );
    }
}
