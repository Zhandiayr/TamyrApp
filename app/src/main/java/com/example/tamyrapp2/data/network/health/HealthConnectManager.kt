package com.example.tamyrapp2.data.network.health

import android.content.Context
import android.os.Build
import androidx.health.connect.client.HealthConnectClient
import androidx.health.connect.client.permission.HealthPermission
import androidx.health.connect.client.records.*
import androidx.health.connect.client.request.ReadRecordsRequest
import java.time.Instant
import java.time.temporal.ChronoUnit

class HealthConnectManager(private val context: Context) {
    private val healthConnectClient = HealthConnectClient.getOrCreate(context)

    val permissions = setOf(
        HealthPermission.getReadPermission(HeartRateRecord::class),
        HealthPermission.getReadPermission(StepsRecord::class),
        HealthPermission.getReadPermission(TotalCaloriesBurnedRecord::class),
        HealthPermission.getReadPermission(SleepSessionRecord::class)
    )

    suspend fun hasAllPermissions(): Boolean {
        return healthConnectClient.permissionController.getGrantedPermissions().containsAll(permissions)
    }

    suspend fun readLatestHealthData(): HealthData {
        val endTime = Instant.now()
        val startTime = endTime.minus(1, ChronoUnit.DAYS)

        val heartRates = healthConnectClient.readRecords(
            ReadRecordsRequest(HeartRateRecord::class, timeRangeFilter = androidx.health.connect.client.time.TimeRangeFilter.between(startTime, endTime))
        )

        val steps = healthConnectClient.readRecords(
            ReadRecordsRequest(StepsRecord::class, timeRangeFilter = androidx.health.connect.client.time.TimeRangeFilter.between(startTime, endTime))
        )

        val calories = healthConnectClient.readRecords(
            ReadRecordsRequest(TotalCaloriesBurnedRecord::class, timeRangeFilter = androidx.health.connect.client.time.TimeRangeFilter.between(startTime, endTime))
        )

        val sleep = healthConnectClient.readRecords(
            ReadRecordsRequest(SleepSessionRecord::class, timeRangeFilter = androidx.health.connect.client.time.TimeRangeFilter.between(startTime, endTime))
        )

        return HealthData(
            heartRate = heartRates.records.lastOrNull()?.samples?.lastOrNull()?.beatsPerMinute?.toInt() ?: 0,
            steps = steps.records.lastOrNull()?.count ?: 0,
            calories = calories.records.lastOrNull()?.energy?.inKilocalories?.toInt() ?: 0,
            sleepHours = sleep.records.lastOrNull()?.endTime?.epochSecond?.minus(sleep.records.lastOrNull()?.startTime?.epochSecond ?: 0)?.div(3600)?.toInt() ?: 0
        )
    }

    data class HealthData(
        val heartRate: Int,
        val steps: Int,
        val calories: Int,
        val sleepHours: Int
    )
}
