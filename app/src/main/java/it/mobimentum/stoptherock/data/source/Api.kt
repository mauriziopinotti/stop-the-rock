package it.mobimentum.stoptherock.data.source

import it.mobimentum.stoptherock.BuildConfig
import it.mobimentum.stoptherock.data.AsteroidResponse
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query


interface Api {

	@GET("feed")
	fun feed(
		@Query("start_date") startDate: String,
		@Query("end_date") endDate: String
	): Call<AsteroidResponse>

	companion object {
		fun create(): Api {
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
}

val apiServe by lazy {
	Api.create()
}
