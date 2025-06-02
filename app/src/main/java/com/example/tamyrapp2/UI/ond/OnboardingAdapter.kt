package com.example.tamyrapp2.UI.ond

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.tamyrapp2.R

class OnboardingAdapter(fragmentActivity: FragmentActivity) : FragmentStateAdapter(fragmentActivity) {

    private val pages = listOf(
        OnboardingFragment.newInstance(
            "Welcome to the App",
            "Welcome to “TAMYR”! Our app will help you monitor your heart health using data from wearable devices and surveys. We offer a smart and adaptive approach to health, predicting risks in time and improving your quality of life.",
            R.drawable.onboarding_1
        ),
        OnboardingFragment.newInstance(
            "Connect Device",
            "To get accurate data about your condition, please connect your wearable device. We support smartwatches and fitness trackers to monitor essential health metrics.",
            R.drawable.onboarding_2
        ),
        OnboardingFragment.newInstance(
            "Filling out a Profile",
            "Now we need some information about you so that the system can make accurate predictions and recommendations.",
            R.drawable.onboarding_3
        )
    )

    override fun getItemCount(): Int = pages.size

    override fun createFragment(position: Int): Fragment = pages[position]
}