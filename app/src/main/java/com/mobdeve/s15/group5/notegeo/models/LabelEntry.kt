package com.mobdeve.s15.group5.notegeo.models

import androidx.databinding.ObservableBoolean

class LabelEntry(val label: String = "", var isChecked: ObservableBoolean = ObservableBoolean(false)) {
    constructor(label: String = "", isChecked: Boolean): this(label, ObservableBoolean(isChecked))
}
