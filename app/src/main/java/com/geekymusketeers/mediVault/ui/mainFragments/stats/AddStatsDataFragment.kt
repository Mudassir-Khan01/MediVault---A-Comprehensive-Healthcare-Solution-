package com.geekymusketeers.mediVault.ui.mainFragments.stats

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.geekymusketeers.mediVault.base.ViewModelFactory
import com.geekymusketeers.mediVault.databinding.FragmentAddStatsDataBinding
import com.geekymusketeers.mediVault.utils.Constants
import com.geekymusketeers.mediVault.utils.Logger

class AddStatsDataFragment : Fragment() {

    private var _binding: FragmentAddStatsDataBinding? = null
    private val args by navArgs<AddStatsDataFragmentArgs>()
    private val addStatsDataViewModel by viewModels<AddStatsDataViewModel> { ViewModelFactory() }
    private lateinit var sharedPreferences: SharedPreferences
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddStatsDataBinding.inflate(inflater, container, false)
        sharedPreferences = requireActivity().getSharedPreferences(Constants.UserData, Context.MODE_PRIVATE)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initObserver()
        initView()
    }

    private fun initObserver() {
        addStatsDataViewModel.run {
            saveDataFromSharedPreferences(sharedPreferences)
            if (args.stats.healthId != null) {
                Logger.debugLog("AddStatsDataFragment HealthId: ${args.stats}")
                setHealthData(args.stats)
                // Set the selected item in spinner
                val position = predefinedTestNames.indexOf(args.stats.name)
                if (position >= 0) {
                    binding.testNameSpinner.setSelection(position)
                }
            } else {
                setHealthId()
            }

            enableButton.observe(viewLifecycleOwner) {
                binding.saveButton.isEnabled = it
                binding.saveButton.setButtonEnabled(it)
            }

            isDataSaved.observe(viewLifecycleOwner) {
                if (it) {
                    Toast.makeText(context, "Data saved", Toast.LENGTH_SHORT).show()
                    findNavController().navigateUp()
                }
            }
        }
    }

    private fun initView() {
        // Set up the spinner adapter
        val adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            addStatsDataViewModel.predefinedTestNames
        ).apply {
            setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        }

        binding.testNameSpinner.adapter = adapter

        // Handle item selection
        binding.testNameSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                addStatsDataViewModel.setTestName(addStatsDataViewModel.predefinedTestNames[position])
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Do nothing
            }
        }

        binding.testResultEditText.setUserInputListener {
            addStatsDataViewModel.setTestResult(it)
        }

        binding.saveButton.setOnClickListener {
            addStatsDataViewModel.saveDataInFirebase()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}