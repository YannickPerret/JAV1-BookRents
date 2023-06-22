package ch.cpnv.bookmybook

import android.database.sqlite.SQLiteDatabase
import android.provider.BaseColumns
import androidx.core.database.getStringOrNull
import ch.cpnv.bookmybook.classes.Book
import ch.cpnv.bookmybook.contracts.BookContract
import ch.cpnv.bookmybook.contracts.RentContract

class Rent(val id: Int, val contact: String, val book: Book, val startDate: String, val returnDate: String?) {

    companion object {
        fun getAllRentItems(database: SQLiteDatabase): List<Rent> {
            val items = mutableListOf<Rent>()

            val query = """
                SELECT ${RentContract.BookEntry.TABLE_NAME}.${RentContract.BookEntry.COLUMN_NAME_ID} AS rent_id, 
                ${RentContract.BookEntry.COLUMN_NAME_CONTACT}, 
                ${RentContract.BookEntry.COLUMN_NAME_START_DATE}, 
                ${RentContract.BookEntry.COLUMN_NAME_RETURN_DATE},
                Book.${BookContract.BookEntry.COLUMN_NAME_ISBN},
                Book.${BookContract.BookEntry.COLUMN_NAME_ID} AS book_id,
                Book.${BookContract.BookEntry.COLUMN_NAME_NAME}
                FROM ${RentContract.BookEntry.TABLE_NAME}
                INNER JOIN Book ON ${RentContract.BookEntry.TABLE_NAME}.${RentContract.BookEntry.COLUMN_NAME_BOOK} = Book.${BookContract.BookEntry.COLUMN_NAME_ID}
               """


            val cursor = database.rawQuery(query, null)

            with(cursor) {
                while (moveToNext()) {
                    val id = cursor.getInt(cursor.getColumnIndexOrThrow("rent_id"))
                    val contact = getString(getColumnIndexOrThrow(RentContract.BookEntry.COLUMN_NAME_CONTACT))
                    val bookId = getLong(getColumnIndexOrThrow("book_id"))
                    val bookName = getString(getColumnIndexOrThrow(BookContract.BookEntry.COLUMN_NAME_NAME))
                    val bookIsbn = getString(getColumnIndexOrThrow(BookContract.BookEntry.COLUMN_NAME_ISBN))
                    val startDate = getString(getColumnIndexOrThrow(RentContract.BookEntry.COLUMN_NAME_START_DATE))
                    val returnDate = getStringOrNull(getColumnIndexOrThrow(RentContract.BookEntry.COLUMN_NAME_RETURN_DATE))

                    val book = Book(bookId, bookName, bookIsbn)
                    val rentItem = Rent(id, contact, book, startDate, returnDate)
                    items.add(rentItem)
                }
            }
            for (item in items) {
                println("Rent ID: ${item.id}")
            }
            return items
        }
    }
}
