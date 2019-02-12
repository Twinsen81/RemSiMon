package com.evartem.remsimon.di;

import com.evartem.remsimon.BuildConfig;
import com.evartem.remsimon.di.scopes.PerApplication;
import com.evartem.remsimon.data.types.http.GeneralApi;

import dagger.Module;
import dagger.Provides;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import timber.log.Timber;

@Module
public abstract class RetrofitModule {

    @Provides
    public static GeneralApi generalApi(Retrofit retrofit) {
      return retrofit.create(GeneralApi.class);
    }

    @Provides
    public static Retrofit retrofit(OkHttpClient okHttpClient) {
       return new Retrofit.Builder()
               .client(okHttpClient)
               .baseUrl("http://127.0.0.1")
               .build();
    }

    @PerApplication
    @Provides
    public static OkHttpClient okHttpClient() {
        Timber.tag("RSM-OkHttp").d("Providing okhttp...");
        OkHttpClient.Builder okhttpBuilder = new OkHttpClient.Builder();
        if (BuildConfig.DEBUG) {
            HttpLoggingInterceptor logging;
            logging = new HttpLoggingInterceptor(message -> Timber.tag("RSM-OkHttp").d(message));
            logging.setLevel(HttpLoggingInterceptor.Level.BODY);
            okhttpBuilder.addInterceptor(logging);
        }
        return okhttpBuilder
                .build();
    }
}
