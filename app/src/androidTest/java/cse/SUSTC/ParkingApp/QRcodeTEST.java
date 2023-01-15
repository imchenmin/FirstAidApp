package cse.SUSTC.ParkingApp;

import androidx.test.espresso.ViewInteraction;
import androidx.test.rule.ActivityTestRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

//import android.widget.Button;
//import android.widget.TextView;
@RunWith(AndroidJUnit4.class)
public class QRcodeTEST {
    @Rule
    public transient ActivityTestRule<MainStage> appInitTestRule = new ActivityTestRule<>(MainStage.class);

    @Test
    public void makeTest() throws InterruptedException {
        ViewInteraction usermenu = onView(withId(R.id.NavigationUser));
        usermenu.perform(click());
        Thread.sleep(1000);
        ViewInteraction qr = onView(withText("Scan QR Code"));
        qr.perform(click());
        Thread.sleep(1000);
        onView(withText("将取景框对准二维码/条码即可扫描")).check(matches(isDisplayed()) );
    }

}
