package com.example.tamyrapp2.UI

import android.os.Bundle
import android.widget.*
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.tamyrapp2.R
import com.example.tamyrapp2.presentation.lifestyle.LifestyleInfoViewModel

class LifestyleInfoActivity : AppCompatActivity() {

    private val viewModel: LifestyleInfoViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lifestyle_info)

        val switchSmoking = findViewById<Switch>(R.id.switch_smoking)
        val switchAlcohol = findViewById<Switch>(R.id.switch_alcohol)
        val switchExercise = findViewById<Switch>(R.id.switch_exercise)
        val spinnerFruitIntake = findViewById<Spinner>(R.id.spinner_fruit_intake)
        val buttonSave = findViewById<Button>(R.id.button_save_lifestyle)

        val fruitOptions = arrayOf("Low", "Moderate", "High")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, fruitOptions)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerFruitIntake.adapter = adapter

        buttonSave.setOnClickListener {
            val smokes = switchSmoking.isChecked
            val drinksAlcohol = switchAlcohol.isChecked
            val exercises = switchExercise.isChecked
            val fruitIntake = spinnerFruitIntake.selectedItem.toString()

            viewModel.saveLifestyleInfo(smokes, drinksAlcohol, exercises, fruitIntake)
            Toast.makeText(this, "Lifestyle Info Sent!", Toast.LENGTH_SHORT).show()
            finish()
        }
    }
}
