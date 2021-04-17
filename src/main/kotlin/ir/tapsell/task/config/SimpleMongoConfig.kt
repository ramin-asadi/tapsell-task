package ir.tapsell.task.config

import com.mongodb.ConnectionString
import com.mongodb.MongoClientSettings
import com.mongodb.client.MongoClient
import com.mongodb.client.MongoClients
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories


@Configuration
@EnableMongoRepositories(basePackages = ["ir.tapsell.task.repository"])
class SimpleMongoConfig {
    @Bean
    @Throws(Exception::class)
    fun mongo(): MongoClient {
        val connectionString = ConnectionString("mongodb://localhost:27017/AppStatisticsDB")
        val mongoClientSettings = MongoClientSettings.builder().applyConnectionString(connectionString).build()
        return MongoClients.create(mongoClientSettings)
    }

    @Bean
    @Throws(Exception::class)
    fun mongoTemplate(): MongoTemplate? {
        return MongoTemplate(mongo(), "AppStatisticsDB")
    }
}