package com.example.demo.ais.adapter.digitraffic;

import com.example.demo.ais.service.spi.AIS;
import io.micrometer.core.instrument.MeterRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;

@Configuration
class AISConfig {

    private static final Logger log = LoggerFactory.getLogger(AISConfig.class);

    @Bean
    @ConditionalOnProperty(name = "ais.source", havingValue = "digitraffic")
    public AIS digitTrafficAIS(MeterRegistry meterRegistry, Clock clock) {
        log.info("Loading AIS data from Digitraffic");
        return new DigiTrafficAIS(meterRegistry, clock);
    }

    @Bean
    @ConditionalOnProperty(name = "ais.source", havingValue = "mock", matchIfMissing = true)
    public AIS mockAIS() {
        log.warn("Using mock AIS data");
        return new MockAIS();
    }
}
