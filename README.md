#Tapsell Task Project:
**Keyword**: Kotlin, Maven, Spring Boot, Spring Data, MongoDB, Cache (Redis)

**Technology Stack:**</br>
- java **8**
- kotlin **1.4.31**
- spring boot **2.4.4**
- mongodb **3.1.6** 
- redis **2.4.6**
- time4j **4.28**
- IDE Intellij
<hr/>

### Learn and study MongoDB in Spring Data (time spent: 3 hours)
One of the topics that should have been used in this task and due
to the lack of previous experience needed to be practiced and
implemented was the discussion of the connection to the Mongodb
using Spring Data. That's why I first learned this by 
installing the Mongo database and implementing a **CRUD** task. 
The resources used for this work are:
 - [Introduction to Spring Data MongoDB](https://www.baeldung.com/spring-data-mongodb-tutorial)
 - [MongoDB repositories](https://docs.spring.io/spring-data/mongodb/docs/1.2.0.RELEASE/reference/html/mongo.repositories.html)
<hr/>

### Create, configure and initialize the Kotlin SpringBoot Maven project (time spent: 2 hours)
Due to lack of previous experience in create a spring boot 
project using Kotlin language and its requirements, 
I created a project using [Spring Initializer](https://start.spring.io) and installed 
the related requirements.

 - [Building web applications with Spring Boot and Kotlin](https://spring.io/guides/tutorials/spring-boot-kotlin/) <br/>
   
After creating the project, I specified the general structure
of the project. Below you can see the general outline of this
structure:<br>
```
task(project name)
    |-> src/main/kotlin/
        |-> ir/tapsell/task/
            |-> config/
            |-> controller/
            |-> model/
            |-> repository/
            |-> service/
                |-> impl/
            |-> utility/
```
In this step, the classes defined in the task are defined in the
project _model_ folder
```kotlin
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
```
The **id** in ``AppStatistics`` class is not unique and specifies the
ID of the Application in business that can be repeated at different times.
To prevent this identifier from being considered as ``_id`` in the 
mongodb, we used ``@Field`` so that it is not considered as a record identifier in the database.
<hr/>

### Sample Data Generator (time spent: 6 hours)
After connecting to the Mongodb and creating a record in it,
the main challenge of this step was to create and store a 
certain amount of data using the features of the Kotlin language.
In this part, using the possibility of `Range` and also generating
a `random` number of a suffering in Kotlin language, this operation
was implemented.<br/>
_This part was implemented using the following code_:
```kotlin
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
```
To create a random date, the `generateRandomDate()` method is used, 
which is defined in the `DateUtility.kt` class, and with each call, 
it creates and returns a date between 2016-03-20 (1395/01/01) to the present day.<br><br>
As shown in the code above, the total number of records as well as 
the number of applications whose statistical data must be created 
in the database is specified in the `application.properties` file.

These values are as follows: You can also see the default values
```kotlin
appStatistics.data.generate.count = 2000
appStatistics.app.count = 30
```
To use the above values in the code, the following class is defined in the program<br/>
```kotlin
@Component
class AppProperties {
    @Value("\${appStatistics.data.generate.count}")
    lateinit var appStatisticsGenerateCount: String

    @Value("\${appStatistics.app.count}")
    lateinit var appCount: String
}
```
to use these class inject into the service class using `@Autowired`<br/>
To test and execute this operation (generate random data), 
the following raster must be called in post mode.
(I used the [**postman**](https://www.postman.com/) for testing)

```
[post] http://localhost:8080/api/appStatistics/generateData
```
used the [MongoDBCompass](https://www.mongodb.com/products/compass) program to view the result in the database
<hr/>

### Display data with the requested logic (time spent: 7 hours)
The data call will be based on the start and end dates, and the 
type of applications.<br> According to the above filter, in the 
repository, a method for receiving data according to the start 
and end date, and the type of applications is defined as
`findByReportTimeBetweenAndType` using the spring data(MongoRepository) features,
which you can see below the signature of this method.
```kotlin
fun findByReportTimeBetweenAndType(from: Date, to: Date, type: Int): List<AppStatistics>
```

The data was to be returned to the `AppStatisticsListResponse.kt` 
class according to the format specified in the task.

The following is the structure of the `AppStatisticsListResponse.kt`
class as well as the `AppStatisticsModel.kt` class used in it.
```kotlin
class AppStatisticsListResponse(
    val stats: List<AppStatisticsModel>
)
```
```kotlin
class AppStatisticsModel(
    val weekNum: Int,
    val year: Int,
    val requests: Int,
    val clicks: Int,
    val installs: Int
)
```
Due to the specified format we need to aggregate the data stored
in the database by **year** and **week**<br>
The implementation of this part was done using the following code,
which we will examine its different parts in the following
```kotlin
override fun getStats(startDate: Date, endDate: Date, type: Int): AppStatisticsListResponse {
        val result = appStatisticsRepository.findByReportTimeBetweenAndType(startDate, endDate, type)
        return AppStatisticsListResponse(
            result.groupBy {
                Pair(
                    DateUtility.convertGregorianToSolar(it.reportTime).year,
                    ceil((DateUtility.convertGregorianToSolar(it.reportTime).dayOfYear / 7F).toDouble()).toInt()
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
```
In the above method, the data is first obtained by date and type and 
then grouped by year and week using `groupBy`. 
This creates a `Map <K, List <T>>` which then needs to be turned into 
a list of `AppStatisticsModel`. This is done using a `map`, 
and finally the data in the list is sorted by **year** and **week**.<br>
Due to the clear input and output parameters, 
we create a method in the controller to respond 
to the data display request as follows:
```kotlin
@PostMapping("/getStats")
    fun getStats(
        @RequestParam("startDate") @DateTimeFormat(pattern = "yyyy-MM-dd") startDate: Date,
        @RequestParam("endDate") @DateTimeFormat(pattern = "yyyy-MM-dd") endDate: Date,
        @RequestParam("type") type: Int
    ): AppStatisticsListResponse {
        return iAppStatisticsService.getStats(startDate, endDate, type)
    }
```
According to the definition of the problem, the input parameters of the 
date are of type `java.util.Date`, and the format according to which the date data 
should be entered is in the form of `yyyy-MM-dd` which the 
conversion operation is performed in the controller using `@DateTimeFormat`<br>
To call this method, use the following reset, which is a post type : 
```
[post] http://localhost:8080/api/appStatistics/getStats?startDate=2016-01-02&endDate=2021-03-20&type=0
```
<hr/>

### Cache Data(time spent: 2 hours)
Depending on the problem, the data must be cached for 20 minutes 
with the key combination of start and end dates and type.<br>
[Redis](https://redis.io/) is used to cache data in this project. 
In the first step, adding the following code to `pom.xml` 
adds the redis dependencies to the project.
```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-redis</artifactId>
</dependency>
```
Make the redis settings in the `application.properties` file as follows:
```kotlin
spring.redis.host=localhost
spring.redis.port=6379
spring.cache.redis.time-to-live=1200000
spring.cache.cache-names=appStatisticsCache, getStats
```
Using `@Cacheable`, the output data of the desired method is cached
```kotlin
@Cacheable(value = ["getStats"], key = "{#startDate, #endDate, #type}")
    override fun getStats(startDate: Date, endDate: Date, type: Int): AppStatisticsListResponse {
        ...
    }
```
<hr/>

### Preparing report (time spent: 4 hours)
<hr/>

## Total Time: 24 hours
