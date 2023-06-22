package ch.cpnv.bookmybook.contracts

import android.provider.BaseColumns

object RentContract {
    object BookEntry : BaseColumns {
        const val TABLE_NAME = "reservation"
        const val COLUMN_NAME_ID = "_id"
        const val COLUMN_NAME_CONTACT = "contact"
        const val COLUMN_NAME_BOOK = "book"
        const val COLUMN_NAME_START_DATE = "start_date"
        const val COLUMN_NAME_RETURN_DATE = "return_date"
    }
}
