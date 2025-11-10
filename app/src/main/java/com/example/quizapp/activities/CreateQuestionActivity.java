package com.example.quizapp.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.quizapp.R;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CreateQuestionActivity extends AppCompatActivity {

    // Views
    private Spinner spType;
    private TextInputEditText etQuestion, etA, etB, etC, etD;
    private LinearLayout layoutOptionC, layoutOptionD;
    private RadioGroup rgCorrectMCQ, rgCorrectTF;
    private RadioButton rbCorrectA, rbCorrectB, rbCorrectC, rbCorrectD;
    private RadioButton rbTrue, rbFalse;
    private Button btnSave, btnCancel;

    // Data
    private FirebaseFirestore db;
    private String selectedType = "MCQ"; // Default

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_question);

        initViews();
        setupSpinner();
        setupListeners();
    }

    private void initViews() {
        spType = findViewById(R.id.spType);
        etQuestion = findViewById(R.id.etQuestion);
        etA = findViewById(R.id.etA);
        etB = findViewById(R.id.etB);
        etC = findViewById(R.id.etC);
        etD = findViewById(R.id.etD);

        layoutOptionC = findViewById(R.id.layoutOptionC);
        layoutOptionD = findViewById(R.id.layoutOptionD);

        // RadioGroups
        rgCorrectMCQ = findViewById(R.id.rgCorrectMCQ);
        rgCorrectTF = findViewById(R.id.rgCorrectTF);

        // RadioButtons for MCQ
        rbCorrectA = findViewById(R.id.rbCorrectA);
        rbCorrectB = findViewById(R.id.rbCorrectB);
        rbCorrectC = findViewById(R.id.rbCorrectC);
        rbCorrectD = findViewById(R.id.rbCorrectD);

        // RadioButtons for True/False
        rbTrue = findViewById(R.id.rbTrue);
        rbFalse = findViewById(R.id.rbFalse);

        btnSave = findViewById(R.id.btnSave);
        btnCancel = findViewById(R.id.btnCancel);

        db = FirebaseFirestore.getInstance();
    }

    private void setupSpinner() {
        String[] types = {"Multiple Choice (MCQ)", "True/False (TF)"};

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                types
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spType.setAdapter(adapter);

        spType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    selectedType = "MCQ";
                    showMultipleChoiceOptions();
                } else {
                    selectedType = "TF";
                    showTrueFalseOptions();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                selectedType = "MCQ";
            }
        });
    }

    private void showMultipleChoiceOptions() {
        // Show all 4 options
        layoutOptionC.setVisibility(View.VISIBLE);
        layoutOptionD.setVisibility(View.VISIBLE);

        // Enable editing
        etA.setEnabled(true);
        etB.setEnabled(true);

        // Clear any preset values
        if ("True".equals(etA.getText().toString()) || "False".equals(etB.getText().toString())) {
            etA.setText("");
            etB.setText("");
        }

        // Show MCQ RadioGroup, hide TF RadioGroup
        rgCorrectMCQ.setVisibility(View.VISIBLE);
        rgCorrectTF.setVisibility(View.GONE);

        // Clear selection
        rgCorrectMCQ.clearCheck();
    }

    private void showTrueFalseOptions() {
        // Hide options C and D
        layoutOptionC.setVisibility(View.GONE);
        layoutOptionD.setVisibility(View.GONE);

        // Set True/False as options
        etA.setText("True");
        etB.setText("False");

        // Make them read-only
        etA.setEnabled(false);
        etB.setEnabled(false);

        // Show TF RadioGroup, hide MCQ RadioGroup
        rgCorrectMCQ.setVisibility(View.GONE);
        rgCorrectTF.setVisibility(View.VISIBLE);

        // Clear selection
        rgCorrectTF.clearCheck();
    }

    private void setupListeners() {
        btnSave.setOnClickListener(v -> saveQuestionToFirestore());
        btnCancel.setOnClickListener(v -> confirmCancel());

        // Update RadioButton text when options change
        etA.addTextChangedListener(new android.text.TextWatcher() {
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            public void afterTextChanged(android.text.Editable s) {
                rbCorrectA.setText("A - " + (s.toString().isEmpty() ? "Correct Answer" : s.toString()));
            }
        });

        etB.addTextChangedListener(new android.text.TextWatcher() {
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            public void afterTextChanged(android.text.Editable s) {
                rbCorrectB.setText("B - " + (s.toString().isEmpty() ? "Correct Answer" : s.toString()));
            }
        });

        etC.addTextChangedListener(new android.text.TextWatcher() {
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            public void afterTextChanged(android.text.Editable s) {
                rbCorrectC.setText("C - " + (s.toString().isEmpty() ? "Correct Answer" : s.toString()));
            }
        });

        etD.addTextChangedListener(new android.text.TextWatcher() {
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            public void afterTextChanged(android.text.Editable s) {
                rbCorrectD.setText("D - " + (s.toString().isEmpty() ? "Correct Answer" : s.toString()));
            }
        });
    }

    private void saveQuestionToFirestore() {
        String questionText = etQuestion.getText().toString().trim();

        // Validate question
        if (questionText.isEmpty()) {
            etQuestion.setError("Please enter a question");
            etQuestion.requestFocus();
            Toast.makeText(this, "Please enter a question", Toast.LENGTH_SHORT).show();
            return;
        }

        // Create question map
        Map<String, Object> questionMap = new HashMap<>();
        questionMap.put("type", selectedType);
        questionMap.put("question", questionText);

        if ("MCQ".equals(selectedType)) {
            // Multiple Choice Question
            String optA = etA.getText().toString().trim();
            String optB = etB.getText().toString().trim();
            String optC = etC.getText().toString().trim();
            String optD = etD.getText().toString().trim();

            // Validate options
            if (optA.isEmpty() || optB.isEmpty() || optC.isEmpty() || optD.isEmpty()) {
                Toast.makeText(this, "Please fill all options (A, B, C, D)", Toast.LENGTH_SHORT).show();
                return;
            }

            List<String> options = new ArrayList<>();
            options.add(optA);
            options.add(optB);
            options.add(optC);
            options.add(optD);

            questionMap.put("options", options);

            // Get selected correct answer from RadioGroup
            int selectedId = rgCorrectMCQ.getCheckedRadioButtonId();
            if (selectedId == -1) {
                Toast.makeText(this, "Please select the correct answer", Toast.LENGTH_SHORT).show();
                return;
            }

            int correctIndex;
            if (selectedId == R.id.rbCorrectA) {
                correctIndex = 0;
            } else if (selectedId == R.id.rbCorrectB) {
                correctIndex = 1;
            } else if (selectedId == R.id.rbCorrectC) {
                correctIndex = 2;
            } else if (selectedId == R.id.rbCorrectD) {
                correctIndex = 3;
            } else {
                correctIndex = 0;
            }

            questionMap.put("correctIndex", correctIndex);
            questionMap.put("correctTrue", null);

        } else {
            // True/False Question
            int selectedId = rgCorrectTF.getCheckedRadioButtonId();
            if (selectedId == -1) {
                Toast.makeText(this, "Please select True or False", Toast.LENGTH_SHORT).show();
                return;
            }

            boolean isCorrectTrue = (selectedId == R.id.rbTrue);

            questionMap.put("correctTrue", isCorrectTrue);
            questionMap.put("options", null);
            questionMap.put("correctIndex", null);
        }

        // Show loading state
        btnSave.setEnabled(false);
        btnSave.setText("Saving...");

        // Save to Firestore
        db.collection("questions")
                .add(questionMap)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(this, "✅ Question saved successfully!", Toast.LENGTH_SHORT).show();

                    // Ask if user wants to add another question
                    new AlertDialog.Builder(this)
                            .setTitle("Success!")
                            .setMessage("Question saved. Do you want to add another question?")
                            .setPositiveButton("Add Another", (dialog, which) -> {
                                clearForm();
                                btnSave.setEnabled(true);
                                btnSave.setText("Save Question");
                            })
                            .setNegativeButton("Go Back", (dialog, which) -> finish())
                            .setCancelable(false)
                            .show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "❌ Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    btnSave.setEnabled(true);
                    btnSave.setText("Save Question");
                });
    }

    private void clearForm() {
        etQuestion.setText("");
        etA.setText("");
        etB.setText("");
        etC.setText("");
        etD.setText("");

        // Clear radio selections
        rgCorrectMCQ.clearCheck();
        rgCorrectTF.clearCheck();

        // Reset to MCQ
        spType.setSelection(0);

        // Re-enable fields
        etA.setEnabled(true);
        etB.setEnabled(true);

        // Focus on question field
        etQuestion.requestFocus();
    }

    private void confirmCancel() {
        String questionText = etQuestion.getText().toString().trim();

        if (!questionText.isEmpty()) {
            new AlertDialog.Builder(this)
                    .setTitle("Discard Changes?")
                    .setMessage("You have unsaved changes. Are you sure you want to leave?")
                    .setPositiveButton("Discard", (dialog, which) -> finish())
                    .setNegativeButton("Cancel", null)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
        } else {
            finish();
        }
    }

    @Override
    public void onBackPressed() {
        confirmCancel();
    }
}