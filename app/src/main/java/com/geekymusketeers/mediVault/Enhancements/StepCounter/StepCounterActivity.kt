package com.geekymusketeers.mediVault.Enhancements.StepCounter


import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.geekymusketeers.mediVault.R
import kotlin.math.roundToInt
import kotlin.random.Random

class StepCounterActivity : AppCompatActivity() {
    private lateinit var tvStepCount: TextView
    private lateinit var tvProgress: TextView
    private lateinit var progressBar: ProgressBar
    private lateinit var btnStartPause: Button
    private lateinit var btnReset: Button
    private lateinit var tvCalories: TextView
    private lateinit var tvDistance: TextView
    private lateinit var tvActiveTime: TextView

    private var stepCount = 0
    private var stepGoal = 10000
    private var isTracking = false
    private var stepRate = 90 // Default 90 steps per minute
    private val handler = Handler(Looper.getMainLooper())

    private val stepRunnable = object : Runnable {
        override fun run() {
            if (isTracking) {
                val newSteps = Random.nextInt(1, 5)
                stepCount += newSteps
                updateUI()
                handler.postDelayed(this, 3000) // Simulates steps every 3 seconds
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_step_counter)

        tvStepCount = findViewById(R.id.tvStepCount)
        tvProgress = findViewById(R.id.tvProgress)
        progressBar = findViewById(R.id.progressBar)
        btnStartPause = findViewById(R.id.btnStartPause)
        btnReset = findViewById(R.id.btnReset)
        tvCalories = findViewById(R.id.tvCalories)
        tvDistance = findViewById(R.id.tvDistance)
        tvActiveTime = findViewById(R.id.tvActiveTime)

        btnStartPause.setOnClickListener {
            toggleTracking()
        }

        btnReset.setOnClickListener {
            resetSteps()
        }
    }

    private fun toggleTracking() {
        isTracking = !isTracking
        btnStartPause.text = if (isTracking) "Pause Tracking" else "Start Tracking"

        if (isTracking) {
            handler.post(stepRunnable)
        } else {
            handler.removeCallbacks(stepRunnable)
        }
    }

    private fun resetSteps() {
        stepCount = 0
        stepRate = 90
        updateUI()
    }

    private fun updateUI() {
        tvStepCount.text = stepCount.toString()
        val progress = ((stepCount.toDouble() / stepGoal) * 100).coerceAtMost(100.0).roundToInt()
        progressBar.progress = progress
        tvProgress.text = "$progress% of daily goal"

        val calories = calculateCalories(stepCount)
        val distance = calculateDistance(stepCount)
        val activeTime = estimateActiveTime(stepCount, stepRate)

        tvCalories.text = "Calories Burned: $calories"
        tvDistance.text = "Distance: %.1f mi".format(distance)
        tvActiveTime.text = "Active Time: ${formatTime(activeTime)}"
    }

    private fun calculateCalories(steps: Int): Int {
        return ((steps / 2000.0) * 100).roundToInt()
    }

    private fun calculateDistance(steps: Int): Double {
        return steps / 2000.0
    }

    private fun estimateActiveTime(steps: Int, rate: Int): Int {
        return if (rate > 0) steps / rate else 0
    }

    private fun formatTime(minutes: Int): String {
        val hrs = minutes / 60
        val mins = minutes % 60
        return "${hrs}h ${mins}m"
    }
}
