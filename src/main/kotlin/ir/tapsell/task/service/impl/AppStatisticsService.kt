package ir.tapsell.task.service.impl

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.cache.annotation.CacheConfig
import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Service
import ir.tapsell.task.config.AppProperties
import ir.tapsell.task.model.AppStatistics
import ir.tapsell.task.model.AppStatisticsListResponse
import ir.tapsell.task.model.AppStatisticsModel
import ir.tapsell.task.model.AppType
import ir.tapsell.task.repository.IAppStatisticsRepository
import ir.tapsell.task.service.IAppStatisticsService
import ir.tapsell.task.utility.DateUtility
import java.util.*
import kotlin.math.ceil

@Service
@CacheConfig(cacheNames = ["appStatisticsCache"])
class AppStatisticsService(private val appStatisticsRepository: IAppStatisticsRepository) : IAppStatisticsService {

    @Autowired
    lateinit var appProperties: AppProperties

    override fun generateData(): String {
        (1..appProperties.appStatisticsGenerateCount.toInt()).map {
            AppStatistics(
                id = "app${(1..appProperties.appCount.toInt()).random()}",
                reportTime = DateUtility.generateRandomDate(),
                type = AppType.values().toList().shuffled().first().ordinal,
                videoRequests = (1..100).random(),
                webViewRequest = (1..100).random(),
                videoClicks = (1..100).random(),
                webViewClicks = (1..100).random(),
                videoInstalls = (1..100).random(),
                webViewInstalls = (1..100).random()
            )
        }
            .toList()
            .forEach(appStatisticsRepository::save)
        return "${appProperties.appStatisticsGenerateCount} Data Generated"
    }

    override fun getAllAppStatistics(): List<AppStatistics> {
        return appStatisticsRepository.findAll()
    }

    @Cacheable(value = ["getStats"], key = "{#startDate, #endDate, #type}")
    override fun getStats(startDate: Date, endDate: Date, type: Int): AppStatisticsListResponse {
        val result = appStatisticsRepository.findByReportTimeBetweenAndType(startDate, endDate, type)
        return AppStatisticsListResponse(
            result.groupBy {
                Pair(
                    DateUtility.convertGregorianToSolar(it.reportTime).year,
                    ceil((DateUtility.convertGregorianToSolar(it.reportTime).dayOfYear / 7F).toDouble()).toInt()
                    // این مخاسبات برای بدست آوردن هفته در سال انجام شده است و داده ها بر اساس سال و هفته گروه بندی می شوند
                )
            }.map {
                AppStatisticsModel(
                    year = it.key.first,
                    weekNum = it.key.second,
                    requests = it.value.sumBy { appStatistics -> appStatistics.videoRequests + appStatistics.webViewRequest },
                    clicks = it.value.sumBy { appStatistics -> appStatistics.videoClicks + appStatistics.webViewClicks },
                    installs = it.value.sumBy { appStatistics -> appStatistics.videoInstalls + appStatistics.webViewInstalls },

                    )
            }.sortedWith(compareBy({ it.year }, { it.weekNum })).toList()
        )
    }
}