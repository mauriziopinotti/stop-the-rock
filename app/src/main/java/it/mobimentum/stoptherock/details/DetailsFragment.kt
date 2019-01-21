package it.mobimentum.stoptherock.details

import android.arch.lifecycle.ViewModelProvider
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.ShareCompat
import android.support.v7.app.AppCompatActivity
import android.view.*
import it.mobimentum.stoptherock.OnAsteroidClickListener
import it.mobimentum.stoptherock.R
import it.mobimentum.stoptherock.data.Asteroid
import it.mobimentum.stoptherock.databinding.DetailsFragmentBinding
import it.mobimentum.stoptherock.di.Injectable
import javax.inject.Inject


class DetailsFragment : Fragment(), Injectable {

	companion object {
		const val ARG_ASTEROID = "asteroid"

		fun newInstance() = DetailsFragment()
	}

	@Inject
	lateinit var viewModelFactory: ViewModelProvider.Factory

	private lateinit var detailsViewModel: DetailsViewModel
	private lateinit var binding: DetailsFragmentBinding

	private lateinit var asteroid: Asteroid

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)

		arguments?.let {
			asteroid = it.get(ARG_ASTEROID) as Asteroid
		}
	}

	override fun onCreateView(
		inflater: LayoutInflater, container: ViewGroup?,
		savedInstanceState: Bundle?
	): View? {

		binding = DataBindingUtil.inflate(inflater, R.layout.details_fragment, container, false)
		with(binding) {
			setLifecycleOwner(this@DetailsFragment)
			listener = object : OnAsteroidClickListener {
				override fun onItemClicked(asteroid: Asteroid) {
					openUrl(asteroid)
				}
			}
		}

		setHasOptionsMenu(true)

		return binding.root
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)

		detailsViewModel = ViewModelProviders.of(this, viewModelFactory).get(DetailsViewModel::class.java)

		(activity as AppCompatActivity).supportActionBar?.apply {
			title = asteroid.name
			setDisplayHomeAsUpEnabled(true)
		}

		binding.item = asteroid
	}

	override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
		inflater.inflate(R.menu.details_fragment_menu, menu)
	}

	override fun onOptionsItemSelected(item: MenuItem?): Boolean {
		when (item?.itemId) {
			R.id.menu_share -> {
				shareUrl(asteroid)

				return true
			}
		}

		return super.onOptionsItemSelected(item)
	}

	private fun openUrl(asteroid: Asteroid) {
		// Open system browser
		val intent = Intent(Intent.ACTION_VIEW)
		intent.data = asteroid.url
		startActivity(intent)
	}

	private fun shareUrl(asteroid: Asteroid) {
		// Open system chooser
		ShareCompat.IntentBuilder.from(activity)
			.setType("text/plain")
			.setChooserTitle(getString(R.string.share_title))
			.setText(getString(R.string.share_msg, asteroid.url.toString()))
			.startChooser()
	}
}
