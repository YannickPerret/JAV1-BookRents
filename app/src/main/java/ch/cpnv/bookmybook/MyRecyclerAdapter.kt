package ch.cpnv.bookmybook

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class MyRecyclerAdapter(private val dataSet: List<BookItemReservation>) :
    RecyclerView.Adapter<MyRecyclerAdapter.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val contactName: TextView = view.findViewById(R.id.contactName)
        val bookName: TextView = view.findViewById(R.id.bookName)
        val startDate: TextView = view.findViewById(R.id.startDate)
        val returnDate: TextView = view.findViewById(R.id.returnDate)

    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.text_row_item, viewGroup, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val bookItem: BookItemReservation = dataSet[position]
        holder.contactName.text = bookItem.contact
        holder.bookName.text = bookItem.bookName
        holder.startDate.text = bookItem.startDate
        holder.returnDate.text = bookItem.returnDate ?: "en attente"

        if (bookItem.returnDate != null) {
            holder.itemView.setBackgroundColor(Color.GREEN)
        } else {
            holder.itemView.setBackgroundColor(Color.WHITE)
        }
    }


    override fun getItemCount() = dataSet.size
}
