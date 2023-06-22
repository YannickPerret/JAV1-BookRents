package ch.cpnv.bookmybook.classes

data class Book(val id: Long, val name: String, val isbn: String){
    override fun toString(): String {
        return name
    }
}
