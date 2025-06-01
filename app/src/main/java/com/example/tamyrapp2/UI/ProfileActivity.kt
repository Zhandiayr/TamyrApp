package com.example.tamyrapp2.UI

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.*
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.example.tamyrapp2.R
import com.example.tamyrapp2.data.network.personalinfo.PersonalInfoViewModel
import com.example.tamyrapp2.presentation.utils.ViewModelFactory

class ProfileActivity : AppCompatActivity() {

    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var tvUserName: TextView
    private lateinit var tvUserEmail: TextView
    private lateinit var editName: EditText
    private lateinit var editSurname: EditText
    private lateinit var editAge: EditText
    private lateinit var spinnerGender: Spinner
    private lateinit var editWeight: EditText
    private lateinit var editHeight: EditText
    private lateinit var saveButton: Button
    private lateinit var cancelButton: Button
    private lateinit var profileImage: ImageView
    private lateinit var editAvatar: ImageView
    private lateinit var arrowName: ImageView
    private lateinit var arrowSurname: ImageView
    private lateinit var arrowAge: ImageView
    private lateinit var arrowGender: ImageView
    private lateinit var arrowWeight: ImageView
    private lateinit var arrowHeight: ImageView

    private val viewModel: PersonalInfoViewModel by viewModels {
        ViewModelFactory(application)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        sharedPreferences = getSharedPreferences("auth_prefs", Context.MODE_PRIVATE)

        tvUserName = findViewById(R.id.tv_user_name)
        tvUserEmail = findViewById(R.id.tv_user_email)
        editName = findViewById(R.id.edit_name)
        editSurname = findViewById(R.id.edit_surname)
        editAge = findViewById(R.id.edit_age)
        spinnerGender = findViewById(R.id.spinner_gender)
        editWeight = findViewById(R.id.edit_weight)
        editHeight = findViewById(R.id.edit_height)
        saveButton = findViewById(R.id.saveButton)
        cancelButton = findViewById(R.id.cancelButton)
        profileImage = findViewById(R.id.profile_image)
        editAvatar = findViewById(R.id.edit_avatar)

        arrowName = findViewById(R.id.arrow_name)
        arrowSurname = findViewById(R.id.arrow_surname)
        arrowAge = findViewById(R.id.arrow_age)
        arrowGender = findViewById(R.id.arrow_gender)
        arrowWeight = findViewById(R.id.arrow_weight)
        arrowHeight = findViewById(R.id.arrow_height)

        val genderAdapter = ArrayAdapter.createFromResource(
            this,
            R.array.sex_options,
            android.R.layout.simple_spinner_item
        )
        genderAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerGender.adapter = genderAdapter

        setEditability(false)

        arrowName.setOnClickListener { toggleEdit(editName) }
        arrowSurname.setOnClickListener { toggleEdit(editSurname) }
        arrowAge.setOnClickListener { toggleEdit(editAge) }
        arrowGender.setOnClickListener { spinnerGender.isEnabled = !spinnerGender.isEnabled }
        arrowWeight.setOnClickListener { toggleEdit(editWeight) }
        arrowHeight.setOnClickListener { toggleEdit(editHeight) }

        loadUserInfo()

        saveButton.setOnClickListener {
            val name = editName.text.toString()
            val surname = editSurname.text.toString()
            val age = editAge.text.toString().toIntOrNull()
            val sex = spinnerGender.selectedItem.toString()
            val weight = editWeight.text.toString().toIntOrNull()
            val height = editHeight.text.toString().toIntOrNull()

            if (name.isBlank() || surname.isBlank()) {
                Toast.makeText(this, "Please enter your name and surname", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (age == null || age !in 1..120) {
                Toast.makeText(this, "Age must be between 1 and 120", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (weight == null || weight !in 10..500) {
                Toast.makeText(this, "Weight must be between 10 and 500 kg", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (height == null || height !in 50..250) {
                Toast.makeText(this, "Height must be between 50 and 250 cm", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            viewModel.saveOrUpdatePersonalInfo(name, surname, age, sex, weight, height)
        }

        cancelButton.setOnClickListener {
            finish()
        }

        observeViewModel()
    }

    override fun onResume() {
        super.onResume()
        viewModel.fetchPersonalInfo()
    }

    private fun loadUserInfo() {
        val name = sharedPreferences.getString("user_name", "Name not specified")
        val email = sharedPreferences.getString("user_email", "Email not specified")
        tvUserName.text = name
        tvUserEmail.text = email
    }

    private fun toggleEdit(editText: EditText) {
        val enabled = !editText.isEnabled
        editText.isEnabled = enabled
        editText.isFocusable = enabled
        editText.isFocusableInTouchMode = enabled
        editText.isClickable = enabled
        editText.isCursorVisible = enabled
        editText.isLongClickable = enabled

        if (enabled) {
            editText.requestFocus()
            editText.setSelection(editText.text.length)  // Курсор в конец текста
        }
    }

    private fun setEditability(enabled: Boolean) {
        editName.isEnabled = enabled
        editSurname.isEnabled = enabled
        editAge.isEnabled = enabled
        spinnerGender.isEnabled = enabled
        editWeight.isEnabled = enabled
        editHeight.isEnabled = enabled
    }

    private fun observeViewModel() {
        viewModel.success.observe(this, Observer { success ->
            if (success) {
                Toast.makeText(this, "Data saved successfully", Toast.LENGTH_SHORT).show()
                finish()
            }
        })

        viewModel.error.observe(this, Observer { errorMsg ->
            errorMsg?.let {
                Toast.makeText(this, it, Toast.LENGTH_LONG).show()
            }
        })

        viewModel.personalInfo.observe(this, Observer { info ->
            info?.let {
                editName.setText(it.name)
                editSurname.setText(it.surname)
                editAge.setText(it.age?.toString() ?: "")
                val genderPosition = (spinnerGender.adapter as ArrayAdapter<String>)
                    .getPosition(it.sex ?: "")
                spinnerGender.setSelection(genderPosition)
                editWeight.setText(it.weight?.toString() ?: "")
                editHeight.setText(it.height?.toString() ?: "")

                sharedPreferences.edit().putInt("user_age", it.age ?: -1).apply()
            }
        })

    }
}
