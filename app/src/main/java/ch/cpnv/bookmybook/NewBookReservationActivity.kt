package ch.cpnv.bookmybook

import BookDbHelper
import android.app.Activity
import android.app.DatePickerDialog
import android.content.ContentValues
import android.content.Intent
import android.icu.util.Calendar
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.ContactsContract
import android.view.MenuItem
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.EditText
import androidx.activity.result.contract.ActivityResultContracts
import android.widget.ArrayAdapter
import android.widget.Toast

class NewBookReservationActivity : AppCompatActivity() {
    private lateinit var contactNameInput: AutoCompleteTextView
    private lateinit var bookNameInput: AutoCompleteTextView
    private var selectedBookId: Int? = null
    private lateinit var dbHelper: BookDbHelper

    private val getContent = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val contactUri = result.data?.data
            val projection = arrayOf(ContactsContract.Contacts.DISPLAY_NAME)
            val cursor = contactUri?.let { contentResolver.query(it, projection, null, null, null) }
            cursor?.use {
                if (it.moveToFirst()) {
                    val nameIndex = it.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME)
                    val name = it.getString(nameIndex)
                    contactNameInput.setText(name)
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_book_reservation)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        dbHelper = BookDbHelper(this)

        initViews()
        setupClickListeners()
        setupBookAutocomplete()
    }

    private fun initViews() {
        contactNameInput = findViewById(R.id.contact)
        bookNameInput = findViewById<EditText>(R.id.book_name) as AutoCompleteTextView
    }

    private fun setupClickListeners() {
        contactNameInput.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI)
            getContent.launch(intent)
        }

        bookNameInput.setOnClickListener {
            if (bookNameInput.length() >= 3){
                bookNameInput.showDropDown()
            }
        }
        val startDate = findViewById<EditText>(R.id.start_date)
        startDate.setOnClickListener {
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            DatePickerDialog(this, { _, selectedYear, selectedMonth, selectedDay ->
                startDate.setText("$selectedYear/${selectedMonth + 1}/$selectedDay")
            }, year, month, day).show()
        }

        val reservationButton: Button = findViewById(R.id.bookCreate_button)
        reservationButton.setOnClickListener {
            createReservation()
        }
    }

    private fun setupBookAutocomplete() {
        val db = dbHelper.readableDatabase

        val projection = arrayOf(BookContract.BookEntry.COLUMN_NAME_ID, BookContract.BookEntry.COLUMN_NAME_NAME)
        val cursor = db.query(BookContract.BookEntry.TABLE_NAME, projection, null, null, null, null, null)

        val bookTitles = mutableListOf<String>()
        val bookIds = mutableListOf<Int>()
        while(cursor.moveToNext()) {
            val bookId = cursor.getInt(cursor.getColumnIndexOrThrow(BookContract.BookEntry.COLUMN_NAME_ID))
            val bookTitle = cursor.getString(cursor.getColumnIndexOrThrow(BookContract.BookEntry.COLUMN_NAME_NAME))

            bookTitles.add(bookTitle)
            bookIds.add(bookId)
        }
        cursor.close()

        bookNameInput.setAdapter(ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, bookTitles))
        bookNameInput.setOnItemClickListener { _, _, position, _ ->
            selectedBookId = bookIds[position]
        }
    }

    private fun createReservation() {
        val db = dbHelper.writableDatabase

        val contactName = contactNameInput.text.toString()
        val startDate = findViewById<EditText>(R.id.start_date).text.toString()
        val bookId = selectedBookId

        if (contactName.isEmpty()) {
            Toast.makeText(this, "Veuillez sélectionner un contact", Toast.LENGTH_SHORT).show()
            return
        }

        if (bookId == null) {
            Toast.makeText(this, "Veuillez sélectionner un livre", Toast.LENGTH_SHORT).show()
            return
        }

        if (startDate.isEmpty()) {
            Toast.makeText(this, "Veuillez sélectionner une date de début", Toast.LENGTH_SHORT).show()
            return
        }

        val values = ContentValues().apply {
            put(ReservationContract.BookEntry.COLUMN_NAME_CONTACT, contactName)
            put(ReservationContract.BookEntry.COLUMN_NAME_BOOK, bookId) // save book id instead of book name
            put(ReservationContract.BookEntry.COLUMN_NAME_START_DATE, startDate)
        }

        val newRowId = db?.insert(ReservationContract.BookEntry.TABLE_NAME, null, values)
        if (newRowId != -1L) {
            Toast.makeText(this, "Réservation créée avec succès", Toast.LENGTH_SHORT).show()
            finish()
        } else {
            Toast.makeText(this, "Erreur lors de la création de la réservation", Toast.LENGTH_SHORT).show()
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