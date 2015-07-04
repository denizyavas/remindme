package com.deniz.remindme

import com.deniz.ratpack.http.HttpClient
import com.deniz.ratpack.http.HttpClientFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.PropertySource
import ratpack.launch.LaunchConfig

@Configuration
@org.springframework.context.annotation.Lazy
@PropertySource(ignoreResourceNotFound = true, value = ['classpath:/default.properties', 'classpath:/${environment}.properties'])
class HttpClientConfig {

    @Autowired
    LaunchConfig launchConfig

    @Bean
    public static HttpClientFactory httpClientFactory() {
        new HttpClientFactory()
    }

    @Bean
    @Qualifier('httpClient')
    @org.springframework.context.annotation.Lazy
    public static HttpClient httpClient(
            LaunchConfig launchConfig,
            @Value('${http.client.type}')
                    String httpClientType,
            @Value('${http.proxy.host}')
                    String httpProxyHost,
            @Value('${http.proxy.port}')
                    int httpProxyPort,
            @Value('${http.proxy.user}')
                    String httpProxyUser,
            @Value('${http.proxy.password}')
                    String httpProxyPassword,
            @Value('${http.proxy.nonProxyHosts}')
                    String httpNonProxyHosts
    ) {
        httpClientFactory().createHttpClient(launchConfig, httpClientType, httpProxyHost, httpProxyPort, httpProxyUser, httpProxyPassword, httpNonProxyHosts, '', '', 30000, 30000)
    }

}


