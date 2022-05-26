package com.austin.neoviewer

import android.app.Activity
import android.view.WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
import android.view.WindowManager.LayoutParams.TYPE_TOAST
import androidx.appcompat.widget.Toolbar
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.Root
import androidx.test.espresso.matcher.ViewMatchers.withText
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.TypeSafeMatcher

fun <T: Activity> ActivityScenario<T>.getToolbarNavigationContentDescriptor(toolbarId: Int): String {
    var description = ""
    onActivity {
        description = it.findViewById<Toolbar>(toolbarId).navigationContentDescription as String
    }
    return description
}
