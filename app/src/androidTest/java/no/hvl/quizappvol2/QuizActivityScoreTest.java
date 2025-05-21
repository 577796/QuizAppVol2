
package no.hvl.quizappvol2;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import android.content.Context;

import androidx.lifecycle.ViewModelProvider;
import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import no.hvl.quizappvol2.Activity.QuizActivity;
import no.hvl.quizappvol2.ViewModel.QuizViewModel;

import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class QuizActivityScoreTest {

    @Test
    public void testCorrectAnswerIncreasesScore() throws InterruptedException {
        ActivityScenario<QuizActivity> scenario = ActivityScenario.launch(QuizActivity.class);

        Thread.sleep(1000);

        final String[] correctAnswer = new String[1];
        scenario.onActivity(activity -> {
            QuizViewModel viewModel = new ViewModelProvider(activity).get(QuizViewModel.class);
            correctAnswer[0] = viewModel.getCurrentItem().getDescription();
        });

        onView(withText(correctAnswer[0])).perform(click());
        onView(withId(R.id.next)).perform(click());

        Thread.sleep(1000);
        onView(withId(R.id.scoreDisplay)).check(matches(withText("Score: 1")));
    }

    @Test
    public void testWrongAnswerDoesNotIncreaseScore() throws InterruptedException {
        ActivityScenario<QuizActivity> scenario = ActivityScenario.launch(QuizActivity.class);

        Thread.sleep(1000);

        final String[] correctAnswer = new String[1];
        scenario.onActivity(activity -> {
            QuizViewModel viewModel = new ViewModelProvider(activity).get(QuizViewModel.class);
            correctAnswer[0] = viewModel.getCurrentItem().getDescription();
        });

        // Try clicking any wrong option that is not the correct one
        String[] allOptions = {"Gul Bil", "Rød Bil", "Hvit bil"};
        for (String option : allOptions) {
            if (!option.equals(correctAnswer[0])) {
                onView(withText(option)).perform(click());
                break;
            }
        }

        onView(withId(R.id.next)).perform(click());

        Thread.sleep(1000);
        onView(withId(R.id.scoreDisplay)).check(matches(withText("Score: 0")));
    }
}
