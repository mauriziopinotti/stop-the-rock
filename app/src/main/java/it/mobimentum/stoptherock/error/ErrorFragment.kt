package it.mobimentum.stoptherock.error

import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.databinding.DataBindingUtil
import android.media.MediaPlayer
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import it.mobimentum.stoptherock.MainActivity
import it.mobimentum.stoptherock.R
import it.mobimentum.stoptherock.databinding.ErrorFragmentBinding


class ErrorFragment : Fragment() {

	private lateinit var viewModel: ErrorViewModel
	private lateinit var binding: ErrorFragmentBinding

	override fun onCreateView(
		inflater: LayoutInflater, container: ViewGroup?,
		savedInstanceState: Bundle?
	): View? {

		binding = DataBindingUtil.inflate(inflater, R.layout.error_fragment, container, false)
		with(binding) {
			setLifecycleOwner(this@ErrorFragment)
			listener = object : View.OnClickListener {
				override fun onClick(view: View?) {
					when (view?.id) {
						R.id.retryBtn -> startActivity(Intent(activity, MainActivity::class.java))
						R.id.noConnImg -> playStopTheRock()
					}
				}
			}
		}

		return binding.root
	}

	override fun onActivityCreated(savedInstanceState: Bundle?) {
		super.onActivityCreated(savedInstanceState)

		viewModel = ViewModelProviders.of(activity!!).get(ErrorViewModel::class.java)

		(activity as AppCompatActivity).supportActionBar?.apply {
			setDisplayHomeAsUpEnabled(false)
		}
	}

	private fun playStopTheRock() {
		// Good job, you found me!
		MediaPlayer.create(context, R.raw.stop_the_rock).start()
	}

	companion object {
		fun newInstance() = ErrorFragment()
	}
}