package com.example.tamyrapp2.data.network.survey

import retrofit2.Call
import retrofit2.http.*

interface SurveyApiService {

    @GET("api/surveys/all")
    fun getAllSurveys(
        @Header("Authorization") token: String
    ): Call<List<SurveyDataRequest>>

    @GET("api/surveys/user/{userId}/answers")
    fun getAnswersByUserId(
        @Path("userId") userId: Long,
        @Header("Authorization") token: String
    ): Call<List<AnswerDataRequest>>

    @GET("api/surveys/user/{userId}/survey/{surveyId}/has-answered")
    fun hasUserAnsweredSurvey(
        @Path("userId") userId: Long,
        @Path("surveyId") surveyId: Long,
        @Header("Authorization") token: String
    ): Call<Boolean>

    @POST("api/surveys/submit")
    fun submitAnswer(
        @Body answer: AnswerDataRequest,
        @Header("Authorization") token: String
    ): Call<AnswerDataRequest>

    @GET("api/surveys/{surveyId}")
    fun getSurveyById(
        @Path("surveyId") surveyId: Long,
        @Header("Authorization") token: String
    ): Call<SurveyDataRequest>

    @GET("api/surveys/max-answer-id")
    fun getMaxAnswerId(
        @Header("Authorization") token: String
    ): Call<Long>
}
