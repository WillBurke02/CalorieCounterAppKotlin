import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.caloriecounterappkotlin.Foods

class DatabaseHelper(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_VERSION = 1
        private const val DATABASE_NAME = "FoodsDB"
        private const val TABLE_NAME = "foods"
        private const val COLUMN_ID = "id"
        private const val COLUMN_LABEL = "label"
        private const val COLUMN_CALORIES = "calories"
    }

    override fun onCreate(db: SQLiteDatabase) {
        val CREATE_FOODS_TABLE = ("CREATE TABLE $TABLE_NAME ($COLUMN_ID INTEGER PRIMARY KEY, $COLUMN_LABEL TEXT, $COLUMN_CALORIES REAL)")
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
                    val label = it.getString(it.getColumnIndex(COLUMN_LABEL))
                    val calories = it.getDouble(it.getColumnIndex(COLUMN_CALORIES))
                    val food = Foods(label, calories)
                    foods.add(food)
                } while (it.moveToNext())
            }
            it.close()
        }

        db.close()
        return foods
    }
}