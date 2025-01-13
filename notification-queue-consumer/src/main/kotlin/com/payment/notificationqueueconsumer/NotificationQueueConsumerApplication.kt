package com.payment.notificationqueueconsumer

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class NotificationQueueConsumerApplication

fun main(args: Array<String>) {
    runApplication<NotificationQueueConsumerApplication>(*args)
}
