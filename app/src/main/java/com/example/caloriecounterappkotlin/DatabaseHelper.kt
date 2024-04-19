package com.example.caloriecounterappkotlin

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_VERSION = 2
        private const val DATABASE_NAME = "NewFoodsDB"
        private const val TABLE_NAME = "foods"
        private const val COLUMN_ID = "id"
        private const val COLUMN_LABEL = "label"
        private const val COLUMN_CALORIES = "calories"
        private const val COLUMN_DESCRIPTION = "description"
        private const val COLUMN_CATEGORY = "category"
    }

    override fun onCreate(db: SQLiteDatabase) {
        val CREATE_FOODS_TABLE = "CREATE TABLE $TABLE_NAME ($COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT, $COLUMN_LABEL TEXT, $COLUMN_CALORIES REAL, $COLUMN_DESCRIPTION TEXT, $COLUMN_CATEGORY TEXT)"
        db.execSQL(CREATE_FOODS_TABLE)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        onCreate(db)
    }

    fun addFood(food: Foods): Long {
        val values = ContentValues()
        values.put(COLUMN_LABEL, food.label)
        values.put(COLUMN_CALORIES, food.calories)
        values.put(COLUMN_DESCRIPTION, food.description ?: "None")
        values.put(COLUMN_CATEGORY, food.category.name) // Store category as string representation
        val db = this.writableDatabase
        val id = db.insert(TABLE_NAME, null, values)
        db.close()
        return id
    }

    fun getAllFoods(): ArrayList<Foods> {
        val foods = ArrayList<Foods>()
        val selectQuery = "SELECT  * FROM $TABLE_NAME"
        val db = this.readableDatabase
        val cursor: Cursor? = db.rawQuery(selectQuery, null)

        cursor?.let {
            if (it.moveToFirst()) {
                do {
                    val id = it.getLong(it.getColumnIndex(COLUMN_ID))
                    val label = it.getString(it.getColumnIndex(COLUMN_LABEL))
                    val calories = it.getDouble(it.getColumnIndex(COLUMN_CALORIES))
                    val description = it.getString(it.getColumnIndex(COLUMN_DESCRIPTION))
                    val categoryStr = it.getString(it.getColumnIndex(COLUMN_CATEGORY))
                    val category = FoodCategory.valueOf(categoryStr) // Convert string back to enum
                    val food = Foods(id, label, calories, description, category)
                    foods.add(food)
                } while (it.moveToNext())
            }
            it.close()
        }

        db.close()
        return foods
    }

    fun updateFood(food: Foods): Boolean {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_LABEL, food.label)
            put(COLUMN_CALORIES, food.calories)
            put(COLUMN_DESCRIPTION, food.description)
            put(COLUMN_CATEGORY, food.category.name) // Store category as string representation
        }
        val updatedRows = db.update(TABLE_NAME, values, "$COLUMN_ID = ?", arrayOf(food.id.toString()))
        db.close()
        return updatedRows > 0
    }

    fun deleteFood(food: Foods): Boolean {
        val db = this.writableDatabase
        val deletedRows = db.delete(TABLE_NAME, "$COLUMN_ID = ?", arrayOf(food.id.toString()))
        db.close()
        return deletedRows > 0
    }
}
