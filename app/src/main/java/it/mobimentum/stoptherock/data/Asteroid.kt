package it.mobimentum.stoptherock.data

import android.net.Uri
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.text.SimpleDateFormat
import java.util.*

/**
 * Immutable model class for an Asteroid.
 */
@Parcelize
data class Asteroid constructor(
	var name: String,
	var isPotentiallyHazardousAsteroid: Boolean,
	var epochDateCloseApproach: Long,
	var absoluteBrightness: Double,
	var estimatedDiameterMin: Double,
	var estimatedDiameterMax: Double,
	var url: Uri
) : Parcelable {

	val formattedDate: String
		get() = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(Date(epochDateCloseApproach))

	val formattedDiameter: String
		get() = String.format(
			Locale.getDefault(), "%d - %d",
			Math.floor(estimatedDiameterMin).toInt(), Math.ceil(estimatedDiameterMax).toInt()
		)

	val formattedBrightness: String
		get() = String.format(
			Locale.getDefault(), "%.1f", absoluteBrightness
		)

	val isThreat: Boolean
		get() = isPotentiallyHazardousAsteroid

	override fun toString(): String {
		return "Asteroid(name='$name', isPotentiallyHazardousAsteroid=$isPotentiallyHazardousAsteroid, epochDateCloseApproach=$epochDateCloseApproach)"
	}
}
