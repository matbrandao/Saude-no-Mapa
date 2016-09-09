package com.mat_brandao.saudeapp.network.retrofit;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.schedulers.Schedulers;

/**
 * Created by infosolo2 on 18/05/2015.
 */
public class RestClient {
    private static final String BASE_URL = "http://mobile-aceite.tcu.gov.br:80/";

    private static RetrofitInterface REST_CLIENT;
    private static RetrofitInterface REST_CLIENT_HEADER;

    private static String mToken;

    private RestClient() {}

    static {
        setupRestClient();
    }

    public static RetrofitInterface get() {
        return REST_CLIENT;
    }

    public static RetrofitInterface getHeader(String token) {
        mToken = token;
        setupRestClientHeader();
        return REST_CLIENT_HEADER;
    }

    private static void setupRestClient() {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();

        HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor();

        // Can be Level.BASIC, Level.HEADERS, or Level.BODY
        httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        builder.networkInterceptors().add(httpLoggingInterceptor);
        builder.readTimeout(30, TimeUnit.SECONDS);
        builder.connectTimeout(30, TimeUnit.SECONDS);

        Gson gson = new GsonBuilder()
                .setDateFormat("yyyy-MM-dd")
                .excludeFieldsWithoutExposeAnnotation()
                .create();

        RxJavaCallAdapterFactory rxAdapter = RxJavaCallAdapterFactory.createWithScheduler(Schedulers.io());

        Retrofit retrofit = new Retrofit.Builder()
                .client(builder.build())
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .addCallAdapterFactory(rxAdapter)
                .build();

        REST_CLIENT = retrofit.create(RetrofitInterface.class);
    }

    private static void setupRestClientHeader() {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();

        HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor();
        httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        builder.networkInterceptors().add(httpLoggingInterceptor);

        if (mToken != null) {
            builder.addInterceptor(chain -> {
                Request request = chain.request()
                        .newBuilder()
                        .addHeader("appToken", mToken)
                        .build();
                return chain.proceed(request);
            });
        }

        Gson gson = new GsonBuilder()
                .setDateFormat("yyyy-MM-dd")
                .excludeFieldsWithoutExposeAnnotation()
                .create();

        RxJavaCallAdapterFactory rxAdapter = RxJavaCallAdapterFactory.createWithScheduler(Schedulers.io());

        Retrofit retrofit = new Retrofit.Builder()
                .client(builder.build())
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .addCallAdapterFactory(rxAdapter)
                .build();

        REST_CLIENT_HEADER = retrofit.create(RetrofitInterface.class);
    }
}