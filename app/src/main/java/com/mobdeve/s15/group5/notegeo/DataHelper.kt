package com.mobdeve.s15.group5.notegeo

import com.mobdeve.s15.group5.notegeo.models.LabelEntry

class DataHelper {
    companion object {
        fun loadLabelEntries() =
            arrayListOf(LabelEntry("Sample"), LabelEntry("Hello World"), LabelEntry("Another One"))
    }
}