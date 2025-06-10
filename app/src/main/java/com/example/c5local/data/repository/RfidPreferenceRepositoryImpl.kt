package com.example.c5local.data.repository

import android.content.Context
import com.example.c5local.domain.repository.RfidPreferenceRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RfidPreferenceRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : RfidPreferenceRepository {

    private val prefs = context.getSharedPreferences("rfid_prefs", Context.MODE_PRIVATE)

    override fun saveRfidPower(value: Int) {
        prefs.edit().putInt("rfid_power", value).apply()
    }

    override fun getRfidPower(): Int {
        return prefs.getInt("rfid_power", 30)
    }
}