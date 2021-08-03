package com.mobdeve.s15.group5.notegeo

import com.mobdeve.s15.group5.notegeo.models.Label
import com.mobdeve.s15.group5.notegeo.models.Note

class DataHelper {
    companion object {
        fun loadNotes() = arrayListOf(
            Note("Sample", "Lorem Ipsum Brodie", -16061521),
            Note(
                "Hello!",
                "Just some random text to wee wee. Need to make this note a bit more longer so we can see a difference in the layout",
                -35002
            ),
            Note("Test try...", "Is this cool or what? Kotlin master race OwO", -15262682)
        )
    }
}