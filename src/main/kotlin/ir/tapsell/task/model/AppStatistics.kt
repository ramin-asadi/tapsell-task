package ir.tapsell.task.model

import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.core.mapping.Field
import java.util.*

@Document
data class AppStatistics(
    @Field(name = "id")
    val id: String,
    val reportTime: Date,
    val type: Int,
    val videoRequests: Int,
    val webViewRequest: Int,
    val videoClicks: Int,
    val webViewClicks: Int,
    val videoInstalls: Int,
    val webViewInstalls: Int
)