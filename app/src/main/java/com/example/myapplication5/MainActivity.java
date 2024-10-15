package com.example.myapplication5;


import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    private TextView questionTextView;
    private RadioGroup optionsRadioGroup;
    private Button submitButton;
    private Button hintButton; // Кнопка для подсказки
    private Spinner difficultySpinner;

    private DatabaseReference myRef;
    private int score = 0;
    private Question currentQuestion;
    private List<Question> questionList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Инициализация Firebase
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        myRef = database.getReference("users");

        questionTextView = findViewById(R.id.questionTextView);
        optionsRadioGroup = findViewById(R.id.optionsRadioGroup);
        submitButton = findViewById(R.id.submitButton);
        hintButton = findViewById(R.id.hintButton);
        difficultySpinner = findViewById(R.id.difficultySpinner);

        // Заполнение списка вопросов
        populateQuestions();

        // Настройка уровня сложности
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.difficulties, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        difficultySpinner.setAdapter(adapter);
        difficultySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                loadRandomQuestion();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkAnswer();
            }
        });

        // Обработка нажатия на кнопку подсказки
        hintButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                giveHint();
            }
        });
    }

    private void populateQuestions() {
        questionList = new ArrayList<>();
        questionList.add(new Question("Франция", new String[]{"Париж", "Берлин", "Лондон", "Мадрид"}, "Париж"));
        questionList.add(new Question("Испания", new String[]{"Мадрид", "Барселона", "Севилья", "Валенсия"}, "Мадрид"));
        questionList.add(new Question("Германия", new String[]{"Берлин", "Гамбург", "Мюнхен", "Фrankfurt"}, "Берлин"));
        questionList.add(new Question("Италия", new String[]{"Рим", "Венеция", "Флоренция", "Милан"}, "Рим"));
        // Добавьте больше вопросов по необходимости
    }

    private void loadRandomQuestion() {
        Random random = new Random();
        int index = random.nextInt(questionList.size());
        currentQuestion = questionList.get(index);
        questionTextView.setText("Какой город является столицей " + currentQuestion.getCountry() + "?");
        setupOptions();
    }

    private void setupOptions() {
        optionsRadioGroup.clearCheck();
        String[] options = currentQuestion.getOptions();
        for (int i = 0; i < options.length; i++) {
            RadioButton radioButton = (RadioButton) optionsRadioGroup.getChildAt(i);
            radioButton.setText(options[i]);
            radioButton.setVisibility(View.VISIBLE); // Убедитесь, что все варианты видимы
        }
    }

    private void checkAnswer() {
        int selectedId = optionsRadioGroup.getCheckedRadioButtonId();
        if (selectedId != -1) {
            RadioButton selectedRadioButton = findViewById(selectedId);
            String selectedAnswer = selectedRadioButton.getText().toString();

            if (selectedAnswer.equals(currentQuestion.getCorrectAnswer())) {
                score += 10; // Добавляем очки за правильный ответ
                Toast.makeText(MainActivity.this, "Правильный ответ!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(MainActivity.this, "Неправильный ответ! Правильный ответ: " + currentQuestion.getCorrectAnswer(), Toast.LENGTH_LONG).show();
            }

            // Сохранение результата пользователя
            saveUserScore("user1", "John", score); // Здесь можно изменить идентификатор пользователя
            loadRandomQuestion(); // Загружаем новый вопрос
        } else {
            Toast.makeText(MainActivity.this, "Пожалуйста, выберите ответ!", Toast.LENGTH_SHORT).show();
        }
    }

    private void giveHint() {
        // Исключаем один неправильный вариант
        String[] options = currentQuestion.getOptions();
        Random random = new Random();
        int incorrectOptionIndex;

        do {
            incorrectOptionIndex = random.nextInt(options.length);
        } while (options[incorrectOptionIndex].equals(currentQuestion.getCorrectAnswer()));

        RadioButton incorrectRadioButton = (RadioButton) optionsRadioGroup.getChildAt(incorrectOptionIndex);
        incorrectRadioButton.setVisibility(View.GONE); // Убираем неправильный вариант
        Toast.makeText(this, "Подсказка: Один неправильный ответ исключен!", Toast.LENGTH_SHORT).show();
    }

    // Метод для сохранения результата пользователя в Firebase
    public void saveUserScore(String userId, String username, int score) {
        User user = new User(username, score);
        myRef.child(userId).setValue(user);
    }
}

