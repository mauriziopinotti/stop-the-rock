package it.mobimentum.stoptherock.di

import dagger.Module
import dagger.Provides
import it.mobimentum.stoptherock.BuildConfig
import it.mobimentum.stoptherock.data.source.Api
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module(includes = [ViewModelModule::class])
class AppModule {

	@Singleton
	@Provides
	fun provideApi(): Api {
		val client = OkHttpClient.Builder()
			.addInterceptor { chain ->
				// Add API_KEY to every request
				var request = chain.request()
				val url = request.url().newBuilder()
					.addQueryParameter("api_key", BuildConfig.NEO_API_KEY)
					.build()
				request = request.newBuilder()
					.url(url)
					.build()
				chain.proceed(request)
			}
			// XXX DEBUG Optional interceptor to emulate setLogLevel in Retrofit 1
//                .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
			.build()

		val retrofit = Retrofit.Builder()
			.addConverterFactory(GsonConverterFactory.create())
			.client(client)
			.baseUrl(BuildConfig.NEO_BASE_URL)
			.build()

		return retrofit.create(Api::class.java)
	}
}
