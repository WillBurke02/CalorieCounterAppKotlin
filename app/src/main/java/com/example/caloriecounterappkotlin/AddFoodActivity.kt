package com.example.caloriecounterappkotlin

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import com.google.android.material.textfield.TextInputLayout

class AddFoodActivity : AppCompatActivity() {
    private lateinit var labelInput: EditText
    private lateinit var caloriesInput: EditText
    private lateinit var labelLayout: TextInputLayout
    private lateinit var caloriesLayout: TextInputLayout
    private lateinit var addFoodBtn: Button
    private lateinit var closeBtn: ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_food)

        // Initialize views
        labelInput = findViewById(R.id.labelInput)
        caloriesInput = findViewById(R.id.caloriesInput)
        labelLayout = findViewById(R.id.labelLayout)
        caloriesLayout = findViewById(R.id.calorieLayout)
        addFoodBtn = findViewById(R.id.addFoodBtn)
        closeBtn = findViewById(R.id.closeBtn)

        labelInput.addTextChangedListener {
            if (it!!.isNotEmpty())
                labelLayout.error = null
        }
        caloriesInput.addTextChangedListener {
            if (it!!.isNotEmpty())
                caloriesLayout.error = null
        }

        addFoodBtn.setOnClickListener {
            val label = labelInput.text.toString()
            val calories = caloriesInput.text.toString().toDoubleOrNull()

            if (label.isEmpty()) {
                labelLayout.error = "Please enter a valid label"
            } else {
                labelLayout.error = null
            }

            if (calories == null) {
                caloriesLayout.error = "Please enter a valid amount"
            } else {
                caloriesLayout.error = null

                val food = Foods(0, label, calories, null)

                val dbHelper = DatabaseHelper(this)
                val insertedId = dbHelper.addFood(food)

                if (insertedId != -1L) {
                    labelInput.text.clear()
                    caloriesInput.text.clear()
                    labelLayout.error = null
                    caloriesLayout.error = null

                    val intent = Intent()
                    intent.putExtra("foodAdded", true)
                    setResult(Activity.RESULT_OK, intent)
                    finish()
                } else {
                    // Handle insertion failure if necessary
                }
            }
        }
        closeBtn.setOnClickListener {
            finish()
        }
    }
}
