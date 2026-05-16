package com.libmgmt.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import io.micrometer.prometheus.PrometheusConfig
import io.micrometer.prometheus.PrometheusMeterRegistry

@Configuration
class MetricsConfig {

    @Bean
    fun prometheusMeterRegistry(): PrometheusMeterRegistry {
        return PrometheusMeterRegistry(PrometheusConfig.DEFAULT)
    }
}
