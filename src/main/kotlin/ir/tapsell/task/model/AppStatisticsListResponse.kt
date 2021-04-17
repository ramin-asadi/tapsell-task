package ir.tapsell.task.model

import java.io.Serializable

class AppStatisticsListResponse(
    val stats: List<AppStatisticsModel>
) : Serializable