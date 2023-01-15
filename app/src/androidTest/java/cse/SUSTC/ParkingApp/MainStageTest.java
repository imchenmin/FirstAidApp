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
@RunWith(AndroidJUnit4.class)
public class MainStageTest {
    @Rule
    public transient ActivityTestRule<MainStage> appInitTestRule = new ActivityTestRule<>(MainStage.class);

    @Test
    public void mapTest() throws InterruptedException {
        appInitTestRule.getActivity();
        ViewInteraction mapmenu = onView(withId(R.id.NavigationMap));
        mapmenu.perform(click());
        Thread.sleep(3000);
        onView(withId(R.id.mapFrame)).check(matches(isDisplayed()) );
    }
    @Test
    public void orderTest() throws InterruptedException {
        ViewInteraction ordermenu = onView(withId(R.id.NavigationOrder));
        ordermenu.perform(click());
        Thread.sleep(3000);
        onView(withId(R.id.orderFrame)).check(matches(isDisplayed()) );
    }
    @Test
    public void userTest() throws InterruptedException {
        ViewInteraction usermenu = onView(withId(R.id.NavigationUser));
        usermenu.perform(click());
        Thread.sleep(3000);
        onView(withId(R.id.usrFrame)).check(matches(isDisplayed()));
    }
    @Test
    public void combineTest() throws InterruptedException {
        ViewInteraction usermenu = onView(withId(R.id.NavigationUser));
        usermenu.perform(click());
        Thread.sleep(3000);
        onView(withId(R.id.usrFrame)).check(matches(isDisplayed()));

        ViewInteraction ordermenu = onView(withId(R.id.NavigationOrder));
        ordermenu.perform(click());
        Thread.sleep(3000);
        onView(withId(R.id.orderFrame)).check(matches(isDisplayed()) );

        ViewInteraction mapmenu = onView(withId(R.id.NavigationMap));
        mapmenu.perform(click());
        Thread.sleep(3000);
        onView(withId(R.id.mapFrame)).check(matches(isDisplayed()) );
    }

}
