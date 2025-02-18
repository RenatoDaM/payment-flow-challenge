package com.payment.notificationqueueconsumer.configuration

import com.github.tomakehurst.wiremock.core.WireMockConfiguration
import com.github.tomakehurst.wiremock.junit5.WireMockExtension

object WireMockConfiguration {

    fun createWireMock(): WireMockExtension {
        return WireMockExtension.newInstance()
            .options(WireMockConfiguration.wireMockConfig().port(9090))
            .build()
    }

}
