package ch.cpnv.bookmybook.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import ch.cpnv.bookmybook.R
import ch.cpnv.bookmybook.Rent
import ch.cpnv.bookmybook.activities.MainActivity
interface OnRentClickListener {
    fun onRentClick(rent: Rent)
}
class RentAdapter(private val items: List<Rent>, mainActivity: MainActivity,private val listener: OnRentClickListener) : RecyclerView.Adapter<RentAdapter.MyViewHolder>() {

    inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val contactName: TextView = view.findViewById(R.id.contactName)
        val bookName: TextView = view.findViewById(R.id.bookName)
        val startDate: TextView = view.findViewById(R.id.startDate)
        val returnDate: TextView = view.findViewById(R.id.returnDate)
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.row_item, viewGroup, false)

        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val rentItem: Rent = items[position]
        holder.contactName.text = rentItem.contact
        holder.bookName.text = rentItem.book.name
        holder.startDate.text = rentItem.startDate
        holder.returnDate.text = rentItem.returnDate ?: "en attente"

        if (rentItem.returnDate != null) {
            holder.itemView.setBackgroundColor(Color.GREEN)
        } else {
            holder.itemView.setBackgroundColor(Color.WHITE)
        }

        // Gestion du clic sur le Rent item
        holder.itemView.setOnClickListener {
            listener.onRentClick(rentItem)
        }
    }

    override fun getItemCount() = items.size
}
