package it.mobimentum.stoptherock.feed

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.databinding.ObservableBoolean
import android.os.Handler
import android.os.Looper
import android.util.Log
import it.mobimentum.stoptherock.SingleLiveEvent
import it.mobimentum.stoptherock.data.Asteroid
import it.mobimentum.stoptherock.data.source.AsteroidsRepository
import java.util.concurrent.Executor
import java.util.concurrent.Executors
import javax.inject.Inject

data class Page(val positionStart: Int, val count: Int)

class FeedViewModel @Inject constructor(
	val repository: AsteroidsRepository
) : ViewModel() {

	// Handlers
	private val networkThread: Executor = Executors.newFixedThreadPool(1)!!
	private val mainThread: Executor = MainThreadExecutor()

	val loading = ObservableBoolean(false)
	val empty = ObservableBoolean(false)

	private val list = mutableListOf<Asteroid>()

	val items: MutableLiveData<List<Asteroid>> = MutableLiveData<List<Asteroid>>().apply { value = list }
	var addItems: MutableLiveData<Event<Page>> = MutableLiveData()
	val removeItems: MutableLiveData<Event<Page>> = MutableLiveData()

	internal val openAsteroidEvent = SingleLiveEvent<Asteroid>()
	internal val showErrorEvent = SingleLiveEvent<Boolean>()

	init {
		fetchItems(true)
	}

	fun onRefresh() {
		fetchItems(true)
	}

	fun fetchItems(initialLoading: Boolean) {
		// Ensure we're not overlapping requests
		if (!loading.get()) {
			loading.set(true)

			// Clean the list on refresh
			if (initialLoading) {
				val size = list.size
				list.clear()
				removeItems(0, size)

				repository.reset()
			}

			// Don't show the "no items" message while loading
			empty.set(false)

			// Get asteroids
			networkThread.execute {
				val position = list.size
				val newItems = repository.getNextPage()

				mainThread.execute {
					if (newItems != null) {
						// Add new items to the list
						list.addAll(newItems)
						addItems(position, newItems.size)
					}
					else {
						// Show the error view
						showError()
					}

					Log.d(TAG, "New list size: ${list.size}")

					// We're all set!
					loading.set(false)
					empty.set(list.size == 0)
				}
			}
		}
	}

	private fun showError() {
		showErrorEvent.value = true
	}

	private fun addItems(positionStart: Int, count: Int) {
		addItems.value = Event(Page(positionStart, count))
	}

	private fun removeItems(positionStart: Int, count: Int) {
		removeItems.value = Event(Page(positionStart, count))
	}

	companion object {
		private const val TAG = "FeedViewModel"
	}
}

class MainThreadExecutor : Executor {

	private val mainThreadHandler = Handler(Looper.getMainLooper())

	override fun execute(command: Runnable) {
		mainThreadHandler.post(command)
	}
}