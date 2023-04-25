package com.example.galleon


import android.os.Bundle
import android.util.TypedValue
import android.view.View
import androidx.annotation.ColorInt
import androidx.preference.PreferenceFragmentCompat


class PreferencesSettings : PreferenceFragmentCompat()  {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences, rootKey)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        //Give settings background color
        val typedValue = TypedValue()
        val theme = requireContext().theme
        theme.resolveAttribute(R.attr.colorNavigation, typedValue, true)
        @ColorInt val color = typedValue.data
        view.setBackgroundColor(color)

        super.onViewCreated(view, savedInstanceState)
    }

}