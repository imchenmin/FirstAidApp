package cse.SUSTC.ParkingApp;

import androidx.test.espresso.ViewInteraction;
import androidx.test.rule.ActivityTestRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

//import android.widget.Button;
//import android.widget.TextView;
@RunWith(AndroidJUnit4.class)
public class FeedbackTEST {
    @Rule
    public transient ActivityTestRule<MainStage> appInitTestRule = new ActivityTestRule<>(MainStage.class);

    @Test
    public void feedbackTest() throws InterruptedException {
        ViewInteraction usermenu = onView(withId(R.id.NavigationUser));
        usermenu.perform(click());
        Thread.sleep(1000);
        ViewInteraction feeback = onView(withText("Feedback or Appeal"));
        feeback.perform(click());

    }
}
