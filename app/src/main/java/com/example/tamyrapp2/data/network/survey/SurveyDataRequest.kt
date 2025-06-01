package com.example.tamyrapp2.data.network.survey

data class SurveyDataRequest(
    val surveyId: Long,
    val surveyDescription: String,
    val questionsAnswersVariants: String,
    val isDaily: Boolean,
    val surveyType: String
)


