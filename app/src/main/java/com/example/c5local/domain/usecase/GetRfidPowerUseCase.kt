package com.example.c5local.domain.usecase

import com.example.c5local.domain.repository.RfidPreferenceRepository
import javax.inject.Inject

class GetRfidPowerUseCase @Inject constructor(
    private val repository: RfidPreferenceRepository
) {
    operator fun invoke():Int {
        return repository.getRfidPower()
    }
}