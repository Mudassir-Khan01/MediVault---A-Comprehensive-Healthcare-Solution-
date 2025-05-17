package com.geekymusketeers.mediVault.ui.mainFragments.stats

import android.app.Application
import android.content.SharedPreferences
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.geekymusketeers.mediVault.base.BaseViewModel
import com.geekymusketeers.mediVault.model.HealthData
import com.geekymusketeers.mediVault.model.TestResult
import com.geekymusketeers.mediVault.model.User
import com.geekymusketeers.mediVault.utils.Constants
import com.geekymusketeers.mediVault.utils.DateTimeExtension
import com.geekymusketeers.mediVault.utils.Logger
import com.geekymusketeers.mediVault.utils.SharedPrefsExtension.getUserFromSharedPrefs
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AddStatsDataViewModel(application: Application) : BaseViewModel(application) {

    var healthData = MutableLiveData<HealthData>()
    var testName = MutableLiveData<String>()
    var healthId = MutableLiveData<String>()
    var testResult = MutableLiveData<String>()
    var statsList = MutableLiveData<MutableList<TestResult>>()
    var enableButton = MutableLiveData<Boolean>()
    var isDataSaved = MutableLiveData<Boolean>()
    var userLiveData = MutableLiveData<User>()
    var errorLiveData = MutableLiveData<String>()

    // Add predefined test names
    val predefinedTestNames = listOf(
        "Blood Pressure",
        "Pulse Rate",
        "Oxygen Level",
        "Blood Sugar",
        "Body Temperature",
        "Weight",
        "Height",
        "BMI"
    )

    init {
        healthData.value = HealthData()
    }

    fun setHealthData(healthData: HealthData) {
        this.healthData.value = healthData
        this.healthId.value = healthData.healthId
        this.testName.value = healthData.name
        this.statsList.value = healthData.tests as MutableList<TestResult>
    }

    fun setHealthId() {
        this.healthId.value = DateTimeExtension.getTimeStamp()
        updateButtonState()
    }

    fun setTestResult(testResult: String) {
        this.testResult.value = testResult
        updateButtonState()
    }

    fun setTestName(testName: String) {
        this.testName.value = testName
        updateButtonState()
    }

    fun saveDataInFirebase() {
        val timeStamp = DateTimeExtension.getTimeStamp()
        val result = TestResult(testResult.value, timeStamp)

        if (statsList.value == null) {
            statsList.value = mutableListOf()
        }

        statsList.value!!.let {
            it.add(result)
            if (it.size > 28) {
                it.removeAt(0)
            }
        }

        Logger.debugLog("StatsList ${statsList.value}")

        val healthData = HealthData(
            testName.value,
            statsList.value,
            healthId.value
        )

        this.healthData.value = healthData
        Logger.debugLog("HealthData $healthData")

        pushIntoFirebase()
    }

    private fun pushIntoFirebase() = viewModelScope.launch(Dispatchers.IO) {
        val userId = userLiveData.value?.UID
        FirebaseDatabase.getInstance().reference.child(Constants.Users).child(userId!!)
            .child(Constants.HealthData).child(healthId.value!!).setValue(healthData.value)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    Logger.debugLog("Data saved successfully")
                    isDataSaved.postValue(true)
                } else {
                    Logger.debugLog("Data not saved")
                    errorLiveData.postValue("Data not saved")
                }
            }.addOnFailureListener {
                Logger.debugLog("Data not saved, called on Failure")
                errorLiveData.postValue("Data not saved, called on Failure")
            }
    }

    private fun updateButtonState() {
        val requiredField =
            testResult.value.isNullOrEmpty() ||
                    testName.value.isNullOrEmpty() ||
                    healthId.value.isNullOrEmpty()
        enableButton.value = requiredField.not()
    }

    fun saveDataFromSharedPreferences(sharedPreferences: SharedPreferences) =
        viewModelScope.launch(Dispatchers.IO) {
            userLiveData.postValue(sharedPreferences.getUserFromSharedPrefs())
        }
}