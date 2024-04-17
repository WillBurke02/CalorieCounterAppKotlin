package com.example.caloriecounterappkotlin

import DatabaseHelper
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.widget.TextView
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MainActivity : ComponentActivity() {
    private lateinit var foods: ArrayList<Foods>
    private lateinit var foodAdapter: FoodAdapter
    private lateinit var linearLayoutManager: LinearLayoutManager
    private lateinit var balanceTextView: TextView
    private lateinit var budgetTextView: TextView
    private lateinit var expenseTextView: TextView
    private lateinit var addBtn: FloatingActionButton
    private lateinit var dbHelper: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        dbHelper = DatabaseHelper(this)

        // Initialize the RecyclerView
        val recyclerView: RecyclerView = findViewById(R.id.recyclerview)

        // Find TextViews
        balanceTextView = findViewById(R.id.balance)
        budgetTextView = findViewById(R.id.budget)
        expenseTextView = findViewById(R.id.expense)

        //Find Button
        addBtn = findViewById(R.id.addBtn)

        foods = dbHelper.getAllFoods()

        foodAdapter = FoodAdapter(foods)
        linearLayoutManager = LinearLayoutManager(this)

        recyclerView.apply {
            adapter = foodAdapter
            layoutManager = linearLayoutManager
        }

        // Update dashboard
        updateDashboard()

        addBtn.setOnClickListener {
            val intent = Intent(this, AddFoodActivity::class.java)
            startActivity(intent)
        }
    }

    private fun updateDashboard() {
        val totalCalories = foods.map { it.calories }.sum()
        val budgetCalories = foods.filter { it.calories > 0 }.map { it.calories }.sum()
        val expenseCalories = totalCalories - budgetCalories

        balanceTextView.text = "%.2f cals".format(totalCalories)
        budgetTextView.text = "%.2f cals".format(budgetCalories)
        expenseTextView.text = "%.2f cals".format(expenseCalories)
    }
}
