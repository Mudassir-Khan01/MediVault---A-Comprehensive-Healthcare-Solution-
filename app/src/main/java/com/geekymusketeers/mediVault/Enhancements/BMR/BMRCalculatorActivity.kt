package com.geekymusketeers.mediVault.Enhancements.BMR


import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.geekymusketeers.mediVault.R

class BMRCalculatorActivity : AppCompatActivity() {

    private lateinit var etAge: EditText
    private lateinit var etWeight: EditText
    private lateinit var etHeight: EditText
    private lateinit var radioGroupGender: RadioGroup
    private lateinit var radioGroupUnit: RadioGroup
    private lateinit var spinnerActivityLevel: Spinner
    private lateinit var btnCalculate: Button
    private lateinit var tvBMR: TextView
    private lateinit var tvTDEE: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bmr_calculator)

        etAge = findViewById(R.id.etAge)
        etWeight = findViewById(R.id.etWeight)
        etHeight = findViewById(R.id.etHeight)
        radioGroupGender = findViewById(R.id.radioGroupGender)
        radioGroupUnit = findViewById(R.id.radioGroupUnit)
        spinnerActivityLevel = findViewById(R.id.spinnerActivityLevel)
        btnCalculate = findViewById(R.id.btnCalculate)
        tvBMR = findViewById(R.id.tvBMR)
        tvTDEE = findViewById(R.id.tvTDEE)

        val activityLevels = arrayOf(
            "Sedentary (little to no exercise)",
            "Lightly Active (1-3 days/week)",
            "Moderately Active (3-5 days/week)",
            "Very Active (6-7 days/week)",
            "Super Active (hard exercise & job)"
        )

        val activityMultipliers = arrayOf(1.2, 1.375, 1.55, 1.725, 1.9)
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, activityLevels)
        spinnerActivityLevel.adapter = adapter

        btnCalculate.setOnClickListener {
            val age = etAge.text.toString().toIntOrNull()
            val weight = etWeight.text.toString().toDoubleOrNull()
            val height = etHeight.text.toString().toDoubleOrNull()

            if (age == null || weight == null || height == null || age <= 0 || weight <= 0 || height <= 0) {
                Toast.makeText(this, "Please enter valid inputs", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val gender = if (radioGroupGender.checkedRadioButtonId == R.id.radioMale) "male" else "female"
            val isMetric = radioGroupUnit.checkedRadioButtonId == R.id.radioMetric
            val activityIndex = spinnerActivityLevel.selectedItemPosition
            val activityMultiplier = activityMultipliers[activityIndex]

            val bmr = calculateBMR(weight, height, age, gender, isMetric)
            val tdee = bmr * activityMultiplier

            tvBMR.text = "BMR: ${bmr.toInt()} kcal/day"
            tvTDEE.text = "TDEE: ${tdee.toInt()} kcal/day"
        }
    }

    private fun calculateBMR(weight: Double, height: Double, age: Int, gender: String, isMetric: Boolean): Double {
        var weightKg = weight
        var heightCm = height

        if (!isMetric) {
            weightKg = weight * 0.453592 // Convert lbs to kg
            heightCm = height * 2.54 // Convert inches to cm
        }

        val baseBMR = (10 * weightKg) + (6.25 * heightCm) - (5 * age)
        return if (gender == "male") baseBMR + 5 else baseBMR - 161
    }
}
