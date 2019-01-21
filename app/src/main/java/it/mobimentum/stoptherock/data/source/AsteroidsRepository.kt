package it.mobimentum.stoptherock.data.source

import android.util.Log
import it.mobimentum.stoptherock.BuildConfig
import it.mobimentum.stoptherock.data.Asteroid
import it.mobimentum.stoptherock.data.AsteroidResponse
import java.text.SimpleDateFormat
import java.util.*

class AsteroidsRepository {

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

//        if (nextDate.time < Date().time + 14 * DAY) {
//            Log.w(TAG, "2 weeks limit reached!")
//
//            return emptyList()
//        }

		// Define the time range for the next request
		val startDate = nextDate
		val endDate = Date(startDate.time + 7 * DAY)
		val request = apiServe.feed(DATE_FORMAT.format(startDate), DATE_FORMAT.format(endDate))

		// Do request
		var response: AsteroidResponse? = null
		try {
			Log.d(TAG, "Excuting request: ${request.request().url()}")
			response = request.execute().body()
			if (BuildConfig.DEBUG) Log.v(TAG, "Got response: ${response}");
		}
		catch (e: Exception) {
			Log.e(TAG, "Error getting asteroids feed", e)
		}

		// Save pagination for infinite scroll
		nextDate = Date(endDate.time + 7 * DAY)

//		return emptyList() // XXX DEBUG

		return if (response != null) response.asteroids else null
	}
}