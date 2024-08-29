package com.example.caloriecounterappkotlin

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : ComponentActivity() {
    private lateinit var foods: ArrayList<Foods>
    private lateinit var foodAdapter: FoodAdapter
    private lateinit var linearLayoutManager: LinearLayoutManager
    private lateinit var balanceTextView: TextView
    private lateinit var budgetTextView: TextView
    private lateinit var expenseTextView: TextView
    private lateinit var addBtn: FloatingActionButton
    private lateinit var dbHelper: DatabaseHelper

    private var lastDeletedFood: Foods? = null

    private val addFoodLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val foodAdded = result.data?.getBooleanExtra("foodAdded", false) ?: false
            if (foodAdded) {
                refreshFoodsListAndDashboard()
            }
        }
    }

    private val updateFoodLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val foodUpdated = result.data?.getBooleanExtra("foodUpdated", false) ?: false
            if (foodUpdated) {
                refreshFoodsListAndDashboard()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        dbHelper = DatabaseHelper(this)

        // Initialize the RecyclerView
        val recyclerView: RecyclerView = findViewById(R.id.recyclerview)

        foods = dbHelper.getAllFoods()

        foodAdapter = FoodAdapter(foods)
        linearLayoutManager = LinearLayoutManager(this)

        recyclerView.apply {
            adapter = foodAdapter
            layoutManager = linearLayoutManager
        }

        // Find TextViews
        balanceTextView = findViewById(R.id.balance)
        budgetTextView = findViewById(R.id.budget)
        expenseTextView = findViewById(R.id.expense)

        //Find Button
        addBtn = findViewById(R.id.addBtn)

        // swipe to remove
        val itemTouchHelper = object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                deleteFood(foods[viewHolder.adapterPosition])
            }
        }

        val swipeHelper = ItemTouchHelper(itemTouchHelper)
        swipeHelper.attachToRecyclerView(recyclerView)

        addBtn.setOnClickListener {
            val intent = Intent(this, AddFoodActivity::class.java)
            addFoodLauncher.launch(intent)
        }

        foodAdapter.setOnItemClickListener(object : FoodAdapter.OnFoodItemClickListener {
            override fun onFoodItemClick(food: Foods) {
                updateFood(food)
            }
        })
    }

    private fun updateFood(food: Foods) {
        val intent = Intent(this, DetailedActivity::class.java)
        intent.putExtra("food", food)
        // Pass the category along with the food object
        intent.putExtra("category", food.category)
        updateFoodLauncher.launch(intent)
    }


    private fun refreshFoodsListAndDashboard() {
        foods.clear()
        foods.addAll(dbHelper.getAllFoods())
        foodAdapter.notifyDataSetChanged()
        updateDashboard()
    }

    private fun undoDelete() {
        lastDeletedFood?.let {
            GlobalScope.launch {
                dbHelper.addFood(it)
                withContext(Dispatchers.Main) {
                    foods.add(it)
                    foodAdapter.notifyItemInserted(foods.size - 1)
                    updateDashboard()
                }
            }
        }
        lastDeletedFood = null
    }

    private fun showSnackbar(food: Foods) {
        val view = findViewById<View>(R.id.coordinator)
        val snackbar = Snackbar.make(view, "Food deleted!", Snackbar.LENGTH_LONG)
        snackbar.setAction("Undo") {
            undoDelete()
            snackbar.dismiss()
        }
            .setActionTextColor(ContextCompat.getColor(this, R.color.red))
            .setTextColor(ContextCompat.getColor(this, R.color.white))
            .show()
    }

    private fun deleteFood(food: Foods) {
        lastDeletedFood = food
        val deleted = dbHelper.deleteFood(food)
        if (deleted) {
            foods.remove(food)
            foodAdapter.notifyDataSetChanged()
            updateDashboard()
            showSnackbar(food)
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

