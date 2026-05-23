package com.sahmfood.pos.domain.entities

import kotlinx.serialization.Serializable

enum class SyncStatus { PENDING, IN_FLIGHT, SUCCEEDED, FAILED }

enum class SyncOpType { CREATE_ORDER, UPDATE_ORDER_STATUS }

@Serializable
data class SyncQueueEntry(
    val id: String,
    val opType: SyncOpType,
    val orderId: String,
    val payloadJson: String,
    val attempts: Int = 0,
    val status: SyncStatus = SyncStatus.PENDING,
    val createdAt: Long
)
