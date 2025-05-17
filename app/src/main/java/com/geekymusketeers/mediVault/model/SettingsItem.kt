package com.geekymusketeers.mediVault.model

data class SettingsItem(
    val itemID: Int,
    val drawableInt: Int,
    val itemName: String
)

enum class SettingsState {
    TO_EDIT_PROFILE,
    TO_UPLOAD_PRESCRIPTION,
    TO_UPI_QR,
    BMI,
    BMR,
    WaterIntake,
    CHAT_BOT,
    TO_LOGOUT,
    ;

    companion object {
        fun getSettingsState(state: SettingsState) : Int {
            return when (state) {
                TO_EDIT_PROFILE -> 0
                TO_UPLOAD_PRESCRIPTION -> 1
                TO_UPI_QR -> 2
                BMI -> 3
                BMR-> 4
                WaterIntake-> 5
                CHAT_BOT->6
                TO_LOGOUT-> 7
            }
        }
    }
}