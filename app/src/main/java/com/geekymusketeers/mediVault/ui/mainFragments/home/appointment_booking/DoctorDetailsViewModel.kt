package com.geekymusketeers.mediVault.ui.mainFragments.home.appointment_booking

import android.app.Application
import com.geekymusketeers.mediVault.base.BaseViewModel
import com.geekymusketeers.mediVault.model.User

class DoctorDetailsViewModel(application: Application) : BaseViewModel(application) {

    private var doctor: User = User()

    fun initDoctor(doctorDetails: User) {
        doctor = doctorDetails
    }

    fun getDoctor(): User {
        return doctor
    }

}
