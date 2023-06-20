package ch.cpnv.bookmybook

data class BookItemReservation(val id: Int, val contact: String, val bookId: Long, val bookName: String, val startDate: String, val returnDate: String?)
