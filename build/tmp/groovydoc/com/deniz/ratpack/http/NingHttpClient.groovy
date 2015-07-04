package com.deniz.ratpack.http

import com.deniz.remindme.HttpClientConfig
import com.ning.http.client.AsyncCompletionHandlerBase
import com.ning.http.client.AsyncHttpClient
import com.ning.http.client.AsyncHttpClient.BoundRequestBuilder
import com.ning.http.client.AsyncHttpClientConfig
import com.ning.http.client.ProxyServer
import com.ning.http.client.Realm
import com.ning.http.client.Response
import groovy.util.logging.Slf4j
import ratpack.launch.LaunchConfig
import ratpack.rx.internal.ExecControllerBackedScheduler
import rx.Scheduler

/**
 * author: TRYavasU
 * date: 05/04/2015
 */
@Slf4j
class NingHttpClient implements HttpClient {


    LaunchConfig launchConfig
    String authenticationUser, authenticationPassword
    AsyncHttpClient asyncHttpClient
    Scheduler resumingScheduler

    public NingHttpClient() {
    }

    public NingHttpClient(LaunchConfig launchConfig, String proxyHost, int proxyPort,
                          String proxyUser, String proxyPassword, String nonProxyHosts,
                          String authenticationUser, String authenticationPassword,
                          int connectionTimeout, int readTimeout) {
        AsyncHttpClientConfig config
        if (proxyHost) {
            def proxyServer = new ProxyServer(proxyHost, proxyPort, proxyUser, proxyPassword)
            if (nonProxyHosts) {
                nonProxyHosts.split(",")?.collect({ it.trim() })?.each {
                    proxyServer.addNonProxyHost(it)
                }
            }
            config = new AsyncHttpClientConfig.Builder()
                    .setProxyServer(proxyServer)
                    .setConnectionTimeoutInMs(connectionTimeout)
                    .setRequestTimeoutInMs(readTimeout)
                    .build()
        } else {
            config = new AsyncHttpClientConfig.Builder()
                    .setConnectionTimeoutInMs(connectionTimeout)
                    .setRequestTimeoutInMs(readTimeout)
                    .build()
        }
        asyncHttpClient = new AsyncHttpClient(config)

        this.authenticationUser = authenticationUser
        this.authenticationPassword = authenticationPassword

        this.launchConfig = launchConfig

        resumingScheduler = new ExecControllerBackedScheduler(launchConfig.execController)
    }

    public NingHttpClient(LaunchConfig launchConfig) {
        asyncHttpClient = new AsyncHttpClient(new AsyncHttpClientConfig.Builder().build())
        this.launchConfig = launchConfig

        resumingScheduler = new ExecControllerBackedScheduler(launchConfig.execController)
    }

    BoundRequestBuilder createRequestBuilder(HttpClient.HttpMethod httpMethod, String url, data) {
        log.info "starting {} {}", httpMethod, url

        Realm realm = authenticationUser ? (new Realm.RealmBuilder()).setScheme(Realm.AuthScheme.BASIC).setPrincipal(authenticationUser).setPassword(authenticationPassword).build() : null

        BoundRequestBuilder requestBuilder
        if (HttpMethod.GET == httpMethod) {
            requestBuilder = asyncHttpClient.prepareGet(url)
                    .addHeader('Accept-Charset', 'UTF-8')
                    .addHeader('Content-Type', 'application/json;charset=UTF-8')
                    .setRealm(realm)
        } else if (HttpMethod.DELETE == httpMethod) {
            requestBuilder = asyncHttpClient.prepareDelete(url).addHeader('Accept-Charset', 'UTF-8').setRealm(realm)
        } else {
            requestBuilder = asyncHttpClient.preparePost(url)
                    .addHeader('Accept-Charset', 'UTF-8')
                    .addHeader('Content-Type', 'multipart/form-data;charset=UTF-8')
                    .setRealm(realm).setBody(data)
        }
        requestBuilder
    }

    rx.Observable<HttpResponse> getResultAsResponse(HttpClient.HttpMethod httpMethod, String url, data) {
        rx.Observable.just("starting").flatMap({
            observe(launchConfig.execController.control.promise { f ->
                createRequestBuilder(httpMethod, url, data).execute(new AsyncCompletionHandlerBase() {
                    @Override
                    Response onCompleted(Response response) throws Exception {
                        f.success(response)
                        log.info "HTTP {} {} {}", response.statusCode, httpMethod, url
                        response
                    }

                    @Override
                    void onThrowable(Throwable t) {
                        log.error "HTTP Error $httpMethod $url", t
                        f.error(t)
                    }
                })
            })
        }).map({ Response response ->
            new HttpResponse(uri: url.toURI(), statusCode: response.statusCode, bodyAsBytes: response.responseBodyAsBytes)
        }).observeOn(resumingScheduler)
    }

    @Override
    rx.Observable<HttpResponse> doGet(String url) throws Exception {
        getResultAsResponse(HttpMethod.GET, url, null)
    }

    @Override
    rx.Observable<HttpResponse> doPost(String url, String data) throws Exception {
        getResultAsResponse(HttpMethod.POST, url, data)
    }

    @Override
    rx.Observable<HttpResponse> doPost(String url, InputStream inputStream) throws Exception {
        getResultAsResponse(HttpMethod.POST, url, inputStream)
    }

    @Override
    rx.Observable<HttpResponse> doPost(String url, byte[] byteArray) throws Exception {
        getResultAsResponse(HttpMethod.POST, url, byteArray)
    }

    @Override
    rx.Observable<HttpResponse> doDelete(String url) throws Exception {
        getResultAsResponse(HttpMethod.DELETE, url, null)
    }

}
