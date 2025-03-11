package no.hvl.quizappvol2;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.intent.Intents.intending;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasAction;
import static androidx.test.espresso.matcher.RootMatchers.isDialog;
import static androidx.test.espresso.matcher.ViewMatchers.isAssignableFrom;
import static androidx.test.espresso.matcher.ViewMatchers.withHint;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.test.espresso.UiController;
import androidx.test.espresso.ViewAction;
import androidx.test.espresso.intent.Intents;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;

import org.hamcrest.Matcher;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import no.hvl.quizappvol2.Activity.GalleryActivity;

@RunWith(AndroidJUnit4.class)
public class GalleryImageCountTest {

    @Rule
    public ActivityScenarioRule<GalleryActivity> activityRule =
            new ActivityScenarioRule<>(GalleryActivity.class);

    private int extractedImageCount;

    @Before
    public void setUp() {
        Intents.init(); // ✅ Initialize Espresso Intents

        // Wait for the UI to load and ensure the TextView is displayed
        onView(withId(R.id.photos)).check(matches(isDisplayed()));

        // Extract the number from the TextView dynamically
        extractedImageCount = extractNumber(getTextFromView(R.id.photos));
    }

    @After
    public void tearDown() {
        Intents.release(); // ✅ Release Espresso Intents after test
    }

    @Test
    public void testExtractedImageCount() {
        // Convert expected text
        String expectedText = "Total Images: " + extractedImageCount;

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
        onView(withId(R.id.recyclerView)).
                perform(actionOnItemAtPosition(0, clickChildViewWithId(R.id.btnDelete)));

        waitUntilViewTextChanges(R.id.photos, "Total Images: " + expectedCountAfterDelete);
        
        // Verify the image count has decreased
        onView(withId(R.id.photos))
                .check(matches(withText("Total Images: " + expectedCountAfterDelete)));
    }

    // ✅ Helper method to wait for UI updates
    private void waitUntilViewTextChanges(int viewId, String expectedText) {
        for (int i = 0; i < 10; i++) { // Polling max 10 times
            try {
                onView(withId(viewId)).check(matches(withText(expectedText)));
                return; // Success, exit loop
            } catch (AssertionError e) {
                try {
                    Thread.sleep(500); // Wait 500ms and retry
                } catch (InterruptedException ignored) {}
            }
        }
        throw new AssertionError("TextView did not update to expected value: " + expectedText);
    }

    @Test
    public void testAddImageToGallery() {

        int expectedCountAfterAdd = extractedImageCount + 1;

        // Stub intent to simulate selecting an image from gallery
        Intent resultData = new Intent();
        Uri fakeImageUri = Uri.parse("file:///android_asset/images/gulbil.jpg"); // Fake image path
        resultData.setData(fakeImageUri);
        Instrumentation.ActivityResult result =
                new Instrumentation.ActivityResult(Activity.RESULT_OK, resultData);

        // Stub response for gallery selection
        intending(hasAction(Intent.ACTION_GET_CONTENT)).respondWith(result);
        intending(hasAction(Intent.ACTION_OPEN_DOCUMENT)).respondWith(result);

        // Click the "Add Photo" button
        onView(withId(R.id.addPhoto)).perform(click());

        // Wait for the description dialog
        onView(withText("Add Description")) // Ensure the title matches
                .inRoot(isDialog()) // Confirms it's inside a dialog
                .check(matches(isDisplayed()));

        // Type "Gul bil" into the first EditText in the dialog
        onView(withHint("Enter description")) // Looks for the first input field
                .inRoot(isDialog())
                .perform(typeText("Gul bil"), closeSoftKeyboard());

        // Click the "Save" button inside the dialog
        onView(withText("Save"))
                .inRoot(isDialog())
                .perform(click());

        // Verify that the image count increased
        onView(withId(R.id.photos)).check(matches(withText("Total Images: " + expectedCountAfterAdd)));
    }



    public static ViewAction clickChildViewWithId(final int id) {
        return new ViewAction() {
            @Override
            public Matcher<View> getConstraints() {
                return isAssignableFrom(ViewGroup.class);
            }

            @Override
            public String getDescription() {
                return "Click on a child view with specified ID.";
            }

            @Override
            public void perform(UiController uiController, View view) {
                View childView = view.findViewById(id);
                if (childView != null) {
                    childView.performClick();
                }
            }
        };
    }

    // Helper method to extract number from TextView
    private int extractNumber(String text) {
        return Integer.parseInt(text.replaceAll("[^0-9]", ""));
    }

    // Helper method to get text dynamically from a View
    private String getTextFromView(int viewId) {
        String[] extractedText = new String[1];
        onView(withId(R.id.photos))
                .check(matches(isDisplayed()))
                .check((view, noViewFoundException) ->
                        extractedText[0] = ((TextView) view).getText().toString());
        return extractedText[0];
    }
}
