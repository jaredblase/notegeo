package com.mobdeve.s15.group5.notegeo

import com.mobdeve.s15.group5.notegeo.models.LabelEntry
import com.mobdeve.s15.group5.notegeo.models.Note

class DataHelper {
    companion object {
        fun loadLabelEntries() =
            arrayListOf(LabelEntry("Sample"), LabelEntry("Hello World"), LabelEntry("Another One"))

        fun loadNotes() = arrayListOf(
            Note("Sample", "Lorem Ipsum Brodie"),
            Note(
                "Hello!",
                "Just some random text to wee wee. Need to make this note a bit more longer so we can see a difference in the layout"
            ),
            Note("Test try...", "Is this cool or what? Kotlin master race OwO")
        )
    }
}