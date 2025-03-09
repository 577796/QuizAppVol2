# QuizAppVol2

### Test: Navigation to QuizActivity from MainActivity
**Description**  
This test verifies that when the user clicks the "Start Quiz" button in MainActivity, the application correctly navigates to QuizActivity. This ensures that the quiz feature is accessible from the main screen.

**Expected Result**  
- When the quizButton is clicked in MainActivity, the app should launch QuizActivity.
- The test should confirm that QuizActivity is the currently active activity.

**Test Implementation**  
- **Class**: MainActivityTest
- **Method**: testNavigationToQuizActivity()

**Test Steps**  
1. Launch MainActivity.
2. Click the button with ID `R.id.quizButton`.
3. Verify that QuizActivity is opened using Espresso Intents.

**Result**  
Test Passed
- The test correctly identifies that clicking `quizButton` navigates to `QuizActivity`.
- `intended(hasComponent(QuizActivity.class.getName()))` confirms the transition.

### Test: Verifying Image Count in GalleryActivity (Add & Delete)
**Description**  
This test verifies that:
- The `TextView` displaying the total number of images correctly updates after an image is added.
- The `TextView` correctly reflects the count after an image is deleted.
- The UI is updated accordingly.

**Expected Result**  
- The initial number of images is retrieved from the `TextView` (`Total images: X`).
- After adding an image, the number should increase by 1 (`Total images: X+1`).
- After deleting an image, the number should decrease by 1 (`Total images: X`).

**Test Implementation**  
- **Class**: GalleryImageCountTest
- **Method**: testAddAndDeleteImage()

**Test Steps**  
**Step 1**: Verify Initial Image Count
1. Launch GalleryActivity.
2. Ensure the `TextView` with ID `R.id.photos` is displayed.
3. Extract the number of images displayed.

**Step 2**: Add an Image
1. Stub an intent to simulate selecting an image.
2. Click the "Add Photo" button.
3. Verify that `TextView` updates with "Total images: X+1".

**Step 3**: Delete an Image
1. Click the "Delete" button on the first image.
2. Verify that `TextView` updates back to "Total images: X".

**Result**  
Test Failed
- App crashes when trying to add a new photo. It also fails to register a change in number of images.

### Summary
As of right now it´s a simpler version to see if it is able to count the number of photos. That works great.  
Next step is to modify the test to test what it is actually supposed to test.

**Score Update In QuizActivity**:
**Step 1**: Verify the score
**Step 2**: Answer a question correctly 
**Step 3**: Read the Score from ScoreView.

**Result**  
Test has not been implemented as I had issues getting the score to update.

    
