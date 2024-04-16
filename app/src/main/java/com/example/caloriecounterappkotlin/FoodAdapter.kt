package com.example.caloriecounterappkotlin

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView

class FoodAdapter(private val foods: ArrayList<Foods>) :
    RecyclerView.Adapter<FoodAdapter.FoodHolder>() {

    class FoodHolder(view: View) : RecyclerView.ViewHolder(view) {
        val label : TextView = view.findViewById(R.id.label)
        val calories : TextView = view.findViewById(R.id.calories)
        val type : TextView = view.findViewById(R.id.type)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FoodHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.food_layout, parent, false)
        return FoodHolder(view)
    }

    override fun onBindViewHolder(holder: FoodHolder, position: Int) {
        val food = foods[position]
        val context = holder.calories.context

        //Could add red and green for calorie budgets and expenditure
        holder.calories.text = "+ %.2f cals".format(food.calories)
        holder.calories.setTextColor(ContextCompat.getColor(context, R.color.green))

        holder.label.text = food.label
    }

    override fun getItemCount(): Int {
        return foods.size
    }
}