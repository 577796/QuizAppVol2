package no.hvl.quizappvol2;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;

import androidx.test.espresso.intent.Intents;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.After;
import org.junit.runner.RunWith;

import no.hvl.quizappvol2.Activity.GalleryActivity;
import no.hvl.quizappvol2.Activity.MainActivity;
import no.hvl.quizappvol2.Activity.QuizActivity;


@RunWith(AndroidJUnit4.class)
public class MainActivityTest {

    @Rule
    public ActivityScenarioRule<MainActivity> activityScenarioRule =
            new ActivityScenarioRule<>(MainActivity.class);

    @Before
    public void setup() {
        // Initialize Espresso Intents
        Intents.init();
    }

    @After
    public void tearDown() {
        // Release Espresso Intents
        Intents.release();
    }

    @Test
    public void testNavigationToGalleryActivity() {
        // Click the button that starts the Gallery
        onView(withId(R.id.galleryButton)).perform(click());

        // Check that the Gallery is displayed
        intended(hasComponent(GalleryActivity.class.getName()));
    }
    @Test
    public void testNavigationToQuizActivity() throws InterruptedException {

        // Click the button that starts the Quiz
        onView(withId(R.id.quizButton)).perform(click());

        // Check that the QuizActivity is displayed
        intended(hasComponent(QuizActivity.class.getName()));
    }
}

