package com.payment.paymentflowchallenge.configuration

import com.mongodb.reactivestreams.client.MongoClients
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.mongodb.config.AbstractReactiveMongoConfiguration
import org.springframework.data.mongodb.core.ReactiveMongoTemplate
import org.springframework.data.mongodb.repository.config.EnableReactiveMongoRepositories

@Configuration
@EnableReactiveMongoRepositories(
    basePackages = ["com.payment.paymentflowchallenge.dataprovider.database.mongo.repository"]
)
class MongoConfiguration : AbstractReactiveMongoConfiguration() {

    override fun getDatabaseName() = "circuit_breaker"

    override fun reactiveMongoClient() = mongoClient()

    @Bean
    fun mongoClient() = MongoClients.create()

    @Bean
    fun reactiveMongoTemplate()
        = ReactiveMongoTemplate(mongoClient(), databaseName)
}