package ir.tapsell.task

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cache.annotation.EnableCaching

@SpringBootApplication
@EnableCaching
class TaskApplication

fun main(args: Array<String>) {
    runApplication<TaskApplication>(*args)
}
