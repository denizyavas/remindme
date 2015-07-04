package com.deniz.ratpack.http

/**
 * author: TRYavasU
 * date: 05/04/2015
 */
interface HttpClient {

    enum HttpMethod {
        GET, POST, DELETE
    }

    rx.Observable<HttpResponse> doGet(String url) throws Exception

    rx.Observable<HttpResponse> doPost(String url, byte[] byteArray) throws Exception

    rx.Observable<HttpResponse> doPost(String url, InputStream inputStream) throws Exception

    rx.Observable<HttpResponse> doPost(String url, String text) throws Exception

    rx.Observable<HttpResponse> doDelete(String url) throws Exception
}
