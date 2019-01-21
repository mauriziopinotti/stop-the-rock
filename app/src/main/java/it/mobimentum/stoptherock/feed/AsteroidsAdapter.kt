package it.mobimentum.stoptherock.feed

import android.databinding.BindingAdapter
import android.databinding.DataBindingUtil
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import it.mobimentum.stoptherock.OnAsteroidClickListener
import it.mobimentum.stoptherock.R
import it.mobimentum.stoptherock.data.Asteroid
import it.mobimentum.stoptherock.databinding.FeedItemBinding

class AsteroidsAdapter(
	private val feedViewModel: FeedViewModel
) : RecyclerView.Adapter<AsteroidsAdapter.ViewHolder>() {

	private var items: List<Asteroid> = emptyList()

	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
		return ItemViewHolder(parent, feedViewModel)
	}

	override fun getItemCount() = items.size

	override fun onBindViewHolder(holder: ViewHolder, position: Int) {
		if (holder is ItemViewHolder && items.size > position) {
			holder.bind(items[position])
		}
	}

	fun update(items: List<Asteroid>) {
		this.items = items

		notifyDataSetChanged()
	}

	abstract class ViewHolder(view: View) : RecyclerView.ViewHolder(view)

	class ItemViewHolder(
		private val parent: ViewGroup,
		private val feedViewModel: FeedViewModel,
		private val binding: FeedItemBinding = DataBindingUtil.inflate(
			LayoutInflater.from(parent.context),
			R.layout.feed_item,
			parent,
			false
		)
	) : ViewHolder(binding.root) {

		fun bind(asteroid: Asteroid) {
			with(binding) {
				item = asteroid
				listener = object : OnAsteroidClickListener {
					override fun onItemClicked(asteroid: Asteroid) {
						Log.i(TAG, "Clicked item: ${asteroid}")
						feedViewModel.openAsteroidEvent.value = asteroid
					}
				}
				executePendingBindings()
			}
		}
	}

	companion object {
		private const val TAG = "AsteroidsAdapter"

		@JvmStatic
		@BindingAdapter("asteroid_items")
		fun RecyclerView.bindItems(items: List<Asteroid>) {
			val adapter = adapter as AsteroidsAdapter
			adapter.update(items)
		}
	}
}