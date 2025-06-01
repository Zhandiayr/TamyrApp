package com.example.tamyrapp2.data.network.survey

data class AnswerDataRequest(
    val surveyId: Long,
    val userId: Long,
    val date: String,
    val answer: String
)
