package no.hvl.quizappvol2.Fragments;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.io.File;

import no.hvl.quizappvol2.R;

public class QuizImageFragment extends Fragment {

    private static final String IMAGE_PATH_KEY = "image_path";
    private ImageView quizImage;

    public static QuizImageFragment newInstance(String imagePath) {
        QuizImageFragment fragment = new QuizImageFragment();
        Bundle args = new Bundle();
        args.putString(IMAGE_PATH_KEY, imagePath);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_quiz_image, container, false);
        quizImage = view.findViewById(R.id.quizImage);

        if (getArguments() != null) {
            String imagePath = getArguments().getString(IMAGE_PATH_KEY);
            File imgFile = new File(imagePath);
            if (imgFile.exists()) {
                Bitmap bitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                quizImage.setImageBitmap(bitmap);
            }
        }

        return view;
    }
}

