package com.geekymusketeers.mediVault.Enhancements.BMI

import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.geekymusketeers.mediVault.R

class BMICalculatorActivity : AppCompatActivity() {

    private lateinit var etWeight: EditText
    private lateinit var etHeight: EditText
    private lateinit var btnCalculate: Button
    private lateinit var tvBMIResult: TextView
    private lateinit var progressBMI: ProgressBar
    private lateinit var rbMetric: RadioButton
    private lateinit var rbImperial: RadioButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bmi_calculator)

        etWeight = findViewById(R.id.et_weight)
        etHeight = findViewById(R.id.et_height)
        btnCalculate = findViewById(R.id.btn_calculate)
        tvBMIResult = findViewById(R.id.tv_bmi_result)
        progressBMI = findViewById(R.id.progress_bmi)
        rbMetric = findViewById(R.id.rb_metric)
        rbImperial = findViewById(R.id.rb_imperial)

        btnCalculate.setOnClickListener {
            calculateBMI()
        }
    }

    private fun calculateBMI() {
        val weightText = etWeight.text.toString()
        val heightText = etHeight.text.toString()

        if (weightText.isEmpty() || heightText.isEmpty()) {
            Toast.makeText(this, "Please enter weight and height", Toast.LENGTH_SHORT).show()
            return
        }

        val weight = weightText.toDouble()
        val height = heightText.toDouble()

        if (weight <= 0 || height <= 0) {
            Toast.makeText(this, "Invalid input values", Toast.LENGTH_SHORT).show()
            return
        }

        val bmi = if (rbMetric.isChecked) {
            // Metric: weight (kg) / height^2 (m^2)
            val heightMeters = height / 100
            weight / (heightMeters * heightMeters)
        } else {
            // Imperial: (weight (lbs) * 703) / (height (in)^2)
            (weight * 703) / (height * height)
        }

        val bmiRounded = String.format("%.1f", bmi).toDouble()
        val category = getBMICategory(bmiRounded)

        tvBMIResult.text = "BMI: $bmiRounded ($category)"
        tvBMIResult.visibility = View.VISIBLE
        progressBMI.visibility = View.VISIBLE

        updateProgressBar(bmiRounded)
    }

    private fun getBMICategory(bmi: Double): String {
        return when {
            bmi < 18.5 -> "Underweight"
            bmi < 25.0 -> "Normal weight"
            bmi < 30.0 -> "Overweight"
            else -> "Obese"
        }
    }

    private fun updateProgressBar(bmi: Double) {
        val progress = when {
            bmi < 18.5 -> 25
            bmi < 25.0 -> 50
            bmi < 30.0 -> 75
            else -> 100
        }
        progressBMI.progress = progress
    }
}
