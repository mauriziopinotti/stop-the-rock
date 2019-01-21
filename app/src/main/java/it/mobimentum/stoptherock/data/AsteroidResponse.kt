package it.mobimentum.stoptherock.data

import android.net.Uri
import java.net.URL

/**
 * Generated with https://plugins.jetbrains.com/plugin/9960-json-to-kotlin-class-jsontokotlinclass-
 * then manually cleaned for readability and performance.
 */
data class AsteroidResponse(
	val element_count: Int,
	val links: Map<String, URL>,
	val near_earth_objects: Map<String, List<NearEarthObject>>
) {
	val asteroids: List<Asteroid>
		get() {
			val list = mutableListOf<Asteroid>()
			near_earth_objects.forEach {
				it.value.forEach {
					list.add(
						Asteroid(
							it.name,
							it.is_potentially_hazardous_asteroid,
							it.close_approach_data[0].epoch_date_close_approach,
							it.absolute_magnitude_h,
							it.estimated_diameter.meters.estimated_diameter_min,
							it.estimated_diameter.meters.estimated_diameter_max,
							Uri.parse(it.nasa_jpl_url)
						)
					)
				}
			}

			return list
		}
}

data class NearEarthObject(
	val absolute_magnitude_h: Double,
	val close_approach_data: List<CloseApproachData>,
	val estimated_diameter: EstimatedDiameter,
	val id: String,
	val is_potentially_hazardous_asteroid: Boolean,
	val is_sentry_object: Boolean,
	val links: Map<String, URL>,
	val name: String,
	val nasa_jpl_url: String,
	val neo_reference_id: String
)

data class CloseApproachData(
	val close_approach_date: String,
	val epoch_date_close_approach: Long,
	val miss_distance: MissDistance,
	val orbiting_body: String,
	val relative_velocity: RelativeVelocity
)

data class RelativeVelocity(
	val kilometers_per_hour: String,
	val kilometers_per_second: String,
	val miles_per_hour: String
)

data class MissDistance(
	val astronomical: String,
	val kilometers: String,
	val lunar: String,
	val miles: String
)

data class EstimatedDiameter(
	val feet: Feet,
	val kilometers: Kilometers,
	val meters: Meters,
	val miles: Miles
)

data class Miles(
	val estimated_diameter_max: Double,
	val estimated_diameter_min: Double
)

data class Kilometers(
	val estimated_diameter_max: Double,
	val estimated_diameter_min: Double
)

data class Meters(
	val estimated_diameter_max: Double,
	val estimated_diameter_min: Double
)

data class Feet(
	val estimated_diameter_max: Double,
	val estimated_diameter_min: Double
)
