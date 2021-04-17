package ir.tapsell.task.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

@Component
class AppProperties {
    @Value("\${appStatistics.data.generate.count}")
    lateinit var appStatisticsGenerateCount: String

    @Value("\${appStatistics.app.count}")
    lateinit var appCount: String
}