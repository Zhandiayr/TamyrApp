package com.example.tamyrapp2.presentation.utils

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.tamyrapp2.data.network.personalinfo.PersonalInfoViewModel

class ViewModelFactory(private val application: Application) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PersonalInfoViewModel::class.java)) {
            return PersonalInfoViewModel(application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
