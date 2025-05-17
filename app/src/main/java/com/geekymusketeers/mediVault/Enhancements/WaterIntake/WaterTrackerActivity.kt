package com.geekymusketeers.mediVault.Enhancements.WaterIntake

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.os.Bundle
import android.view.animation.AccelerateInterpolator
import android.view.animation.BounceInterpolator
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.geekymusketeers.mediVault.R
import kotlin.math.min

class WaterTrackerActivity : AppCompatActivity() {

    private var goal = 2000 // Default Goal in ml
    private var intake = 0
    private var goalReached = false

    private lateinit var progressBar: ProgressBar
    private lateinit var tvWaterIntake: TextView
    private lateinit var btnReset: Button
    private lateinit var btnChangeGoal: Button
    private lateinit var etCustomAmount: EditText
    private lateinit var btnAddCustom: Button
    private lateinit var welcomeText: TextView

    private val waterAmounts = mapOf(
        R.id.btnSmallGlass to 200,
        R.id.btnMediumGlass to 300,
        R.id.btnLargeGlass to 500,
        R.id.btnBottle to 750
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_water_tracker)

        // Initialize UI Elements
        progressBar = findViewById(R.id.progressBar)
        tvWaterIntake = findViewById(R.id.tvWaterIntake)
        btnReset = findViewById(R.id.btnReset)
        btnChangeGoal = findViewById(R.id.btnChangeGoal)
        etCustomAmount = findViewById(R.id.etCustomAmount)
        btnAddCustom = findViewById(R.id.btnAddCustom)
        welcomeText = findViewById(R.id.tvHeader)

        // Set click listeners for water buttons
        waterAmounts.forEach { (buttonId, amount) ->
            findViewById<Button>(buttonId).setOnClickListener { addWater(amount) }
        }

        btnAddCustom.setOnClickListener {
            val customAmount = etCustomAmount.text.toString().toIntOrNull()
            if (customAmount != null && customAmount > 0) {
                addWater(customAmount)
                etCustomAmount.text.clear()
            } else {
                showToast("Enter a valid amount")
            }
        }

        btnReset.setOnClickListener { resetIntake() }

        btnChangeGoal.setOnClickListener {
            goal = if (goal == 2000) 3000 else 2000
            btnChangeGoal.text = "Set Goal (${goal / 1000}L)"
            updateUI()
            showToast("Goal updated to ${goal}ml")
            goalReached = false
        }

        updateUI()
    }

    private fun addWater(amount: Int) {
        intake += amount
        updateUI()
        showToast("Added $amount ml")

        // Check if goal was reached
        if (intake >= goal && !goalReached) {
            goalReached = true
            celebrateGoalAchievement()
        }
    }

    private fun resetIntake() {
        intake = 0
        goalReached = false
        updateUI()
        showToast("Water intake reset")
        resetCelebrationEffects()
    }

    private fun updateUI() {
        val progress = min(100, (intake.toFloat() / goal * 100).toInt())
        progressBar.progress = progress
        tvWaterIntake.text = "$intake / $goal ml"

        // Change color when approaching goal
        if (progress >= 90) {
            progressBar.progressTintList = ContextCompat.getColorStateList(this, R.color.colorAccent)
        } else {
            progressBar.progressTintList = ContextCompat.getColorStateList(this, R.color.dull_blue)
        }
    }

    private fun celebrateGoalAchievement() {
        // 1. Show congratulatory message
        welcomeText.text = "Congratulations!\nGoal Achieved!"
        welcomeText.setTextColor(ContextCompat.getColor(this, R.color.colorAccent))

        // 2. Bounce animation for the progress text
        val bounceX = ObjectAnimator.ofFloat(tvWaterIntake, "scaleX", 1f, 1.2f, 1f)
        val bounceY = ObjectAnimator.ofFloat(tvWaterIntake, "scaleY", 1f, 1.2f, 1f)
        bounceX.duration = 500
        bounceY.duration = 500

        val animatorSet = AnimatorSet()
        animatorSet.playTogether(bounceX, bounceY)
        animatorSet.interpolator = BounceInterpolator()
        animatorSet.start()

        // 3. Confetti effect (simplified)
        val confettiAnimation = ObjectAnimator.ofFloat(welcomeText, "rotation", 0f, 5f, -5f, 0f)
        confettiAnimation.duration = 1000
        confettiAnimation.repeatCount = 3
        confettiAnimation.start()

        // 4. Show special toast
        showToast("🎉 Great job! You reached your daily goal! 🎉")

        // 5. Pulse effect for the progress bar
        val pulse = ObjectAnimator.ofFloat(progressBar, "scaleX", 1f, 1.05f, 1f)
        pulse.duration = 1000
        pulse.repeatCount = 3
        pulse.interpolator = AccelerateInterpolator()
        pulse.start()
    }

    private fun resetCelebrationEffects() {
        welcomeText.text = "Water Intake"
        welcomeText.setTextColor(ContextCompat.getColor(this, R.color.light_blue))
        progressBar.progressTintList = ContextCompat.getColorStateList(this, R.color.dull_blue)
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}