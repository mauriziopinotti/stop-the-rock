package it.mobimentum.stoptherock.feed

import android.arch.lifecycle.LifecycleOwner
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.Observer
import android.support.v4.view.accessibility.AccessibilityEventCompat.TYPE_VIEW_ACCESSIBILITY_FOCUSED
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.RecyclerViewAccessibilityDelegate
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.view.accessibility.AccessibilityEvent

/**
 * Original code: https://github.com/ciandt-mobile/android-recyclerview-binding
 */
class EndlessScroll(
	private val recyclerView: RecyclerView,
	val visibleThreshold: Int = 10,
	private val loadMore: () -> Unit
) :
	RecyclerView.OnScrollListener() {

	private var previousTotal = 1
	private var loading = true

	class AccessibilityDelegate(recyclerView: RecyclerView, var changeRow: () -> Unit) :
		RecyclerViewAccessibilityDelegate(recyclerView) {

		override fun onRequestSendAccessibilityEvent(
			host: ViewGroup?,
			child: View?,
			event: AccessibilityEvent?
		): Boolean {
			event?.let {
				if (it.eventType == TYPE_VIEW_ACCESSIBILITY_FOCUSED) {
					changeRow()
				}
			}

			return super.onRequestSendAccessibilityEvent(host, child, event)
		}
	}

	init {
		recyclerView.setAccessibilityDelegateCompat(
			AccessibilityDelegate(
				recyclerView,
				::changeRowByAccessibility
			)
		)
	}

	private fun changeRowByAccessibility() {
		if (this.recyclerView.scrollState != RecyclerView.SCROLL_STATE_IDLE) {
			return
		}

		loading()
	}

	init {
		if (D) Log.w(TAG, "EndlessScroll created")
	}

	override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
		super.onScrolled(recyclerView, dx, dy)

		if (D) Log.d(TAG, "onScrolled(): scrollState=${this.recyclerView.scrollState}")
		if (this.recyclerView.scrollState == RecyclerView.SCROLL_STATE_IDLE) {
			return
		}

		loading()
	}

	private fun loading() {
		val visibleItemCount = recyclerView.childCount
		val totalItemCount = recyclerView.layoutManager?.itemCount!!
		val firstVisibleItem = (recyclerView.layoutManager as LinearLayoutManager).findFirstVisibleItemPosition()

		if (loading && totalItemCount > previousTotal) {
			loading = false
			previousTotal = totalItemCount
		}

		if (D) Log.d(
			TAG,
			"loading(): loading=$loading th=${totalItemCount - visibleItemCount} <= ${firstVisibleItem + visibleThreshold}"
		);
		if (!loading && (totalItemCount - visibleItemCount) <= (firstVisibleItem + visibleThreshold)) {
			if (D) Log.i(TAG, "loadMore()");
			loadMore()
			loading = true
		}
	}

	companion object {
		private const val TAG = "EndlessScroll"

		private const val D = false
	}
}

fun RecyclerView.endless(visibleThreshold: Int = 10, loadMore: () -> Unit) {
	this.clearOnScrollListeners()
	this.addOnScrollListener(EndlessScroll(this, visibleThreshold, loadMore))
}

open class Event<out T>(val data: T) {

	var hasBeenHandled = false
		protected set

	fun trigger(body: Event<T>.() -> Unit) {
		if (!hasBeenHandled) {
			hasBeenHandled = true
			body(this)
		}
	}
}

fun <T : Event<*>> LiveData<T>.subscribe(owner: LifecycleOwner, body: T.() -> Unit) {
	observe(owner, Observer {
		it?.trigger { body(it) }
	})
}