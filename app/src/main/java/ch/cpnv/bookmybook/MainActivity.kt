package ch.cpnv.bookmybook

import BookDbHelper
import ReservationContract
import android.content.ContentValues
import android.content.Intent
import android.database.sqlite.SQLiteDatabase
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.icu.text.SimpleDateFormat
import android.os.Bundle
import android.provider.BaseColumns
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.util.Date
import java.util.Locale

class MainActivity : AppCompatActivity() {
    private lateinit var dbHelper: BookDbHelper
    private lateinit var db: SQLiteDatabase
    private lateinit var recyclerView: RecyclerView
    private lateinit var items: MutableList<BookItemReservation>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        dbHelper = BookDbHelper(this)
        db = dbHelper.readableDatabase

        recyclerView = findViewById(R.id.my_recycler_view)
        items = mutableListOf<BookItemReservation>()

        val itemTouchHelperCallback = object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                val bookItem = items[position]

                // Mettez à jour la base de données avec la date de retour actuelle
                val currentDate = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())
                val values = ContentValues().apply {
                    put(ReservationContract.BookEntry.COLUMN_NAME_RETURN_DATE, currentDate)
                }
                val selection = "${BaseColumns._ID} = ?"
                val selectionArgs = arrayOf(bookItem.id.toString())
                db.update(ReservationContract.BookEntry.TABLE_NAME, values, selection, selectionArgs)

                // Recharger les données de la RecyclerView
                onResume()
            }
            override fun onChildDraw(c: Canvas, recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, dX: Float, dY: Float, actionState: Int, isCurrentlyActive: Boolean) {
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)

                val itemView = viewHolder.itemView

                if (dX < 0) { // Swipe vers la gauche
                    val paint = Paint()
                    paint.color = Color.GREEN
                    val background = RectF(itemView.right.toFloat() + dX, itemView.top.toFloat(), itemView.right.toFloat(), itemView.bottom.toFloat())
                    c.drawRect(background, paint)

                    paint.color = Color.WHITE
                    paint.textSize = 48f
                    val text = "Valider"
                    c.drawText(text, itemView.right.toFloat() - 100, itemView.top.toFloat() + 50, paint)
                }
            }
        }

        val itemTouchHelper = ItemTouchHelper(itemTouchHelperCallback)
        itemTouchHelper.attachToRecyclerView(recyclerView)

        val fab: FloatingActionButton = findViewById(R.id.fab)
        fab.setOnClickListener {
            val intent = Intent(this, NewBookReservationActivity::class.java)
            startActivity(intent)
        }

        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNavigationView.menu.findItem(R.id.navigation_item1).isChecked = true

        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_item1 -> {
                    true
                }
                R.id.navigation_item3 -> {
                    startActivity(Intent(this, MyBookActivity::class.java))
                    true
                }
                else -> false
            }
        }
    }
    override fun onResume() {
        super.onResume()

        // Vide la liste d'éléments et la recharge avec les nouvelles données de la base de données.
        items.clear()

        val projection = arrayOf(BaseColumns._ID, ReservationContract.BookEntry.COLUMN_NAME_CONTACT, ReservationContract.BookEntry.COLUMN_NAME_BOOK, ReservationContract.BookEntry.COLUMN_NAME_START_DATE)

        val cursor = db.query(
            ReservationContract.BookEntry.TABLE_NAME,
            projection,
            null,
            null,
            null,
            null,
            null
        )

        with(cursor) {
            while (moveToNext()) {
                val id = getInt(getColumnIndexOrThrow(ReservationContract.BookEntry.COLUMN_NAME_ID))
                val contact = getString(getColumnIndexOrThrow(ReservationContract.BookEntry.COLUMN_NAME_CONTACT))
                val book = getString(getColumnIndexOrThrow(ReservationContract.BookEntry.COLUMN_NAME_BOOK))
                val startDate = getString(getColumnIndexOrThrow(ReservationContract.BookEntry.COLUMN_NAME_START_DATE))

                val item = BookItemReservation(id, contact, book, startDate)

                items.add(item)
            }
        }

        recyclerView.layoutManager = LinearLayoutManager(this)
        val adapter = MyRecyclerAdapter(items)
        recyclerView.adapter = adapter

    }

    override fun onDestroy() {
        db.close()
        super.onDestroy()
    }
}
