package com.udacity.util

import android.widget.TextView
import androidx.databinding.BindingAdapter

@BindingAdapter("text")
fun TextView.setContext(text: String?) {
    this.text = text
    contentDescription = text
}
