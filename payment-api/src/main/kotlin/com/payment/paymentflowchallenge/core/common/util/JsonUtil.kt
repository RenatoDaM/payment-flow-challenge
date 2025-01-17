package com.payment.paymentflowchallenge.core.common.util

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.springframework.beans.factory.annotation.Autowired

@Autowired
private val mapper: ObjectMapper = jacksonObjectMapper()

fun Any.toJson(): String {
    return mapper.writeValueAsString(this)
}

fun <T> Any.fromJson(value: String, typeReference: TypeReference<T>): T {
    return mapper.readValue(value, typeReference)
}
