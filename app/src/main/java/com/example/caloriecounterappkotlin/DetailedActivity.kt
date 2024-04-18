package com.example.caloriecounterappkotlin

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import com.google.android.material.textfield.TextInputEditText

class DetailedActivity : AppCompatActivity() {
    private lateinit var labelInput: TextInputEditText
    private lateinit var caloriesInput: TextInputEditText
    private lateinit var descriptionInput: TextInputEditText
    private lateinit var updateBtn: Button
    private lateinit var closeBtn: ImageButton
    private lateinit var dbHelper: DatabaseHelper
    private lateinit var food: Foods
    private lateinit var rootView: ConstraintLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detailed)

        // Initialize views
        labelInput = findViewById(R.id.labelInput)
        caloriesInput = findViewById(R.id.caloriesInput)
        descriptionInput = findViewById(R.id.descriptionInput)
        updateBtn = findViewById(R.id.updateBtn)
        closeBtn = findViewById(R.id.closeBtn)
        dbHelper = DatabaseHelper(this)
        rootView = findViewById(R.id.rootView)

        // Retrieve the food item passed from FoodAdapter
        food = intent.getSerializableExtra("food") as Foods

        // Populate the EditText fields with food details
        labelInput.setText(food.label)
        caloriesInput.setText(food.calories.toString())
        descriptionInput.setText(food.description)

        rootView.setOnClickListener {
            this.window.decorView.clearFocus()

            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(it.windowToken, 0)
        }

        updateBtn.setOnClickListener {
            val label = labelInput.text.toString()
            val calories = caloriesInput.text.toString().toDoubleOrNull()
            val description = descriptionInput.text.toString()

            if (label.isNotEmpty() && calories != null) {
                val updatedFood = Foods(food.id, label, calories, description)
                val updated = dbHelper.updateFood(updatedFood)

                if (updated) {
                    val intent = Intent()
                    intent.putExtra("foodUpdated", true)
                    setResult(Activity.RESULT_OK, intent)
                    finish()
                } else {
                    // Handle update failure if necessary
                }
            } else {
                // Handle invalid input if necessary
            }
        }

        closeBtn.setOnClickListener {
            finish()
        }
    }
}
