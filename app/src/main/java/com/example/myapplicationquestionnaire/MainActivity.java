package com.example.myapplicationquestionnaire;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {
    EditText editName, editAge;
    TextView tvSalaryValue, tvResult;
    SeekBar seekSalary;
    RadioGroup rg1, rg2, rg3, rg4, rg5;
    CheckBox chkExperience, chkTeamwork, chkTravel;
    Button btnSubmit;
    static final int SALARY_MIN = 800;
    static final int SALARY_MAX = 3000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        editName = findViewById(R.id.editName);
        editAge = findViewById(R.id.editAge);
        tvSalaryValue = findViewById(R.id.tvSalaryValue);
        tvResult = findViewById(R.id.tvResult);
        seekSalary = findViewById(R.id.seekSalary);

        rg1 = findViewById(R.id.rg1);
        rg2 = findViewById(R.id.rg2);
        rg3 = findViewById(R.id.rg3);
        rg4 = findViewById(R.id.rg4);
        rg5 = findViewById(R.id.rg5);

        chkExperience = findViewById(R.id.chkExperience);
        chkTeamwork = findViewById(R.id.chkTeamwork);
        chkTravel = findViewById(R.id.chkTravel);
        btnSubmit = findViewById(R.id.btnSubmit);

        updateSalaryLabel(seekSalary.getProgress());//значення зарплати

        seekSalary.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                updateSalaryLabel(progress);
            }
            @Override public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override public void onStopTrackingTouch(SeekBar seekBar) {}
        });


        TextWatcher watcher = new TextWatcher() {//TextWatcher активуємо кнопку коли піб і вік заповнені
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                checkFormReady();
            }
            @Override public void afterTextChanged(Editable s) {}
        };

        editName.addTextChangedListener(watcher);
        editAge.addTextChangedListener(watcher);

        btnSubmit.setOnClickListener(view -> onSubmitClicked());
    }

    @SuppressLint("SetTextI18n")
    private void updateSalaryLabel(int progress) {
        int salary = SALARY_MIN + (SALARY_MAX - SALARY_MIN) * progress / 100;
        tvSalaryValue.setText(salary + "$");
    }

    private int getCurrentSalary() {
        return SALARY_MIN + (SALARY_MAX - SALARY_MIN) * seekSalary.getProgress() / 100;
    }


    private void checkFormReady() {//кнопка стає активною лише якщо є мінімум 2 слова в ПІБ і не порожній вік
        String name = editName.getText().toString().trim();
        String ageStr = editAge.getText().toString().trim();

        boolean nameOk = name.split("\\s+").length >= 3;
        boolean ageOk = !ageStr.isEmpty();

        btnSubmit.setEnabled(nameOk && ageOk);
    }

    @SuppressLint("SetTextI18n")
    private void onSubmitClicked() {
        String name = editName.getText().toString().trim();
        String ageStr = editAge.getText().toString().trim();
        int salary = getCurrentSalary();

        //3 слова в піб
        if (name.split("\\s+").length < 3) {
            showResult(false, "ПІБ має містити щонайменше 3 слова");
            return;
        }

        //вік
        int age;
        try {
            age = Integer.parseInt(ageStr);
        } catch (NumberFormatException e) {
            showResult(false, "Некоректний вік");
            return;
        }

        if (age < 21 || age > 40) {
            showResult(false, "Вік кандидата має бути від 21 до 40 років\nВаш вік: " + age);
            return;
        }

        //зарплата
        if (salary > 2000) {
            showResult(false, "Бажана зарплата перевищує бюджет компанії (макс. 2000$)\nВаша зарплата: " + salary + "$");
            return;
        }

        int score = 0;

        //8
        if (rg1.getCheckedRadioButtonId() == R.id.q1a3) score += 2;
        //private
        if (rg2.getCheckedRadioButtonId() == R.id.q2a3) score += 2;
        //main()
        if (rg3.getCheckedRadioButtonId() == R.id.q3a2) score += 2;
        //ООП
        if (rg4.getCheckedRadioButtonId() == R.id.q4a1) score += 2;
        //Stack
        if (rg5.getCheckedRadioButtonId() == R.id.q5a2) score += 2;

        if (chkExperience.isChecked()) score += 2;
        if (chkTeamwork.isChecked()) score += 1;
        if (chkTravel.isChecked()) score += 1;

        if (score >= 10) {
            showResult(true,
                    "Вітаємо, " + name.split("\\s+")[1] + "!\n" +
                            "Ви успішно пройшли тест\nНабрано балів: " + score + " / 14\n\n" +
                            "Контакти компанії TechUA:\n" +
                            "hr@techua.com\n" +
                            "+38(044) 123-45-67\n" +
                            "www.techua.com");
        } else {
            showResult(false,
                    "На жаль, Ви не пройшли тест.\n" +
                            "Набрано балів: " + score + " / 14\n" +
                            "Мінімально необхідно: 10 балів.\n\n");
        }
    }

    private void showResult(boolean success, String message) {
        tvResult.setText(message);

        //колір фону результату
        if (success) {
            tvResult.setBackgroundColor(Color.parseColor("#E8F5E9"));
            tvResult.setTextColor(Color.parseColor("#1B5E20"));
        } else {
            tvResult.setBackgroundColor(Color.parseColor("#FFEBEE"));
            tvResult.setTextColor(Color.parseColor("#B71C1C"));
        }

        tvResult.setVisibility(android.view.View.VISIBLE);

        //прокрутка вниз після появи результату
        ScrollView scrollView = (ScrollView) findViewById(R.id.main).getParent();
        scrollView.post(() -> scrollView.fullScroll(android.view.View.FOCUS_DOWN));
    }
}