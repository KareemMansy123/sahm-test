package com.sahmfood.pos.domain.services

import com.sahmfood.pos.domain.entities.Receipt

sealed class PrintResult {
    data object Success : PrintResult()
    data class Failure(val reason: String) : PrintResult()
}

interface PrinterService {
    suspend fun print(receipt: Receipt): PrintResult
}
