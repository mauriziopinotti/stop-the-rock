package it.mobimentum.stoptherock.feed

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import it.mobimentum.stoptherock.MainActivity
import it.mobimentum.stoptherock.R
import it.mobimentum.stoptherock.databinding.FeedFragmentBinding
import kotlinx.android.synthetic.main.feed_fragment.*

class FeedFragment : Fragment() {

	companion object {
		fun newInstance() = FeedFragment()
	}

	private lateinit var viewModel: FeedViewModel
	private lateinit var binding: FeedFragmentBinding

	override fun onCreateView(
		inflater: LayoutInflater, container: ViewGroup?,
		savedInstanceState: Bundle?
	): View {
		binding = DataBindingUtil.inflate(inflater, R.layout.feed_fragment, container, false)
		binding.setLifecycleOwner(this)

		return binding.root
	}


	override fun onActivityCreated(savedInstanceState: Bundle?) {
		super.onActivityCreated(savedInstanceState)

		viewModel = ViewModelProviders.of(activity!!).get(FeedViewModel::class.java)

		(activity as AppCompatActivity).supportActionBar?.apply {
			title = getString(R.string.feed_activity_title)
			setDisplayHomeAsUpEnabled(false)
		}

		// Init RecyclerView
		val layoutManager = LinearLayoutManager(context)
		asteroidsList.layoutManager = layoutManager
		asteroidsList.hasFixedSize()
		asteroidsList.adapter = AsteroidsAdapter(viewModel)
		asteroidsList.addItemDecoration(DividerItemDecoration(context, layoutManager.orientation))
		asteroidsList.endless { viewModel.fetchItems(false) }

		// Subscribe to RecyclerView events
		viewModel.addItems.subscribe(this) {
			// Elements added to the asteroid list
			(asteroidsList.adapter as AsteroidsAdapter).notifyItemRangeInserted(data.positionStart, data.count)
		}
		viewModel.removeItems.subscribe(this) {
			// Elements removed from the asteroid list
			(asteroidsList.adapter as AsteroidsAdapter).notifyItemRangeRemoved(data.positionStart, data.count)

			// Re-init endless scroll when items are removed, or infninte scroll will break
			asteroidsList.endless { viewModel.fetchItems(false) }
		}

		// Subscribe to error events
		viewModel.showErrorEvent.observe(this, Observer { error ->
			when (error) {
				// Error state removed, refresh the list
				false -> viewModel.onRefresh()

				// Error state set, show the error view
				true -> startActivity(
					Intent(activity, MainActivity::class.java)
						.putExtra(MainActivity.EXTRA_IS_ERROR, true)
				)
			}
		})

		binding.viewModel = viewModel
	}
}
