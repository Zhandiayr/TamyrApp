package com.example.tamyrapp2.data.network.survey

import android.app.Application
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.example.tamyrapp2.data.network.RetrofitInstance
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SurveyViewModel(application: Application) : AndroidViewModel(application) {

    val allSurveys = MutableLiveData<List<SurveyDataRequest>?>()
    val unansweredSurveys = MutableLiveData<List<SurveyDataRequest>>()
    val answerSubmitSuccess = MutableLiveData<Boolean>()
    val maxAnswerId = MutableLiveData<Long>()

    fun fetchUnansweredSurveys(userId: Long, token: String) {
        val authHeader = "Bearer $token"
        RetrofitInstance.surveyApi.getAllSurveys(authHeader)
            .enqueue(object : Callback<List<SurveyDataRequest>> {
                override fun onResponse(
                    call: Call<List<SurveyDataRequest>>,
                    response: Response<List<SurveyDataRequest>>
                ) {
                    val surveys = response.body()
                    if (!response.isSuccessful || surveys == null) {
                        Toast.makeText(getApplication(), "Failed to load surveys", Toast.LENGTH_SHORT).show()
                        unansweredSurveys.value = emptyList()
                        return
                    }
                    allSurveys.value = surveys
                    fetchUserAnswers(userId, surveys, authHeader)
                }

                override fun onFailure(call: Call<List<SurveyDataRequest>>, t: Throwable) {
                    Toast.makeText(getApplication(), "Failed to load surveys: ${t.message}", Toast.LENGTH_SHORT).show()
                    unansweredSurveys.value = emptyList()
                }
            })
    }

    private fun fetchUserAnswers(userId: Long, surveys: List<SurveyDataRequest>, authHeader: String) {
        RetrofitInstance.surveyApi.getAnswersByUserId(userId, authHeader)
            .enqueue(object : Callback<List<AnswerDataRequest>> {
                override fun onResponse(
                    call: Call<List<AnswerDataRequest>>,
                    response: Response<List<AnswerDataRequest>>
                ) {
                    val answers = response.body()
                    if (!response.isSuccessful || answers == null) {
                        unansweredSurveys.value = surveys
                        return
                    }

                    val answeredIds = answers.map { it.surveyId }.toSet()
                    val notAnswered = surveys.filter { it.surveyId !in answeredIds }
                    unansweredSurveys.value = notAnswered
                }

                override fun onFailure(call: Call<List<AnswerDataRequest>>, t: Throwable) {
                    unansweredSurveys.value = surveys
                }
            })
    }

    fun submitAnswer(answer: AnswerDataRequest, token: String) {
        RetrofitInstance.surveyApi.submitAnswer(answer, "Bearer $token")
            .enqueue(object : Callback<AnswerDataRequest> {
                override fun onResponse(call: Call<AnswerDataRequest>, response: Response<AnswerDataRequest>) {
                    answerSubmitSuccess.value = response.isSuccessful
                }

                override fun onFailure(call: Call<AnswerDataRequest>, t: Throwable) {
                    answerSubmitSuccess.value = false
                }
            })
    }

    fun fetchMaxAnswerId(token: String) {
        val authHeader = "Bearer $token"
        RetrofitInstance.surveyApi.getMaxAnswerId(authHeader)
            .enqueue(object : Callback<Long> {
                override fun onResponse(call: Call<Long>, response: Response<Long>) {
                    if (response.isSuccessful) {
                        maxAnswerId.value = response.body()
                    } else {
                        Toast.makeText(getApplication(), "Failed to fetch max answer ID", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<Long>, t: Throwable) {
                    Toast.makeText(getApplication(), "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
    }
}
