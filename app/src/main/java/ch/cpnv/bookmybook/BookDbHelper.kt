import android.annotation.SuppressLint
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import ch.cpnv.bookmybook.Book
import ch.cpnv.bookmybook.BookContract

class BookDbHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "bookMyBook.db"
        private const val DATABASE_VERSION = 4 // Incremented version to trigger onUpgrade()
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

    @SuppressLint("Range")
    fun getBookById(bookId: Long): Book? {
        val db = this.readableDatabase
        val cursor = db.query(BookContract.BookEntry.TABLE_NAME, null, "${BookContract.BookEntry.COLUMN_NAME_ID}=?", arrayOf(bookId.toString()), null, null, null)
        var book: Book? = null
        if (cursor.moveToFirst()) {
            val id = cursor.getLong(cursor.getColumnIndex(BookContract.BookEntry.COLUMN_NAME_ID))
            val name = cursor.getString(cursor.getColumnIndex(BookContract.BookEntry.COLUMN_NAME_NAME))
            val isbn = cursor.getString(cursor.getColumnIndex(BookContract.BookEntry.COLUMN_NAME_ISBN))
            book = Book(id, name, isbn)
        }
        cursor.close()
        return book
    }


    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS reservation")
        db.execSQL("DROP TABLE IF EXISTS book")

        onCreate(db)
    }
}
