package no.hvl.quizappvol2.Fragments;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import no.hvl.quizappvol2.R;

// Fragment that displays a single quiz image
public class QuizImageFragment extends Fragment {

    private static final String IMAGE_PATH_KEY = "image_path"; // Key used for passing the image path in a Bundle
    private ImageView quizImage; // ImageView to display the image

    // Factory method to create a new instance with an image path
    public static QuizImageFragment newInstance(String imagePath) {
        Log.d("QuizImageFragment", "Received image path: " + imagePath); // Log the received path (debugging)

        QuizImageFragment fragment = new QuizImageFragment(); // Create a new fragment instance
        Bundle args = new Bundle(); // Create a new Bundle to store arguments
        args.putString("imagePath", imagePath); // Add the image path to the bundle
        fragment.setArguments(args); // Attach arguments to the fragment
        return fragment;
    }

    // Called to create and return the fragment's UI
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_quiz_image, container, false);
        quizImage = view.findViewById(R.id.quizImage); // Get the ImageView from the layout

        // Retrieve and use the image path from the arguments
        if (getArguments() != null) {
            String imagePath = getArguments().getString("imagePath"); // Get the path
            Log.d("QuizImageFragment", "Displaying image: " + imagePath); // Log the path

            if (imagePath != null) {
                Uri imageUri = Uri.parse(imagePath); // Convert the string to a URI
                Glide.with(requireContext()) // Use Glide to load image into ImageView
                        .load(imageUri)
                        .into(quizImage);
            } else {
                Log.e("QuizImageFragment", "Image path is NULL"); // Log error if path is missing
            }
        }

        return view; // Return the view for this fragment
    }

}
