package ch.cpnv.bookmybook

import ch.cpnv.bookmybook.Helpers.DatabaseHelper
import android.content.ContentValues
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.widget.Button
import android.widget.EditText
import android.widget.Toast

class NewBookActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_book)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val dbHelper = DatabaseHelper.getInstance(this)
        val db = dbHelper.writableDatabase

        val bookNameInput = findViewById<EditText>(R.id.book_name)
        val bookIsbnInput = findViewById<EditText>(R.id.book_isbn)

        val createBookButton: Button = findViewById(R.id.bookCreate_button)
        createBookButton.setOnClickListener {
            val bookName = bookNameInput.text.toString()
            val bookIsbn = bookIsbnInput.text.toString()

            if(bookName.isBlank() || bookIsbn.isEmpty()){
                Toast.makeText(this, "Veuillez remplir tous les champs", Toast.LENGTH_SHORT).show()
            } else {
                val values = ContentValues().apply {
                    put("name", bookName.trim()) // use trim() to remove leading and trailing spaces
                    put("isbn", bookIsbn)
                }

                val newRowId = db.insert("book", null, values)
                if (newRowId != -1L) {
                    finish()
                }
            }
        }
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}