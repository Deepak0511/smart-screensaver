package in.dpk.assistants.smart_screensaver.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;
import reactor.core.publisher.Mono;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import javax.net.ssl.SSLException;
import java.time.Duration;

@Configuration
@EnableConfigurationProperties
@ConfigurationProperties(prefix = "app.external")
@Data
@Slf4j
public class ExternalApiConfig {
    
    private String weatherApiUrl = "https://api.open-meteo.com/v1/forecast";
    private String locationApiUrl = "https://ipapi.co/json/";
    private String quoteApiUrl = "https://api.quotable.io/random";
    
    @Bean
    public WebClient webClient() throws SSLException {
        // Create SSL context that trusts all certificates (for development)
        SslContext sslContext = SslContextBuilder
                .forClient()
                .trustManager(InsecureTrustManagerFactory.INSTANCE)
                .build();
        
        return WebClient.builder()
                .codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(2 * 1024 * 1024))
                .filter(errorHandler())
                .filter(sslFilter())
                .build();
    }
    
    private ExchangeFilterFunction errorHandler() {
        return ExchangeFilterFunction.ofResponseProcessor(clientResponse -> {
            if (clientResponse.statusCode().isError()) {
                log.warn("HTTP error: {}", clientResponse.statusCode());
            }
            return Mono.just(clientResponse);
        });
    }
    
    private ExchangeFilterFunction sslFilter() {
        return (request, next) -> {
            log.debug("Making request to: {}", request.url());
            return next.exchange(request);
        };
    }
} 