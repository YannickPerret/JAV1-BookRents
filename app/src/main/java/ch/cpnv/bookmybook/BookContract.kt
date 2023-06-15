package ch.cpnv.bookmybook

import android.provider.BaseColumns

object BookContract {
    object BookEntry : BaseColumns {
        const val TABLE_NAME = "book"
        const val COLUMN_NAME_ID = "_id"
        const val COLUMN_NAME_NAME = "name"
        const val COLUMN_NAME_ISBN = "isbn"
    }
}