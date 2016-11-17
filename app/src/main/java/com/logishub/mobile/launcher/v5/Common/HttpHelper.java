package com.logishub.mobile.launcher.v5.Common;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class HttpHelper<T> {

    private T service;

    public T getClient(Class<? extends T> type) {
        if (service == null) {

            HttpLoggingInterceptor logging=new HttpLoggingInterceptor();
            logging.setLevel(HttpLoggingInterceptor.Level.BODY);

            OkHttpClient okHttpClient =new OkHttpClient.Builder()
                    .connectTimeout(Define.HTTP_CONNECTION_TIMEOUT, TimeUnit.MILLISECONDS)
                    .readTimeout(Define.HTTP_CONNECTION_READ_TIMEOUT, TimeUnit.MILLISECONDS)
                    .addInterceptor(logging)
                    .build();

            /*
            OkHttpClient okHttpClient = new OkHttpClient.Builder()
                    .connectTimeout(Define.HTTP_CONNECTION_TIMEOUT, TimeUnit.MILLISECONDS)
                    .readTimeout(Define.HTTP_CONNECTION_READ_TIMEOUT, TimeUnit.MILLISECONDS)
                    .addInterceptor(new Interceptor() {
                @Override
                public Response intercept(Interceptor.Chain chain) throws IOException {
                    Request original = chain.request();
                    Request request = original.newBuilder()
                            .method(original.method(), original.body())
                            .build();

                    return chain.proceed(request);
                }
            }).build();
            */

            Retrofit client = new Retrofit.Builder()
                    .baseUrl(Define.LOGISHUB_RESTFUL_DEFAULT)
                    .client(okHttpClient)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            service = client.create(type);
        }

        return service;
    }
}