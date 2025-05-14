// ✅ Cleaned QuizViewModel.java
package no.hvl.quizappvol2.ViewModel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import no.hvl.quizappvol2.ImageDatabase;
import no.hvl.quizappvol2.ImageItem;

public class QuizViewModel extends AndroidViewModel {

    private final ExecutorService executorService = Executors.newSingleThreadExecutor();
    private List<ImageItem> quizItems = new ArrayList<>();
    private int currentQuestion = 0;

    private final MutableLiveData<Boolean> dataLoaded = new MutableLiveData<>(false);
    private final MutableLiveData<Integer> score = new MutableLiveData<>(0);

    public QuizViewModel(@NonNull Application application) {
        super(application);
        loadImages();
    }

    private void loadImages() {
        executorService.execute(() -> {
            List<ImageItem> items = ImageDatabase.getInstance(getApplication()).imageItemDAO().getAllImagesSync();
            if (items != null) {
                Collections.shuffle(items);
                quizItems = items;
            }
            dataLoaded.postValue(true);
        });
    }

    public LiveData<Boolean> getDataLoaded() {
        return dataLoaded;
    }

    public LiveData<Integer> getScore() {
        return score;
    }

    public ImageItem getCurrentItem() {
        if (quizItems.isEmpty() || currentQuestion >= quizItems.size()) return null;
        return quizItems.get(currentQuestion);
    }

    public List<String> generateOptions() {
        if (quizItems.isEmpty()) return new ArrayList<>();

        Set<String> unique = new HashSet<>();
        List<String> options = new ArrayList<>();

        String correct = quizItems.get(currentQuestion).getDescription();
        unique.add(correct);
        options.add(correct);

        List<ImageItem> shuffled = new ArrayList<>(quizItems);
        Collections.shuffle(shuffled);

        for (ImageItem item : shuffled) {
            if (unique.size() >= 3) break;
            if (unique.add(item.getDescription())) {
                options.add(item.getDescription());
            }
        }

        Collections.shuffle(options);
        return options;
    }

    public boolean checkAnswer(String selectedAnswer) {
        String correct = quizItems.get(currentQuestion).getDescription();
        boolean isCorrect = correct.equals(selectedAnswer);
        if (isCorrect) {
            Integer current = score.getValue();
            score.setValue((current != null ? current : 0) + 1);
        }
        return isCorrect;
    }

    public void advanceQuestion() {
        currentQuestion++;
    }
}
