package com.deniz.ratpack.http

import groovy.util.logging.Slf4j
import ratpack.launch.LaunchConfig

/**
 * author: TRYavasU
 * date: 05/04/2015
 */
@Slf4j
class HttpClientFactory {


    enum HttpClientType {
        ning,
        ratpack
    }

    HttpClient createHttpClient(LaunchConfig launchConfig, String httpClientType,
                                String proxyHost, int proxyPort,
                                String proxyUser, String proxyPassword, String nonProxyHosts,
                                String authenticationUser, String authenticationPassword,
                                int connectionTimeout, int readTimeout) {
        def httpClient
        if (httpClientType == HttpClientType.ning.toString()) {
            httpClient = new NingHttpClient(launchConfig, proxyHost, proxyPort, proxyUser, proxyPassword, nonProxyHosts,
                    authenticationUser, authenticationPassword, connectionTimeout, readTimeout)
        } else {
            httpClient = new RatpackHttpClient(launchConfig, authenticationUser, authenticationPassword, connectionTimeout, readTimeout)
        }
        log.info "created Http Client typed {} : {}", httpClientType, httpClient
        httpClient
    }

}
