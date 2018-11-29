package com.evartem.remsimon.DI;

import com.evartem.remsimon.data.types.http.GeneralApi;

import dagger.Module;
import dagger.Provides;
import retrofit2.Retrofit;

@Module
abstract class RetrofitModule {

    @Provides
    static GeneralApi generalApi(Retrofit retrofit) {
      return retrofit.create(GeneralApi.class);
    }

    @Provides
    static Retrofit retrofit() {
       return new Retrofit.Builder()
               .baseUrl("https://google.com/")
               .build();
    }
}
