package ch.cpnv.bookmybook

import ch.cpnv.bookmybook.Helpers.DatabaseHelper
import android.app.AlertDialog
import android.content.Intent
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.provider.BaseColumns
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ch.cpnv.bookmybook.adapter.BookAdapter
import ch.cpnv.bookmybook.adapter.OnBookClickListener
import ch.cpnv.bookmybook.classes.Book
import ch.cpnv.bookmybook.contracts.BookContract
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MyBookActivity : AppCompatActivity(), OnBookClickListener {
    private lateinit var bookAdapter: BookAdapter
    private lateinit var db: SQLiteDatabase
    private var openedBottomSheetDialog: BottomSheetDialog? = null



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_book)

        val databaseHelper = DatabaseHelper(this)
        db = databaseHelper.writableDatabase


        val recyclerView: RecyclerView = findViewById(R.id.recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(this)

        val bookList = queryBooksFromDatabase()

        val fab: FloatingActionButton = findViewById(R.id.fab)
        fab.setOnClickListener {
            val intent = Intent(this, NewBookActivity::class.java)
            startActivity(intent)
        }

        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNavigationView.menu.findItem(R.id.navigation_item3).isChecked = true

        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_item1 -> {
                    startActivity(Intent(this, MainActivity::class.java))
                    true
                }
                R.id.navigation_item3 -> {
                    true
                }
                else -> false
            }
        }

        bookAdapter = BookAdapter(bookList, this)
        recyclerView.adapter = bookAdapter
    }

    override fun onBookClick(book: Book) {
        // If a BottomSheetDialog is already opened, return
        if (openedBottomSheetDialog?.isShowing == true) return

        val bottomSheetDialog = BottomSheetDialog(this)
        val sheetView = layoutInflater.inflate(R.layout.bottom_sheet_dialog, null)
        bottomSheetDialog.setContentView(sheetView)

        sheetView.findViewById<Button>(R.id.create_reservation_button).setOnClickListener {
            // Handle reservation creation
            val intent = Intent(this, NewBookReservationActivity::class.java)
            intent.putExtra("bookId", book.id) // Pass the clicked book's id to the new activity
            startActivity(intent)
            bottomSheetDialog.dismiss()
            openedBottomSheetDialog = null
        }

        sheetView.findViewById<Button>(R.id.delete_button).setOnClickListener {
            bottomSheetDialog.dismiss()
            openedBottomSheetDialog = null

            // Show confirmation dialog here
            val builder = AlertDialog.Builder(this)

            builder.setTitle("Confirmation")
            builder.setMessage("Êtes-vous sûr de vouloir supprimer ce livre ?")

            builder.setPositiveButton("OUI") { dialog, _ ->
                dialog.dismiss()

                // Delete the book from the database
                val deleteQuery = "DELETE FROM ${BookContract.BookEntry.TABLE_NAME} WHERE ${BaseColumns._ID} = ${book.id}"
                db.execSQL(deleteQuery)
                onResume()
            }

            builder.setNegativeButton("NON") { dialog, _ ->
                dialog.dismiss()
            }

            val dialog = builder.create()
            dialog.show()
        }

        bottomSheetDialog.show()
        openedBottomSheetDialog = bottomSheetDialog
    }


    override fun onResume() {
        super.onResume()
        val bookList = queryBooksFromDatabase()
        bookAdapter.updateBooks(bookList)
    }


    private fun queryBooksFromDatabase(): List<Book> {
        val databaseHelper = DatabaseHelper(this)
        val db = databaseHelper.readableDatabase

        val projection = arrayOf("_id", "name", "isbn")
        val cursor: Cursor = db.query("book", projection, null, null, null, null, null)

        val bookList = mutableListOf<Book>()
        with(cursor) {
            while (moveToNext()) {
                val id = getLong(getColumnIndexOrThrow("_id"))
                val name = getString(getColumnIndexOrThrow("name"))
                val isbn = getString(getColumnIndexOrThrow("isbn"))
                bookList.add(Book(id, name, isbn))
            }
        }
        return bookList
    }
}
