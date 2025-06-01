package com.example.tamyrapp2.UI

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.tamyrapp2.R
import com.example.tamyrapp2.data.network.RetrofitInstance
import com.example.tamyrapp2.data.network.survey.AnswerDataRequest
import com.example.tamyrapp2.data.network.survey.SurveyDataRequest
import org.json.JSONArray
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*

class SurveyActivity : AppCompatActivity() {

    private lateinit var surveyTitle: TextView
    private lateinit var questionLayout: LinearLayout
    private lateinit var submitButton: Button

    private val answerMap = mutableMapOf<String, String>()
    private var currentSurveyId: Long = -1L
    private var currentUserId: Long = -1L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_survey)

        surveyTitle = findViewById(R.id.tv_survey_title)
        questionLayout = findViewById(R.id.question_layout)
        submitButton = findViewById(R.id.btn_submit)

        currentSurveyId = intent.getLongExtra("surveyId", -1L)
        val prefs = getSharedPreferences("auth_prefs", MODE_PRIVATE)
        val token = prefs.getString("access_token", null) ?: return
        currentUserId = prefs.getLong("user_id", -1L)

        fetchSurveyById(currentSurveyId, token)

        submitButton.setOnClickListener {
            if (answerMap.values.any { it.isEmpty() }) {
                Toast.makeText(this, "Please answer all questions", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            submitAnswers(currentSurveyId, currentUserId, answerMap, token)
        }
    }

    private fun fetchSurveyById(surveyId: Long, token: String) {
        RetrofitInstance.surveyApi.getSurveyById(surveyId, "Bearer $token")
            .enqueue(object : Callback<SurveyDataRequest> {
                override fun onResponse(call: Call<SurveyDataRequest>, response: Response<SurveyDataRequest>) {
                    if (response.isSuccessful) {
                        val survey = response.body()
                        survey?.let {
                            surveyTitle.text = it.surveyDescription
                            parseAndDisplayQuestions(it.questionsAnswersVariants)
                        }
                    } else {
                        Toast.makeText(this@SurveyActivity, "Failed to load survey", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<SurveyDataRequest>, t: Throwable) {
                    Toast.makeText(this@SurveyActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
    }

    private fun parseAndDisplayQuestions(jsonString: String) {
        try {
            val jsonArray = JSONArray(jsonString)
            for (i in 0 until jsonArray.length()) {
                val item = jsonArray.getJSONObject(i)
                val question = item.optString("question")
                val options = item.getJSONArray("options")

                val questionText = TextView(this).apply {
                    text = question
                    textSize = 16f
                    setPadding(0, 12, 0, 4)
                }
                questionLayout.addView(questionText)

                val radioGroup = RadioGroup(this)
                for (j in 0 until options.length()) {
                    val option = options.getString(j)
                    val radioButton = RadioButton(this).apply {
                        text = option
                        textSize = 14f
                    }
                    radioGroup.addView(radioButton)
                }

                radioGroup.setOnCheckedChangeListener { group, checkedId ->
                    val selected = group.findViewById<RadioButton>(checkedId)
                    answerMap[question] = selected?.text.toString()
                }

                answerMap[question] = ""
                questionLayout.addView(radioGroup)
            }
        } catch (e: Exception) {
            Toast.makeText(this, "Failed to parse questions: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun submitAnswers(surveyId: Long, userId: Long, answers: Map<String, String>, token: String) {
        val jsonAnswer = JSONObject()
        for ((question, answer) in answers) {
            jsonAnswer.put(question, answer)
        }

        val date = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        val request = AnswerDataRequest(
            surveyId = surveyId,
            userId = userId,
            date = date,
            answer = jsonAnswer.toString()
        )


        RetrofitInstance.surveyApi.submitAnswer(request, "Bearer $token")
            .enqueue(object : Callback<AnswerDataRequest> {
                override fun onResponse(call: Call<AnswerDataRequest>, response: Response<AnswerDataRequest>) {
                    if (response.isSuccessful) {
                        Toast.makeText(this@SurveyActivity, "Answers submitted!", Toast.LENGTH_SHORT).show()
                        finish()
                    } else {
                        Toast.makeText(this@SurveyActivity, "Failed to submit answers", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<AnswerDataRequest>, t: Throwable) {
                    Toast.makeText(this@SurveyActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
    }
}
