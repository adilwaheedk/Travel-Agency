package com.visionxoft.abacus.rehmantravel.utils;

import org.apache.http.conn.ssl.SSLSocketFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;

import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * OKHttp class responsible for communication using Http/Https
 */
public class OKHttp {

    public final static String MediaType_URL_Encode = "application/x-www-form-urlencoded";
    public final static String MediaType_TEXT_HTML = "text/html";
    public int responseCode;
    public String responseStatus;

    private static final MediaType MediaType_PNG = MediaType.parse("image/png");
    private static final MediaType MediaType_JPG = MediaType.parse("image/jpeg");
    private static final MediaType MediaType_GIF = MediaType.parse("image/gif");
    private static final MediaType MediaType_PDF = MediaType.parse("application/pdf");
    private static final MediaType MediaType_STREAM = MediaType.parse("application/octet-stream");
    private OkHttpClient client;
    private String responseBody;

    /**
     * Constructor to setup hostname, build client with SSL for secure communication
     */
    public OKHttp() {
        HostnameVerifier hostnameVerifier = SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER;
        //if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
        //    ConnectionSpec spec = new ConnectionSpec.Builder(ConnectionSpec.MODERN_TLS)
        //            .tlsVersions(TlsVersion.TLS_1_2).cipherSuites(CipherSuite.TLS_ECDHE_RSA_WITH_AES_256_CBC_SHA)
        //            .build();
        //    client = new OkHttpClient.Builder().connectionSpecs(Collections.singletonList(spec))
        //            .connectTimeout(20, TimeUnit.SECONDS) // Connect Timeout
        //            .writeTimeout(20, TimeUnit.SECONDS) // Write Timeout
        //            .readTimeout(30, TimeUnit.SECONDS) // Socket Timeout
        //            .hostnameVerifier(hostnameVerifier)
        //            .followSslRedirects(true)
        //            .build();} else {}
        client = new OkHttpClient.Builder().sslSocketFactory(new SSLFactoryHelper())
                .connectTimeout(20, TimeUnit.SECONDS) // Connect Timeout
                .writeTimeout(20, TimeUnit.SECONDS) // Write Timeout
                .readTimeout(30, TimeUnit.SECONDS) // Socket Timeout
                .hostnameVerifier(hostnameVerifier)
                .build();
    }

    /**
     * Get request
     *
     * @param url URL address to call
     * @return Response if success, else null
     */
    public String getCall(String url) throws Exception {
        Response response = client.newCall(new Request.Builder().url(url).build()).execute();
        responseCode = response.code();
        responseStatus = response.message().trim();
        responseBody = response.body().string().trim();
        return responseBody;
    }

    public String postCall(String url, Hashtable<String, Object> params) throws Exception {
        return postCall(url, params, MediaType_URL_Encode);
    }

    /**
     * Post request
     *
     * @param url       URL address to call
     * @param params    Parameters of request body
     * @param mediaType Content-Type of request header
     * @return Response if success, else null
     */
    public String postCall(String url, Hashtable<String, Object> params, String mediaType) throws Exception {
        FormBody.Builder builder = new FormBody.Builder();
        for (String key : params.keySet()) {
            Object value = params.get(key);
            if (value instanceof String)
                builder.add(key, String.valueOf(value));

            else if (value instanceof List<?>) {
                for (int i = 0; i < ((List) value).size(); i++) {
                    builder.add(key, String.valueOf(((List) value).get(i)));
                }
            }
        }

        Request request = new Request.Builder().url(url)
                .addHeader("Content-Type", mediaType)
                .post(builder.build()).build();
        Response response = client.newCall(request).execute();
        responseCode = response.code();
        responseStatus = response.message().trim();
        responseBody = response.body().string().trim();
        return responseBody;
    }

    /**
     * Send multiple form data request
     *
     * @param url    URL address to call
     * @param params Parameters of request body
     * @return Response if success, else null
     * @throws Exception
     */
    public String multipartRequest(String url, Hashtable<String, Object> params) throws Exception {

        MultipartBody.Builder builder = new MultipartBody.Builder();
        builder.setType(MultipartBody.FORM);
        for (String key : params.keySet()) {
            Object value = params.get(key);
            if (value instanceof String) {
                builder.addFormDataPart(key, String.valueOf(value));
            } else if (value instanceof ArrayList<?>) {
                for (Object obj : (ArrayList<?>) value) {
                    if (obj instanceof File) {
                        File file = (File) obj;
                        builder.addFormDataPart(key, file.getName(),
                                RequestBody.create(getMediaTypeFromFileExt(file.getName()), file));
                    } else if (obj instanceof String) {
                        builder.addFormDataPart(key, String.valueOf(obj));
                    }
                }
            }
        }

        Request request = new Request.Builder()
                .addHeader("Content-Type", MediaType_TEXT_HTML)
                .url(url).post(builder.build()).build();

        Response response = client.newCall(request).execute();
        responseCode = response.code();
        responseStatus = response.message().trim();
        responseBody = response.body().string().trim();
        return responseBody;
    }

    /**
     * Get mime type from file extension
     *
     * @param fileName String filename
     * @return MediaType object
     */
    private static MediaType getMediaTypeFromFileExt(String fileName) {
        if (fileName.endsWith("png")) return MediaType_PNG;
        else if (fileName.endsWith("jpg") || fileName.endsWith("jpeg") || fileName.endsWith("jpe"))
            return MediaType_JPG;
        else if (fileName.endsWith("gif")) return MediaType_GIF;
        else if (fileName.endsWith("pdf")) return MediaType_PDF;
        else return MediaType_STREAM;
    }
}
