package com.example.c5local.domain.repository

interface RfidPreferenceRepository {
    fun saveRfidPower(value: Int)
    fun getRfidPower(): Int
}