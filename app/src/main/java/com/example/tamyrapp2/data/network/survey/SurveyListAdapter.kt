package com.example.tamyrapp2.data.network.survey

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.example.tamyrapp2.R
import com.example.tamyrapp2.data.network.survey.SurveyDataRequest

class SurveyListAdapter(
    context: Context,
    private val surveys: List<SurveyDataRequest>
) : ArrayAdapter<SurveyDataRequest>(context, 0, surveys) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val itemView = convertView ?: LayoutInflater.from(context).inflate(R.layout.item_survey, parent, false)

        val survey = surveys[position]
        val descriptionTextView = itemView.findViewById<TextView>(R.id.tvSurveyDescription)

        descriptionTextView.text = survey.surveyDescription

        return itemView
    }
}
