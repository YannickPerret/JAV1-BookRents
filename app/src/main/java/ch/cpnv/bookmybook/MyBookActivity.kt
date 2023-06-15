package ch.cpnv.bookmybook

import BookDbHelper
import android.content.Intent
import android.database.Cursor
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MyBookActivity : AppCompatActivity() {
    private lateinit var bookAdapter: BookAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_book)

        val recyclerView: RecyclerView = findViewById(R.id.recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(this)

        val bookList = queryBooksFromDatabase()
        bookAdapter = BookAdapter(bookList)
        recyclerView.adapter = bookAdapter

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
    }

    override fun onResume() {
        super.onResume()
        val bookList = queryBooksFromDatabase()
        bookAdapter.updateBooks(bookList)
    }


    private fun queryBooksFromDatabase(): List<Book> {
        val bookDbHelper = BookDbHelper(this)
        val db = bookDbHelper.readableDatabase

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
