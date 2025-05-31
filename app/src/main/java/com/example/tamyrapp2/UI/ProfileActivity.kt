/*package com.example.tamyrapp2.UI

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.tamyrapp2.R

class ProfileActivity : AppCompatActivity() {

    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var tvUserName: TextView
    private lateinit var tvUserEmail: TextView
    private lateinit var cancelButton: Button
    private lateinit var saveButton: Button
    private lateinit var profileImage: ImageView
    private lateinit var editAvatar: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        sharedPreferences = getSharedPreferences("auth_prefs", Context.MODE_PRIVATE)

        tvUserName = findViewById(R.id.tv_user_name)
        tvUserEmail = findViewById(R.id.tv_user_email)
        cancelButton = findViewById(R.id.cancelButton)
        saveButton = findViewById(R.id.saveButton)
        profileImage = findViewById(R.id.profile_image)
        editAvatar = findViewById(R.id.edit_avatar)

        loadUserData()

        cancelButton.setOnClickListener {
            finish() // Закрывает экран без сохранения изменений
        }

        saveButton.setOnClickListener {
            // Пока нет логики сохранения
            finish()
        }
    }

    private fun loadUserData() {
        val name = sharedPreferences.getString("user_name", "Имя не указано")
        val email = sharedPreferences.getString("user_email", "Email не указан")

        tvUserName.text = name
        tvUserEmail.text = email
    }
}
*/
/*
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
    private lateinit var editAge: EditText
    private lateinit var editSex: EditText
    private lateinit var editWeight: EditText
    private lateinit var editHeight: EditText
    private lateinit var saveButton: Button
    private lateinit var cancelButton: Button
    private lateinit var profileImage: ImageView
    private lateinit var editAvatar: ImageView

    private val viewModel: PersonalInfoViewModel by viewModels {
        ViewModelFactory(application)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        sharedPreferences = getSharedPreferences("auth_prefs", Context.MODE_PRIVATE)

        tvUserName = findViewById(R.id.tv_user_name)
        tvUserEmail = findViewById(R.id.tv_user_email)
        editAge = findViewById(R.id.edit_age)
        editSex = findViewById(R.id.edit_sex)
        editWeight = findViewById(R.id.edit_weight)
        editHeight = findViewById(R.id.edit_height)
        saveButton = findViewById(R.id.saveButton)
        cancelButton = findViewById(R.id.cancelButton)
        profileImage = findViewById(R.id.profile_image)
        editAvatar = findViewById(R.id.edit_avatar)

        loadUserInfo()

        saveButton.setOnClickListener {
            val age = editAge.text.toString().toIntOrNull() ?: 0
            val sex = editSex.text.toString()
            val weight = editWeight.text.toString().toIntOrNull() ?: 0
            val height = editHeight.text.toString().toIntOrNull() ?: 0
            viewModel.saveOrUpdatePersonalInfo(age, sex, weight, height)
        }

        cancelButton.setOnClickListener {
            finish()
        }

        observeViewModel()
    }

    override fun onResume() {
        super.onResume()
        viewModel.fetchPersonalInfo() // Предполагается, что метод реализован
    }

    private fun loadUserInfo() {
        val name = sharedPreferences.getString("user_name", "Имя не указано")
        val email = sharedPreferences.getString("user_email", "Email не указан")
        tvUserName.text = name
        tvUserEmail.text = email
    }

    private fun observeViewModel() {
        viewModel.success.observe(this, Observer { success ->
            if (success) {
                Toast.makeText(this, "Данные успешно сохранены", Toast.LENGTH_SHORT).show()
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
                editAge.setText(it.age.toString())
                editSex.setText(it.sex)
                editWeight.setText(it.weight.toString())
                editHeight.setText(it.height.toString())
            }
        })
    }
}
*/
/*
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

        val genderAdapter = ArrayAdapter.createFromResource(
            this,
            R.array.sex_options,
            android.R.layout.simple_spinner_item
        )
        genderAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        val sex = spinnerGender.selectedItem.toString()

        editWeight = findViewById(R.id.edit_weight)
        editHeight = findViewById(R.id.edit_height)
        saveButton = findViewById(R.id.saveButton)
        cancelButton = findViewById(R.id.cancelButton)
        profileImage = findViewById(R.id.profile_image)
        editAvatar = findViewById(R.id.edit_avatar)

        loadUserInfo()

        saveButton.setOnClickListener {
            val name = editName.text.toString()
            val surname = editSurname.text.toString()
            val age = editAge.text.toString().toIntOrNull() ?: 0
            val sex = spinnerGender.selectedItem.toString()
            val weight = editWeight.text.toString().toIntOrNull() ?: 0
            val height = editHeight.text.toString().toIntOrNull() ?: 0
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
        val name = sharedPreferences.getString("user_name", "Имя не указано")
        val email = sharedPreferences.getString("user_email", "Email не указан")
        tvUserName.text = name
        tvUserEmail.text = email
    }

    private fun observeViewModel() {
        viewModel.success.observe(this, Observer { success ->
            if (success) {
                Toast.makeText(this, "Данные успешно сохранены", Toast.LENGTH_SHORT).show()
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
                editAge.setText(it.age.toString())
                val genderPosition = (spinnerSex.adapter as ArrayAdapter<String>).getPosition(it.sex ?: "")
                spinnerSex.setSelection(genderPosition)
                editWeight.setText(it.weight.toString())
                editHeight.setText(it.height.toString())
            }
        })
    }
}
*/
/*
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

        val genderAdapter = ArrayAdapter.createFromResource(
            this,
            R.array.sex_options,
            android.R.layout.simple_spinner_item
        )
        genderAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerGender.adapter = genderAdapter

        loadUserInfo()

        saveButton.setOnClickListener {
            val name = editName.text.toString()
            val surname = editSurname.text.toString()
            val age = editAge.text.toString().toIntOrNull() ?: 0
            val sex = spinnerGender.selectedItem.toString()
            val weight = editWeight.text.toString().toIntOrNull() ?: 0
            val height = editHeight.text.toString().toIntOrNull() ?: 0
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
        val name = sharedPreferences.getString("user_name", "Имя не указано")
        val email = sharedPreferences.getString("user_email", "Email не указан")
        tvUserName.text = name
        tvUserEmail.text = email
    }

    private fun observeViewModel() {
        viewModel.success.observe(this, Observer { success ->
            if (success) {
                Toast.makeText(this, "Данные успешно сохранены", Toast.LENGTH_SHORT).show()
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
                editAge.setText(it.age.toString())
                val genderPosition = (spinnerGender.adapter as ArrayAdapter<String>).getPosition(it.sex ?: "")
                spinnerGender.setSelection(genderPosition)
                editWeight.setText(it.weight.toString())
                editHeight.setText(it.height.toString())
            }
        })
    }
}
*/
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

    private val viewModel: PersonalInfoViewModel by viewModels {
        ViewModelFactory(application)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        sharedPreferences = getSharedPreferences("auth_prefs", Context.MODE_PRIVATE)

        // Инициализация полей
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

        // Настройка спиннера
        val genderAdapter = ArrayAdapter.createFromResource(
            this,
            R.array.sex_options,
            android.R.layout.simple_spinner_item
        )
        genderAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerGender.adapter = genderAdapter

        loadUserInfo()

        saveButton.setOnClickListener {
            val name = editName.text.toString()
            val surname = editSurname.text.toString()
            val age = editAge.text.toString().toIntOrNull() ?: 0
            val sex = spinnerGender.selectedItem.toString()
            val weight = editWeight.text.toString().toIntOrNull() ?: 0
            val height = editHeight.text.toString().toIntOrNull() ?: 0
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
        val name = sharedPreferences.getString("user_name", "Имя не указано")
        val email = sharedPreferences.getString("user_email", "Email не указан")
        tvUserName.text = name
        tvUserEmail.text = email
    }

    private fun observeViewModel() {
        viewModel.success.observe(this, Observer { success ->
            if (success) {
                Toast.makeText(this, "Данные успешно сохранены", Toast.LENGTH_SHORT).show()
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
            }
        })
    }
}
