package it.mobimentum.stoptherock

import android.app.Activity
import android.app.Application
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasActivityInjector
import it.mobimentum.stoptherock.di.AppInjector
import javax.inject.Inject

class RockApp : Application(), HasActivityInjector {

	@Inject
	lateinit var dispatchingAndroidInjector: DispatchingAndroidInjector<Activity>

	override fun onCreate() {
		super.onCreate()

		AppInjector.init(this)
	}

	override fun activityInjector() = dispatchingAndroidInjector
}
