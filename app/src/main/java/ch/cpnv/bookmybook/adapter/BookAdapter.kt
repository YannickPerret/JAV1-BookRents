package ch.cpnv.bookmybook.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import ch.cpnv.bookmybook.classes.Book
import ch.cpnv.bookmybook.MyBookActivity
import ch.cpnv.bookmybook.R

interface OnBookClickListener {
    fun onBookClick(book: Book)
}

class BookAdapter(private var bookList: List<Book>, private val listener: MyBookActivity) : RecyclerView.Adapter<BookAdapter.BookViewHolder>() {

    class BookViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val bookTitleView: TextView = itemView.findViewById(R.id.id)
        val bookIsbnView: TextView = itemView.findViewById(R.id.isbn)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.book_item, parent, false)
        return BookViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: BookViewHolder, position: Int) {
        val currentBook = bookList[position]
        holder.bookTitleView.text = currentBook.name
        holder.bookIsbnView.text = currentBook.isbn

        holder.itemView.setOnClickListener {
            listener.onBookClick(currentBook)
        }
    }

    fun updateBooks(newBooks: List<Book>) {
        bookList = newBooks
        notifyDataSetChanged()
    }

    override fun getItemCount() = bookList.size
}
