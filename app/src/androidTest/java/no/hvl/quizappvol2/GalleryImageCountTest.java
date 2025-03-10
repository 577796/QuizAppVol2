package no.hvl.quizappvol2;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import no.hvl.quizappvol2.Activity.GalleryActivity;
import no.hvl.quizappvol2.R;

@RunWith(AndroidJUnit4.class)
public class GalleryImageCountTest {

    @Rule
    public ActivityTestRule<GalleryActivity> activityRule =
            new ActivityTestRule<>(GalleryActivity.class);

    private int extractedImageCount;

    @Before
    public void setUp() {
        // Wait for the UI to load and ensure the TextView is displayed
        onView(withId(R.id.photos)).check(matches(isDisplayed()));

        // Extract the number from the TextView dynamically
        extractedImageCount = extractNumber(getTextFromView(R.id.photos));
    }

    @Test
    public void testExtractedImageCount() {
        // Convert expected text
        String expectedText = "Total images: " + extractedImageCount;

        // Check if the TextView correctly displays the extracted number
        onView(withId(R.id.photos)).check(matches(withText(expectedText)));
    }

    @Test
    public void testDeleteFirstImage() {
        if (extractedImageCount == 0) {
            throw new AssertionError("No images available for deletion.");
        }

        int expectedCountAfterDelete = extractedImageCount - 1;

        // Click the first delete button in the RecyclerView
        onView(withId(R.id.recyclerView))
                .perform(actionOnItemAtPosition(0, click()));

        // Verify the image count has decreased
        onView(withId(R.id.photos))
                .check(matches(withText("Total images: " + expectedCountAfterDelete)));
    }

    // ✅ Helper method to extract number from TextView
    private int extractNumber(String text) {
        return Integer.parseInt(text.replaceAll("[^0-9]", ""));
    }

    // ✅ Helper method to get text dynamically from a View
    private String getTextFromView(int viewId) {
        final String[] textHolder = new String[1];
        onView(withId(viewId)).check(matches(isDisplayed()));
        onView(withId(viewId)).check((view, noViewFoundException) -> {
            if (view instanceof android.widget.TextView) {
                textHolder[0] = ((android.widget.TextView) view).getText().toString();
            } else {
                throw new AssertionError("View is not a TextView");
            }
        });
        return textHolder[0];
    }
}
