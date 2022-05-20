package com.austin.neoviewer

import android.app.Activity
import androidx.appcompat.widget.Toolbar
import androidx.test.core.app.ActivityScenario

fun <T: Activity> ActivityScenario<T>.getToolbarNavigationContentDescriptor(toolbarId: Int): String {
    var description = ""
    onActivity {
        description = it.findViewById<Toolbar>(toolbarId).navigationContentDescription as String
    }
    return description
}