package it.mobimentum.stoptherock


import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProvider
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.MenuItem
import dagger.android.DispatchingAndroidInjector
import dagger.android.support.HasSupportFragmentInjector
import it.mobimentum.stoptherock.data.Asteroid
import it.mobimentum.stoptherock.details.DetailsFragment
import it.mobimentum.stoptherock.error.ErrorFragment
import it.mobimentum.stoptherock.feed.FeedFragment
import it.mobimentum.stoptherock.feed.FeedViewModel
import javax.inject.Inject

class MainActivity : AppCompatActivity(), HasSupportFragmentInjector {

	companion object {
		const val EXTRA_IS_ERROR = "EXTRA_IS_ERROR"
	}

	@Inject
	lateinit var dispatchingAndroidInjector: DispatchingAndroidInjector<Fragment>

	@Inject
	lateinit var viewModelFactory: ViewModelProvider.Factory

	private lateinit var feedViewModel: FeedViewModel
	private val feedFragment = FeedFragment.newInstance()

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.feed_activity)

		// Setup initial fragment
		if (savedInstanceState == null) {
			supportFragmentManager.beginTransaction()
				.replace(R.id.container, feedFragment)
				.commitNow()
		}

		// Subscribe to ViewModel events
		feedViewModel = ViewModelProviders.of(feedFragment, viewModelFactory).get(FeedViewModel::class.java)
		feedViewModel.openAsteroidEvent.observe(this@MainActivity, Observer<Asteroid> { asteroid ->
			if (asteroid != null) {
				openDetails(asteroid)
			}
		})
	}

	override fun onNewIntent(intent: Intent?) {
		super.onNewIntent(intent)

		when (intent?.hasExtra(EXTRA_IS_ERROR)) {
			true -> {
				// Show error
				supportFragmentManager.beginTransaction()
					.replace(R.id.container, ErrorFragment.newInstance())
					.disallowAddToBackStack()
					.commit()
			}
			false -> {
				// Back to feed list
				supportFragmentManager.beginTransaction()
					.replace(R.id.container, feedFragment)
					.commit()

				// Refresh the list
				feedViewModel.showErrorEvent.value = false
			}
		}
	}

	override fun supportFragmentInjector() = dispatchingAndroidInjector

	private fun openDetails(asteroid: Asteroid) {
		// Show the details view
		supportFragmentManager.beginTransaction()
			.replace(R.id.container, DetailsFragment.newInstance().apply {
				arguments = Bundle().apply {
					putParcelable(DetailsFragment.ARG_ASTEROID, asteroid)
				}
			})
			.addToBackStack(asteroid.name)
			.commit()
	}

	override fun onOptionsItemSelected(item: MenuItem) =
		when (item.itemId) {
			android.R.id.home -> {
				supportFragmentManager.beginTransaction()
					.replace(R.id.container, feedFragment)
					.commit()
				true
			}
			else -> super.onOptionsItemSelected(item)
		}
}