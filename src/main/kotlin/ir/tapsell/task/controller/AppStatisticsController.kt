package ir.tapsell.task.controller

import org.springframework.format.annotation.DateTimeFormat
import org.springframework.web.bind.annotation.*
import ir.tapsell.task.model.AppStatistics
import ir.tapsell.task.model.AppStatisticsListResponse
import ir.tapsell.task.service.IAppStatisticsService
import java.util.*

@RestController
@RequestMapping("/api/appStatistics")
class AppStatisticsController(private val iAppStatisticsService: IAppStatisticsService) {

    @PostMapping("/generateData")
    fun generateData(): String {
        return iAppStatisticsService.generateData()
    }

    @GetMapping("/getAllData")
    fun getAllAppStatistics(): List<AppStatistics> {
        return iAppStatisticsService.getAllAppStatistics()
    }

    @PostMapping("/getStats")
    fun getStats(
        @RequestParam("startDate") @DateTimeFormat(pattern = "yyyy-MM-dd") startDate: Date,
        @RequestParam("endDate") @DateTimeFormat(pattern = "yyyy-MM-dd") endDate: Date,
        @RequestParam("type") type: Int
    ): AppStatisticsListResponse {
        return iAppStatisticsService.getStats(startDate, endDate, type)
    }
}