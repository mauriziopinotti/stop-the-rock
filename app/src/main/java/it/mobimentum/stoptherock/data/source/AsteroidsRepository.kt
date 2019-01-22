package it.mobimentum.stoptherock.data.source

import android.util.Log
import it.mobimentum.stoptherock.BuildConfig
import it.mobimentum.stoptherock.data.Asteroid
import it.mobimentum.stoptherock.data.AsteroidResponse
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AsteroidsRepository @Inject constructor(
	private val api: Api
) {

	companion object {
		private const val TAG = "Repository"

		private val DATE_FORMAT = SimpleDateFormat("yyyy-MM-dd", Locale.US)
		private const val DAY = 86400 * 1000
	}

	private var nextDate = Date()

	fun reset() {
		nextDate = Date()
	}

	fun getNextPage(): List<Asteroid>? {

//        android.os.SystemClock.sleep(5000) // XXX DEBUG

		val result = mutableListOf<Asteroid>()

		// Define the time range for the first week
		val week1 = doRequest(nextDate, Date(nextDate.time + 6 * DAY))
		if (week1 == null) return null
		else if (!week1.isEmpty()) {
			result.addAll(week1)

			// Define the time range for the second week
			val week2 = doRequest(Date(nextDate.time + 7 * DAY), Date(nextDate.time + 13 * DAY))
			if (week2 == null) return null
			result.addAll(week2)
		}

		// Save pagination for infinite scroll
		nextDate = Date(nextDate.time + 14 * DAY)

//		return emptyList() // XXX DEBUG

		return result
	}

	private fun doRequest(startDate: Date, endDate: Date): List<Asteroid>? {
		val request = api.feed(DATE_FORMAT.format(startDate), DATE_FORMAT.format(endDate))
		var response: AsteroidResponse? = null
		try {
			Log.d(TAG, "Executing request: ${request.request().url()}")
			response = request.execute().body()
			if (BuildConfig.DEBUG) Log.v(TAG, "Got response: ${response}");
		}
		catch (e: Exception) {
			Log.e(TAG, "Error getting asteroids feed", e)
		}

		return if (response != null) response.asteroids else null
	}
}