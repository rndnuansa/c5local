package com.example.c5local.domain.usecase

import com.example.c5local.domain.repository.RfidPreferenceRepository
import javax.inject.Inject

class SaveRfidPowerUseCase @Inject constructor(
    private val repository: RfidPreferenceRepository
) {
    operator fun invoke(value: Int) {
        repository.saveRfidPower(value)
    }
}