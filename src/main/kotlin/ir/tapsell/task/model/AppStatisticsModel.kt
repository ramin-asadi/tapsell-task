package ir.tapsell.task.model

import java.io.Serializable

class AppStatisticsModel(
    val weekNum: Int,
    val year: Int,
    val requests: Int,
    val clicks: Int,
    val installs: Int
) : Serializable