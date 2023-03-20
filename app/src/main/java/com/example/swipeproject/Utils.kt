package com.example.swipeproject

import android.app.Activity
import android.content.Context
import android.view.inputmethod.InputMethodManager

class Utils {


    companion object {
        fun hideSoftKeyboard(activity: Activity) {
            val inputMethodManager : InputMethodManager = activity.getSystemService(Context.INPUT_METHOD_SERVICE)
            as InputMethodManager

            inputMethodManager.hideSoftInputFromWindow(activity.currentFocus?.windowToken, 0)
        }
    }
}