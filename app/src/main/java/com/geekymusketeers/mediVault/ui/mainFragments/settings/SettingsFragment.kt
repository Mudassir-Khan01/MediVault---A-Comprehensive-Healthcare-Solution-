package com.geekymusketeers.mediVault.ui.mainFragments.settings

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.geekymusketeers.mediVault.Enhancements.BMI.BMICalculatorActivity
import com.geekymusketeers.mediVault.Enhancements.BMR.BMRCalculatorActivity
import com.geekymusketeers.mediVault.Enhancements.ChatBot.chatBot
import com.geekymusketeers.mediVault.Enhancements.StepCounter.StepCounterActivity
import com.geekymusketeers.mediVault.Enhancements.WaterIntake.WaterTrackerActivity
import com.geekymusketeers.mediVault.R
import com.geekymusketeers.mediVault.base.ViewModelFactory
import com.geekymusketeers.mediVault.databinding.BottomsheetModalBinding
import com.geekymusketeers.mediVault.ui.mainFragments.settings.prescription.AddPrescriptionActivity
import com.geekymusketeers.mediVault.ui.auth.signInScreen.SignInScreen
import com.geekymusketeers.mediVault.databinding.FragmentSettingsBinding
import com.geekymusketeers.mediVault.model.SettingsItem
import com.geekymusketeers.mediVault.model.SettingsState
import com.geekymusketeers.mediVault.ui.adapter.SettingsAdapter
import com.geekymusketeers.mediVault.ui.mainFragments.settings.upi.UPImanager
import com.geekymusketeers.mediVault.ui.profile.ProfileActivity
import com.geekymusketeers.mediVault.utils.Constants
import com.geekymusketeers.mediVault.utils.DialogUtil.createBottomSheet
import com.geekymusketeers.mediVault.utils.DialogUtil.setBottomSheet
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.util.ArrayList

class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!
    private var settingsItemList: MutableList<SettingsItem> = mutableListOf()
    private val settingsViewModel by viewModels<SettingsViewModel> { ViewModelFactory() }
    private lateinit var settingsItemAdapter: SettingsAdapter
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        sharedPreferences = requireActivity().getSharedPreferences(Constants.UserData, Context.MODE_PRIVATE)

        initObservers()
        handleOperations()

        return binding.root
    }

    private fun initObservers() {
        settingsViewModel.run {
            getDataFromSharedPreferences(sharedPreferences)
            allDataDeletedLiveData.observe(viewLifecycleOwner) {
                logoutFun()
            }
        }
    }

    private fun handleOperations() {
        setupSettings()
    }

    private fun setupSettings() {
        settingsItemList.apply {
            add(SettingsItem(0, R.drawable.edit_profile, getString(R.string.edit_profile)))
            add(SettingsItem(1, R.drawable.upload_prescription, getString(R.string.upload_prescription)))
            add(SettingsItem(2, R.drawable.upi_id, getString(R.string.upi_qr)))
            add(SettingsItem(3, R.drawable.bmi, getString(R.string.BMI)))
            add(SettingsItem(4, R.drawable.bmr, getString(R.string.BMR)))
            add(SettingsItem(5, R.drawable.waterintake, getString(R.string.WaterIntake)))
            add(SettingsItem(6, R.drawable.chatbotlogo, getString(R.string.chatbot)))
            add(SettingsItem(7, R.drawable.logout, getString(R.string.logout)))
        }

        settingsItemAdapter =
            SettingsAdapter(requireContext(), settingsItemList as ArrayList<SettingsItem>) {

                settingsItemList.clear()

                when (it) {
                    SettingsState.getSettingsState(SettingsState.TO_EDIT_PROFILE) -> {
                        startActivity(Intent(requireActivity(), ProfileActivity::class.java))
//                        findNavController().navigate(R.id.action_settings_to_profileFragment)
                    }

                    SettingsState.getSettingsState(SettingsState.TO_UPLOAD_PRESCRIPTION) -> {
                        navigateToUploadPrescription()
                    }

                    SettingsState.getSettingsState(SettingsState.TO_UPI_QR) -> {
                        startActivity(Intent(requireActivity(), UPImanager::class.java))
                    }

                    SettingsState.getSettingsState(SettingsState.BMI) -> {
                        BMI()
                    }

                    SettingsState.getSettingsState(SettingsState.BMR) -> {
                        BMR()
                    }

                    SettingsState.getSettingsState(SettingsState.WaterIntake) -> {
                        waterIntake()
                    }
                    SettingsState.getSettingsState(SettingsState.CHAT_BOT) -> {
                        openchatbot()
                    }

                    SettingsState.getSettingsState(SettingsState.TO_LOGOUT) -> {
                        settingsViewModel.deleteAllDataFromSharedPreferences(sharedPreferences)
                    }
                }
            }

        binding.settingsList.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = settingsItemAdapter
        }
    }

    private fun navigateToUploadPrescription() {
        startActivity(Intent(requireActivity(), AddPrescriptionActivity::class.java))
        FirebaseDatabase.getInstance().reference.child(Constants.Users)
            .child(settingsViewModel.userLiveData.value!!.UID.toString()).child(Constants.Prescription)
            .addValueEventListener(object :
                ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val presURL = snapshot.child("fileurl").value.toString().trim()
                    val editor = sharedPreferences.edit()
                    editor.putString("prescription", presURL)
                    editor.apply()
                }

                override fun onCancelled(error: DatabaseError) {}
            })
    }
    private fun openchatbot() {
        startActivity(Intent(requireActivity(), chatBot::class.java))
    }

    private fun waterIntake() {
        startActivity(Intent(requireActivity(), WaterTrackerActivity::class.java))

//        val dialog = BottomsheetModalBinding.inflate(layoutInflater)
//        val bottomSheet = requireActivity().createBottomSheet()
//        dialog.apply {
//            paragraphContent.text = getString(R.string.needHelpDescription)
//            lottieAnimationLayout.apply {
//                setAnimation(R.raw.help_lottie)
//                visibility = View.VISIBLE
//            }
//        }
//        dialog.root.setBottomSheet(bottomSheet)
    }

    private fun BMI() {
        startActivity(Intent(requireActivity(), BMICalculatorActivity::class.java))
//        val aboutUsDialog = BottomsheetModalBinding.inflate(layoutInflater)
//        val aboutUsBottomSheet = requireActivity().createBottomSheet()
//        aboutUsDialog.apply {
//            paragraphHeading.text = getString(R.string.welcome_to_medify)
//            paragraphContent.text = getString(R.string.description)
//        }
//        aboutUsDialog.root.setBottomSheet(aboutUsBottomSheet)
    }

    private fun BMR() {
        startActivity(Intent(requireActivity(), BMRCalculatorActivity::class.java))
//        val intent = Intent(Intent.ACTION_VIEW)
//        intent.data = Uri.parse(requireContext().resources.getString(R.string.mailTo))
//        startActivity(intent)
    }

    @SuppressLint("CommitPrefEdits")
    private fun logoutFun() {

        FirebaseAuth.getInstance().signOut()
        val intent = Intent(context, SignInScreen::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        startActivity(intent)
        requireActivity().finish()
    }
}