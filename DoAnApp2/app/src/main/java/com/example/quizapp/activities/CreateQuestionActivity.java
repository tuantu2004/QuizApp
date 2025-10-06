package com.example.quizapp.activities;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.quizapp.R;
import com.example.quizapp.data.models.Question;
import com.example.quizapp.utils.JsonUtils;

import java.util.ArrayList;
import java.util.List;

public class CreateQuestionActivity extends AppCompatActivity {
    Spinner spType;
    EditText etQuestion, etA, etB, etC, etD, etCorrect;
    Button btnSave;   // ✅ đổi tên đúng với XML

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_question);

        // Ánh xạ view
        spType = findViewById(R.id.spType);
        etQuestion = findViewById(R.id.etQuestion);
        etA = findViewById(R.id.etA);
        etB = findViewById(R.id.etB);
        etC = findViewById(R.id.etC);
        etD = findViewById(R.id.etD);
        etCorrect = findViewById(R.id.etCorrect);
        btnSave = findViewById(R.id.btnSave);  // ✅ đúng ID

        // Spinner chọn loại câu hỏi
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                new String[]{"MCQ", "TF"}
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spType.setAdapter(adapter);

        // Sự kiện khi nhấn nút lưu
        btnSave.setOnClickListener(view -> {
            String type = spType.getSelectedItem().toString();
            String qText = etQuestion.getText().toString().trim();

            if (qText.isEmpty()) {
                Toast.makeText(this, "Nhập nội dung câu hỏi", Toast.LENGTH_SHORT).show();
                return;
            }

            // Load danh sách câu hỏi từ JSON
            List<Question> list = JsonUtils.loadFromInternal(this);
            if (list == null) list = new ArrayList<>();

            // Tạo câu hỏi mới
            Question q = new Question();
            q.id = (int) (System.currentTimeMillis() / 1000);
            q.type = type;
            q.question = qText;

            if ("MCQ".equals(type)) {
                // Câu hỏi 4 lựa chọn
                List<String> opts = new ArrayList<>();
                opts.add(etA.getText().toString());
                opts.add(etB.getText().toString());
                opts.add(etC.getText().toString());
                opts.add(etD.getText().toString());
                q.options = opts;

                int idx;
                try {
                    idx = Integer.parseInt(etCorrect.getText().toString());
                } catch (Exception e) {
                    Toast.makeText(this, "Nhập correct index (0..3)", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (idx < 0 || idx > 3) {
                    Toast.makeText(this, "Correct index phải trong 0..3", Toast.LENGTH_SHORT).show();
                    return;
                }
                q.correctIndex = idx;
                q.correctTrue = null;

            } else {
                // Câu hỏi Đúng/Sai
                int v;
                try {
                    v = Integer.parseInt(etCorrect.getText().toString());
                } catch (Exception e) {
                    Toast.makeText(this, "Nhập 1 = true, 0 = false", Toast.LENGTH_SHORT).show();
                    return;
                }
                q.correctTrue = (v == 1);
                q.options = null;
                q.correctIndex = null;
            }

            // Lưu lại vào JSON
            list.add(q);
            boolean ok = JsonUtils.saveToInternal(this, list);

            if (ok) {
                Toast.makeText(this, "✅ Đã thêm câu hỏi", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(this, "❌ Lỗi lưu câu hỏi", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
