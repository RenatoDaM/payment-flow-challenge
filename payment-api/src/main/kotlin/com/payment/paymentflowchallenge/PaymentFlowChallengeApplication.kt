package com.payment.paymentflowchallenge

import com.payment.paymentflowchallenge.dataprovider.queue.kafka.configuration.KafkaConfig
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication
import org.springframework.data.r2dbc.config.EnableR2dbcAuditing
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories

@SpringBootApplication
@EnableR2dbcAuditing
@EnableConfigurationProperties(KafkaConfig::class)
@EnableR2dbcRepositories(basePackages = ["com.payment.paymentflowchallenge.dataprovider.database.postgres.repository"])
class PaymentFlowChallengeApplication

fun main(args: Array<String>) {
    runApplication<PaymentFlowChallengeApplication>(*args)
}
