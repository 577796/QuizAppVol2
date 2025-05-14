package no.hvl.quizappvol2;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;

import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;


import static org.hamcrest.Matchers.not;
import static org.hamcrest.core.AllOf.allOf;

import android.widget.TextView;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import no.hvl.quizappvol2.Activity.QuizActivity;
import no.hvl.quizappvol2.Fragments.QuizButtonsFragment;

/**
* TEST HAS NOT BEEN UPDATED TO WORK WITH THE CURRENT VERSION
* */


@RunWith(AndroidJUnit4.class)
public class QuizActivityScoreTest {
    @Rule
    public ActivityScenarioRule<QuizActivity> activityRule =
            new ActivityScenarioRule<>(QuizActivity.class);

    //Variabel for å lagre score
    private int extractedScore;
    //Variabel for å lagre correkts score.
    private String correctAnswer;


    @Before
    public void setUp() throws InterruptedException {
    }


    @Test
    public void testScoreIncreaseOnCorrect(){


    }

    @Test
    public void testScoreDoNotIncreaseOnWrong() {
        // Extract the initial score


    }


    // ✅ Helper method to extract number from a TextView
    private int extractNumber(String text) {
        return Integer.parseInt(text.replaceAll("[^0-9]", ""));
    }

    // ✅ Helper method to get text from a view
    private String getTextFromView(int viewId) {
        final String[] textHolder = new String[1];
        onView(withId(viewId)).check(matches(isDisplayed()));
        onView(withId(viewId)).check((view, noViewFoundException) -> {
            if (view instanceof TextView) {
                textHolder[0] = ((TextView) view).getText().toString();
            } else {
                throw new AssertionError("View is not a TextView");
            }
        });
        return textHolder[0];
    }
}


