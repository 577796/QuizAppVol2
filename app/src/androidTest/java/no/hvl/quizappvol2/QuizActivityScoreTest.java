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
        // Ensure the score view is displayed
        onView(withId(R.id.score)).check(matches(isDisplayed()));

        // Ensure score is reset before each test
        activityRule.getScenario().onActivity(activity -> {
            QuizButtonsFragment.resetScore();
        });

        // Extract initial score value
        extractedScore = extractNumber(getTextFromView(R.id.score));

        // Get correct answer from activity (on UI thread)
        activityRule.getScenario().onActivity(activity -> {
            correctAnswer = activity.getCorrectAnswerForDisplayedImage();
        });
    }


    @Test
    public void testScoreIncreaseOnCorrect(){

        // Click the correct answer button
        onView(withText(correctAnswer)).perform(click());

        // Verify that score has increased by 1
        String expectedText = "Score: " + (extractedScore + 1);
        onView(withId(R.id.score)).check(matches(withText(expectedText)));
    }

    @Test
    public void testScoreDoNotIncreaseOnWrong() {
        // Extract the initial score

        int initialScore = extractNumber(getTextFromView(R.id.score));

        // Retrieve the correct answer from QuizActivity
        final String[] correctAnswer = new String[1];
        activityRule.getScenario().onActivity(activity -> {
            correctAnswer[0] = activity.getCorrectAnswerForDisplayedImage();
        });

        // Click any incorrect answer button
        onView(allOf(withId(R.id.answer1), not(withText(correctAnswer[0]))))
                .perform(click());

        // Verify the score remains unchanged
        String expectedText = "Score: " + initialScore;
        onView(withId(R.id.score)).check(matches(withText(expectedText)));
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


