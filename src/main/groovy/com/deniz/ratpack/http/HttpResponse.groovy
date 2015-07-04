package com.deniz.ratpack.http

/**
 * author: TRYavasU
 * date: 05/04/2015
 */
class HttpResponse {

    byte[] bodyAsBytes
    int statusCode
    URI uri

    boolean isSuccess() {
        statusCode >= 200 && statusCode < 300
    }

    InputStream getBodyAsStream() {
        bodyAsBytes ? new ByteArrayInputStream(bodyAsBytes) : null
    }

    String getBodyAsText() {
        bodyAsBytes ? new String(bodyAsBytes, EncodingUtil.CHARSET) : null
    }

    public boolean isSuccessful(String message, List errors) {
        if (!success) {
            errors << "HTTP $statusCode error $message".toString()
        }
        return success
    }

}
