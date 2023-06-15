import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class BookDbHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "bookMyBook.db"
        private const val DATABASE_VERSION = 3 // Incremented version to trigger onUpgrade()
    }

    override fun onCreate(db: SQLiteDatabase) {
        val SQL_CREATE_BOOK_TABLE = "CREATE TABLE IF NOT EXISTS book (" +
                "_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "name TEXT, " +
                "isbn TEXT);"

        val SQL_CREATE_RESERVATION_TABLE = "CREATE TABLE IF NOT EXISTS reservation (" +
                "_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "contact TEXT, " +
                "book INTEGER, " + // Changed type to INTEGER
                "start_date DATE, " +
                "return_date DATE, " +
                "FOREIGN KEY(book) REFERENCES book(_id));" // Added foreign key constraint

        db.execSQL(SQL_CREATE_BOOK_TABLE)
        db.execSQL(SQL_CREATE_RESERVATION_TABLE)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS reservation")
        db.execSQL("DROP TABLE IF EXISTS book")

        onCreate(db)
    }
}
