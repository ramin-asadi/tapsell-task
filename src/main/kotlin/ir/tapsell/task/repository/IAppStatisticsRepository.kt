package ir.tapsell.task.repository

import org.springframework.data.mongodb.repository.MongoRepository
import ir.tapsell.task.model.AppStatistics
import java.util.*

interface IAppStatisticsRepository : MongoRepository<AppStatistics, String> {
    fun findByReportTimeBetweenAndType(from: Date, to: Date, type: Int): List<AppStatistics>
}