package com.example.playlistmaker.search.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import org.koin.androidx.viewmodel.ext.android.viewModel
import com.example.playlistmaker.databinding.FragmentSearchBinding
import com.example.playlistmaker.search.domain.models.SearchState
import com.example.playlistmaker.search.domain.models.Track
import com.example.playlistmaker.search.ui.activity.adapter.TrackAdapter
import com.example.playlistmaker.search.ui.viewmodel.SearchViewModel
import com.example.playlistmaker.player.ui.fragments.AudioPlayerFragment
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.playlistmaker.R


class SearchFragment : Fragment() {

    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!
    private val viewModel: SearchViewModel by viewModel()

    private var currentEditText: String = EDITTEXT_DEF
    private lateinit var adapter: TrackAdapter
    private lateinit var historyAdapter: TrackAdapter
    private val handler = Handler(Looper.getMainLooper())
    private var isClickAllowed = true
    private val searchRunnable = Runnable { performSearch(currentEditText) }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = TrackAdapter(emptyList()) { track ->
            if (clickDebounce()) {
                onTrackClick(track)
            }
        }
        historyAdapter = TrackAdapter(emptyList()) { track ->
            if (clickDebounce()) {
                onTrackClick(track)
            }
        }

        setupViews()
        setupObservers()
        loadSearchHistory()

        savedInstanceState?.let {
            currentEditText = it.getString(EDITTEXT_KEY, EDITTEXT_DEF)
            binding.searchEditText.setText(currentEditText)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(EDITTEXT_KEY, currentEditText)
    }

    private fun setupViews() {
        with(binding) {
            recyclerView.layoutManager = LinearLayoutManager(requireContext())
            recyclerView.adapter = adapter

            tracksHistory.layoutManager = LinearLayoutManager(requireContext())
            tracksHistory.adapter = historyAdapter

            clear.setOnClickListener {
                searchEditText.text.clear()
                searchEditText.clearFocus()
                hideKeyboard(searchEditText)
                viewModel.cancelSearch()
                adapter.updateTracks(emptyList())
                clearErrors()
                updateSearchHistoryVisibility()
            }

            clearHistoryButton.setOnClickListener {
                searchEditText.clearFocus()
                hideKeyboard(searchEditText)
                viewModel.clearHistory()
            }

            refreshButton.setOnClickListener {
                if (currentEditText.isNotEmpty()) {
                    performSearch(currentEditText)
                }
            }

            val searchTextWatcher = object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                    // empty
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    clear.visibility = clearButtonVisibility(s)
                    currentEditText = s?.toString() ?: EDITTEXT_DEF
                    if (s.isNullOrEmpty()) {
                        clearErrors()
                        viewModel.cancelSearch()
                        adapter.updateTracks(emptyList())
                        viewModel.loadHistory()
                    } else {
                        searchDebounce()
                    }
                    updateSearchHistoryVisibility()
                }

                override fun afterTextChanged(s: Editable?) {
                    // empty
                }
            }

            searchEditText.addTextChangedListener(searchTextWatcher)
            searchEditText.setOnFocusChangeListener { _, hasFocus ->
                if (hasFocus) {
                    viewModel.loadHistory()
                }
                updateSearchHistoryVisibility()
            }

            searchEditText.requestFocus()
            showKeyboard(searchEditText)
        }
    }

    private fun setupObservers() {
        viewModel.searchState.observe(viewLifecycleOwner) { state ->
            handleSearchState(state)
        }

        viewModel.historyState.observe(viewLifecycleOwner) { history ->
            historyAdapter.updateTracks(history)
            updateSearchHistoryVisibility()
        }
    }

    private fun handleSearchState(state: SearchState) {
        when (state) {
            is SearchState.Empty -> {
                showSearchHistory()
            }

            is SearchState.Loading -> {
                showProgressBar(true)
            }

            is SearchState.EmptyResult -> {
                showProgressBar(false)
                showNothingFound()
            }

            is SearchState.Content -> {
                showProgressBar(false)
                adapter.updateTracks(state.tracks)
                binding.nothingFound.visibility = View.GONE
                binding.noConnection.visibility = View.GONE
                binding.recyclerView.visibility = View.VISIBLE
            }

            is SearchState.Error.NoConnection -> {
                showProgressBar(false)
                showNoConnection()
            }

            is SearchState.Error.NetworkError -> {
                showProgressBar(false)
                showNoConnection()
            }
        }
    }

    private fun searchDebounce() {
        handler.removeCallbacks(searchRunnable)
        handler.postDelayed(searchRunnable, SEARCH_DEBOUNCE_DELAY)
    }

    private fun clickDebounce(): Boolean {
        val current = isClickAllowed
        if (isClickAllowed) {
            isClickAllowed = false
            handler.postDelayed({ isClickAllowed = true }, CLICK_DEBOUNCE_DELAY)
        }
        return current
    }

    private fun onTrackClick(track: Track) {
        viewModel.addToSearchHistory(track)
        updateSearchHistoryVisibility()
        hideKeyboard(binding.searchEditText)
        val bundle = Bundle().apply {
            putParcelable(AudioPlayerFragment.TRACK_EXTRA, track)
        }

        findNavController().navigate(
            R.id.action_searchFragment_to_audioPlayerFragment,
            bundle
        )
    }

    private fun updateSearchHistoryVisibility() {
        with(binding) {
            val shouldShowHistory = searchEditText.hasFocus() &&
                    searchEditText.text.isEmpty() &&
                    viewModel.historyState.value?.isNotEmpty() == true
            searchHistory.visibility = if (shouldShowHistory) View.VISIBLE else View.GONE
        }
    }

    private fun loadSearchHistory() {
        viewModel.loadHistory()
    }

    private fun showSearchHistory() {
        with(binding) {
            searchHistory.visibility = View.VISIBLE
            recyclerView.visibility = View.GONE
            nothingFound.visibility = View.GONE
            noConnection.visibility = View.GONE
            progressBar.visibility = View.GONE
        }
    }

    private fun clearButtonVisibility(s: CharSequence?): Int {
        return if (s.isNullOrEmpty()) {
            View.GONE
        } else {
            View.VISIBLE
        }
    }

    private fun performSearch(query: String) {
        if (query.isBlank()) return

        clearErrors()
        showProgressBar(true)
        viewModel.searchDebounced(query)
    }

    private fun showProgressBar(show: Boolean) {
        with(binding) {
            progressBar.visibility = if (show) View.VISIBLE else View.GONE
            recyclerView.visibility = if (show) View.GONE else View.VISIBLE
        }
    }

    private fun showNothingFound() {
        with(binding) {
            nothingFound.visibility = View.VISIBLE
            noConnection.visibility = View.GONE
            adapter.updateTracks(emptyList())
            hideKeyboard(searchEditText)
        }
    }

    private fun showNoConnection() {
        with(binding) {
            nothingFound.visibility = View.GONE
            noConnection.visibility = View.VISIBLE
            adapter.updateTracks(emptyList())
        }
    }

    private fun clearErrors() {
        with(binding) {
            nothingFound.visibility = View.GONE
            noConnection.visibility = View.GONE
        }
    }

    private fun showKeyboard(editText: EditText) {
        val inputMethodManager = ContextCompat.getSystemService(
            requireContext(),
            InputMethodManager::class.java
        )
        inputMethodManager?.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT)
    }


    private fun hideKeyboard(editText: android.widget.EditText) {
        val inputMethodManager = ContextCompat.getSystemService(
            requireContext(),
            InputMethodManager::class.java
        )
        inputMethodManager?.hideSoftInputFromWindow(editText.windowToken, 0)
    }

    override fun onResume() {
        super.onResume()
        viewModel.loadHistory()
        updateSearchHistoryVisibility()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        handler.removeCallbacks(searchRunnable)
        _binding = null
    }

    companion object {
        private const val EDITTEXT_KEY = "EDITTEXT_KEY"
        private const val EDITTEXT_DEF = ""
        private const val SEARCH_DEBOUNCE_DELAY = 2000L
        private const val CLICK_DEBOUNCE_DELAY = 1000L
    }

}