package com.example.tamyrapp2.UI.ond

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.example.tamyrapp2.R

class OnboardingActivity : AppCompatActivity() {

    lateinit var viewPager: ViewPager2
    private lateinit var adapter: OnboardingAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_onboarding)

        viewPager = findViewById(R.id.viewPager)
        adapter = OnboardingAdapter(this)
        viewPager.adapter = adapter
    }
}
