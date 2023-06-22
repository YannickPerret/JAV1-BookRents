package ch.cpnv.bookmybook.Helpers

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper private constructor(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "bookMyBook.db"
        private const val DATABASE_VERSION = 4 // Incremented version to trigger onUpgrade()

        @Volatile
        private var INSTANCE: DatabaseHelper? = null

        fun getInstance(context: Context): DatabaseHelper {
            synchronized(this) {
                var instance = INSTANCE

                if (instance == null) {
                    instance = DatabaseHelper(context.applicationContext)
                    INSTANCE = instance
                }

                return instance
            }
        }
    }

    override fun onCreate(db: SQLiteDatabase) {
        val SQL_CREATE_BOOK_TABLE = "CREATE TABLE IF NOT EXISTS book (" +
                "_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "name TEXT, " +
                "isbn TEXT);"

        val SQL_CREATE_RESERVATION_TABLE = "CREATE TABLE IF NOT EXISTS reservation (" +
                "_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "contact TEXT, " +
                "book LONG, " +
                "start_date DATE, " +
                "return_date DATE, " +
                "FOREIGN KEY(book) REFERENCES book(_id));"

        db.execSQL(SQL_CREATE_BOOK_TABLE)
        db.execSQL(SQL_CREATE_RESERVATION_TABLE)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS reservation")
        db.execSQL("DROP TABLE IF EXISTS book")

        onCreate(db)
    }
}
