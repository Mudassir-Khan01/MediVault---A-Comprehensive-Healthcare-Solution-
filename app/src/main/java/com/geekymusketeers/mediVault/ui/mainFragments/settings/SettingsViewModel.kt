package com.geekymusketeers.mediVault.ui.mainFragments.settings

import android.app.Application
import android.content.SharedPreferences
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.geekymusketeers.mediVault.base.BaseViewModel
import com.geekymusketeers.mediVault.model.User
import com.geekymusketeers.mediVault.utils.SharedPrefsExtension.getUserFromSharedPrefs
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class SettingsViewModel(application: Application) : BaseViewModel(application) {

    var userLiveData = MutableLiveData<User>()
    var allDataDeletedLiveData = MutableLiveData<Boolean>()

    fun getDataFromSharedPreferences(sharedPreferences: SharedPreferences) =
        viewModelScope.launch(Dispatchers.IO) {
            userLiveData.postValue(sharedPreferences.getUserFromSharedPrefs())
        }

    fun deleteAllDataFromSharedPreferences(sharedPreferences: SharedPreferences) =
        viewModelScope.launch(Dispatchers.IO) {
            sharedPreferences.edit().clear().apply()
            allDataDeletedLiveData.postValue(true)
        }

}