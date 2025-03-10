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

public class QuizImageFragment extends Fragment {

    private static final String IMAGE_PATH_KEY = "image_path";
    private ImageView quizImage;

    public static QuizImageFragment newInstance(String imagePath) {
        Log.d("QuizImageFragment", "Received image path: " + imagePath);
        QuizImageFragment fragment = new QuizImageFragment();
        Bundle args = new Bundle();
        args.putString("imagePath", imagePath);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_quiz_image, container, false);
        quizImage = view.findViewById(R.id.quizImage);

        if (getArguments() != null) {
            String imagePath = getArguments().getString("imagePath");
            Log.d("QuizImageFragment", "Displaying image: " + imagePath);

            if (imagePath != null) {
                Uri imageUri = Uri.parse(imagePath);
                Glide.with(requireContext())
                        .load(imageUri)
                        .into(quizImage);
            } else {
                Log.e("QuizImageFragment", "Image path is NULL");
            }
        }

        return view;
    }

}

