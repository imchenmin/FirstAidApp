package cse.SUSTC.ParkingApp;

import androidx.test.espresso.ViewAssertion;
import androidx.test.espresso.ViewInteraction;
import androidx.test.rule.ActivityTestRule;

import org.junit.Rule;
import org.junit.Test;

import java.util.Random;

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

public class CarRegisterTest {
    @Rule
    public transient ActivityTestRule<LogInActivity> appInitTestRule = new ActivityTestRule<>(LogInActivity.class);

    @Test
    public void carListTest() throws InterruptedException {
        appInitTestRule.getActivity();
        ViewInteraction inputUname = onView(withId(R.id.LoginUsername));
        inputUname.perform(replaceText("13530544395"), closeSoftKeyboard());
        ViewInteraction inputUpass = onView(allOf(withId(R.id.LoginPassword), isDisplayed()));
        inputUpass.perform(replaceText("abc"), closeSoftKeyboard());
        ViewInteraction loginButton = onView(withId(R.id.LoginBtn));
        loginButton.perform(click());
        Thread.sleep(3000);
        ViewInteraction usermenu = onView(withId(R.id.NavigationUser));
        usermenu.perform(click());
        Thread.sleep(1000);
        ViewInteraction qr = onView(withText("My Car"));
        qr.perform(click());
        Thread.sleep(1000);
        onView(withId(R.id.addCarBtn)).check(matches(isDisplayed()) );
    }
    @Test
    public void addCarTest() throws InterruptedException {
        appInitTestRule.getActivity();
        ViewInteraction inputUname = onView(withId(R.id.LoginUsername));
        inputUname.perform(replaceText("13530544395"), closeSoftKeyboard());
        ViewInteraction inputUpass = onView(allOf(withId(R.id.LoginPassword), isDisplayed()));
        inputUpass.perform(replaceText("abc"), closeSoftKeyboard());
        ViewInteraction loginButton = onView(withId(R.id.LoginBtn));
        loginButton.perform(click());
        Thread.sleep(3000);
        ViewInteraction usermenu = onView(withId(R.id.NavigationUser));
        usermenu.perform(click());
        Thread.sleep(1000);
        ViewInteraction qr = onView(withText("My Car"));
        qr.perform(click());
        Thread.sleep(1000);
        onView(withId(R.id.addCarBtn)).perform(click());
        int flag = new Random().nextInt(99999);
        if (flag < 10000)
        {
            flag += 10000;
        }
        Thread.sleep(1000);
        ViewInteraction inputPlate = onView(withId(R.id.CarPlate));
        inputPlate.perform(replaceText("粤A" + String.valueOf(flag)), closeSoftKeyboard());

        ViewInteraction addcar = onView(withId(R.id.RegisterCar));
        addcar.perform(click());
        Thread.sleep(1000);
        onView(withText("You have successfully Registered！")).check(matches(isDisplayed()) );
    }

    @Test
    public void addCarWrongTest() throws InterruptedException {
        appInitTestRule.getActivity();
        ViewInteraction inputUname = onView(withId(R.id.LoginUsername));
        inputUname.perform(replaceText("13530544395"), closeSoftKeyboard());
        ViewInteraction inputUpass = onView(allOf(withId(R.id.LoginPassword), isDisplayed()));
        inputUpass.perform(replaceText("abc"), closeSoftKeyboard());
        ViewInteraction loginButton = onView(withId(R.id.LoginBtn));
        loginButton.perform(click());
        Thread.sleep(3000);
        ViewInteraction usermenu = onView(withId(R.id.NavigationUser));
        usermenu.perform(click());
        Thread.sleep(1000);
        ViewInteraction qr = onView(withText("My Car"));
        qr.perform(click());
        Thread.sleep(1000);
        onView(withId(R.id.addCarBtn)).perform(click());
        int flag = new Random().nextInt(99999);
        if (flag < 10000)
        {
            flag += 10000;
        }
        Thread.sleep(1000);
        ViewInteraction inputPlate = onView(withId(R.id.CarPlate));
        inputPlate.perform(replaceText("1X" + String.valueOf(flag)), closeSoftKeyboard());

        ViewInteraction addcar = onView(withId(R.id.RegisterCar));
        addcar.perform(click());
        Thread.sleep(1000);
        onView(withText("车牌号不符合规范")).check(matches(isDisplayed()) );
    }
}
