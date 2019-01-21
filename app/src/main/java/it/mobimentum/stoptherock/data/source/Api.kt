package it.mobimentum.stoptherock.data.source

import it.mobimentum.stoptherock.data.AsteroidResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query


interface Api {

	@GET("feed")
	fun feed(
		@Query("start_date") startDate: String,
		@Query("end_date") endDate: String
	): Call<AsteroidResponse>
}
