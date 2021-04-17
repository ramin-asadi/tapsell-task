package ir.tapsell.task.service

import ir.tapsell.task.model.AppStatistics
import ir.tapsell.task.model.AppStatisticsListResponse
import java.util.*

interface IAppStatisticsService {
    fun generateData(): String
    fun getAllAppStatistics(): List<AppStatistics>
    fun getStats(startDate: Date, endDate: Date, type: Int): AppStatisticsListResponse
}