package com.example.tamyrapp2.UI.ond

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.viewpager2.widget.ViewPager2
import com.example.tamyrapp2.R
import com.example.tamyrapp2.UI.PersonalInfoActivity
import com.example.tamyrapp2.UI.ond.OnboardingActivity

class OnboardingFragment : Fragment() {

    companion object {
        private const val ARG_TITLE = "title"
        private const val ARG_DESCRIPTION = "description"
        private const val ARG_IMAGE_RES = "imageRes"

        fun newInstance(title: String, description: String, imageRes: Int) = OnboardingFragment().apply {
            arguments = Bundle().apply {
                putString(ARG_TITLE, title)
                putString(ARG_DESCRIPTION, description)
                putInt(ARG_IMAGE_RES, imageRes)
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_onboarding, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val title = view.findViewById<TextView>(R.id.titleOnboarding)
        val description = view.findViewById<TextView>(R.id.descriptionOnboarding)
        val image = view.findViewById<ImageView>(R.id.imageOnboarding)
        val indicatorContainer = view.findViewById<LinearLayout>(R.id.indicatorContainer)
        val skipTour = view.findViewById<TextView>(R.id.skipTour)

        arguments?.let {
            title.text = it.getString(ARG_TITLE)
            description.text = it.getString(ARG_DESCRIPTION)
            image.setImageResource(it.getInt(ARG_IMAGE_RES))
        }

        val onboardingActivity = activity as? OnboardingActivity
            onboardingActivity?.let { act ->
            val adapter = act.adapter

        act.viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                updateIndicators(indicatorContainer, position)

                skipTour.visibility = if (position == (adapter?.itemCount ?: 0) - 1) View.VISIBLE else View.GONE
            }
        })
    }

    skipTour.setOnClickListener {
        val intent = Intent(requireContext(), PersonalInfoActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        activity?.finish()
    }
}

    private fun updateIndicators(container: LinearLayout, selectedPosition: Int) {
        for (i in 0 until container.childCount) {
            val view = container.getChildAt(i)
            if (i == selectedPosition) {
                view.layoutParams.width = dpToPx(12)
                view.layoutParams.height = dpToPx(6)
                view.setBackgroundResource(R.drawable.indicator_active)
            } else {
                view.layoutParams.width = dpToPx(6)
                view.layoutParams.height = dpToPx(6)
                view.setBackgroundResource(R.drawable.indicator_inactive)
            }
            view.requestLayout()
        }
    }

    private fun dpToPx(dp: Int): Int {
        val density = resources.displayMetrics.density
        return (dp * density).toInt()
    }
}
