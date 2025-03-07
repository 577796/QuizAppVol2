package no.hvl.quizappvol2;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import no.hvl.quizappvol2.Activity.GalleryActivity;

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

        // Extract the number from the TextView
        GalleryActivity activity = activityRule.getActivity();
        String countText = activity.textView.getText().toString();
        extractedImageCount = extractNumber(countText);
    }

    @Test
    public void testExtractedImageCount() {
        // Convert expected text
        String expectedText = "Total images: " + extractedImageCount;

        // Check if the TextView correctly displays the extracted number
        onView(withId(R.id.photos)).check(matches(withText(expectedText)));
    }

    // Helper method to extract number from "Total images: X"
    private int extractNumber(String text) {
        return Integer.parseInt(text.replaceAll("[^0-9]", ""));
    }
}
