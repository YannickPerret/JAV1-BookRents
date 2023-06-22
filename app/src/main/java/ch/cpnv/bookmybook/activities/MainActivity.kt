package ch.cpnv.bookmybook.activities

import android.app.AlertDialog
import ch.cpnv.bookmybook.Helpers.DatabaseHelper
import ch.cpnv.bookmybook.contracts.RentContract
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
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ch.cpnv.bookmybook.R
import ch.cpnv.bookmybook.Rent
import ch.cpnv.bookmybook.adapter.OnRentClickListener
import ch.cpnv.bookmybook.adapter.RentAdapter
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.util.Date
import java.util.Locale

class MainActivity : AppCompatActivity(), OnRentClickListener {
    private lateinit var dbHelper: DatabaseHelper
    private lateinit var db: SQLiteDatabase
    private lateinit var recyclerView: RecyclerView
    private lateinit var items: MutableList<Rent>
    private lateinit var adapter: RentAdapter
    private var openedBottomSheetDialog: BottomSheetDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        dbHelper = DatabaseHelper.getInstance(this)
        db = dbHelper.readableDatabase

        recyclerView = findViewById(R.id.my_recycler_view)
        items = mutableListOf()

        val itemTouchHelperCallback = object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                val rentItem = items[position]

                val currentDate = SimpleDateFormat("yyyy/MM/dd", Locale.getDefault()).format(Date())
                val values = ContentValues().apply {
                    put(RentContract.BookEntry.COLUMN_NAME_RETURN_DATE, currentDate)
                }

                val selection = RentContract.BookEntry.COLUMN_NAME_ID + " = ?"
                val selectionArgs = arrayOf(rentItem.id.toString())

                val rowsAffected = db.update(RentContract.BookEntry.TABLE_NAME, values, selection, selectionArgs)
                if (rowsAffected > 0) {
                    println("La mise à jour a réussi")
                } else {
                    println("La mise à jour a échoué")
                }

                adapter.notifyDataSetChanged()
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

    override fun onRentClick(rent: Rent) {
        // Si un BottomSheetDialog est déjà ouvert, retour
        if (openedBottomSheetDialog?.isShowing == true) return

        val bottomSheetDialog = BottomSheetDialog(this)
        val sheetView = layoutInflater.inflate(R.layout.bottom_rent_dialog, null)
        bottomSheetDialog.setContentView(sheetView)

        sheetView.findViewById<Button>(R.id.delete_button).setOnClickListener {
            bottomSheetDialog.dismiss()
            openedBottomSheetDialog = null

            val builder = AlertDialog.Builder(this)

            builder.setTitle("Confirmation")
            builder.setMessage("Êtes-vous sûr de vouloir supprimer cette réservation ?")

            builder.setPositiveButton("OUI") { dialog, _ ->
                dialog.dismiss()
                // Supprimer la réservation de la base de données
                val deleteQuery = "DELETE FROM ${RentContract.BookEntry.TABLE_NAME} WHERE ${BaseColumns._ID} = ${rent.id}"
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
        items.clear()
        items.addAll(Rent.getAllRentItems(db))

        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = RentAdapter(items, this, this)
        recyclerView.adapter = adapter
    }
    override fun onDestroy() {
        super.onDestroy()
        db.close()
    }
}
