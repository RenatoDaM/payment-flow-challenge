//package com.payment.paymentflowchallenge.testconfig
//
//import io.r2dbc.spi.ConnectionFactory
//import org.springframework.context.annotation.Bean
//import org.springframework.context.annotation.Configuration
//import org.springframework.core.io.ClassPathResource
//import org.springframework.r2dbc.connection.init.CompositeDatabasePopulator
//import org.springframework.r2dbc.connection.init.ConnectionFactoryInitializer
//import org.springframework.r2dbc.connection.init.ResourceDatabasePopulator
//
//
//@Configuration
//class TestDatabaseConfiguration {
//    @Bean
//    fun initializer(connectionFactory: ConnectionFactory): ConnectionFactoryInitializer {
//        val initializer = ConnectionFactoryInitializer()
//        initializer.setConnectionFactory(connectionFactory)
//        val populator: CompositeDatabasePopulator = CompositeDatabasePopulator()
//        populator.addPopulators(ResourceDatabasePopulator(ClassPathResource("test-schema.sql")))
//        initializer.setDatabasePopulator(populator)
//        return initializer
//    }
//}