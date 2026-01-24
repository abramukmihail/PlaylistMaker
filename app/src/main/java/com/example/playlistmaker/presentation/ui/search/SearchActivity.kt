package com.example.playlistmaker.presentation.ui.search

import android.os.Bundle
import android.content.Intent
import android.text.Editable
import android.text.TextWatcher
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.playlistmaker.presentation.creator.Creator
import com.example.playlistmaker.R
import com.example.playlistmaker.domain.api.HistoryInteractor
import com.example.playlistmaker.domain.api.SearchInteractor
import com.example.playlistmaker.domain.models.Track
import com.example.playlistmaker.presentation.ui.audioplayer.AudioPlayerActivity
import android.widget.ProgressBar
import android.os.Handler
import android.os.Looper

class SearchActivity : AppCompatActivity(), SearchInteractor.SearchConsumer
    {

    private lateinit var searchInteractor: SearchInteractor
    private lateinit var historyInteractor: HistoryInteractor

    private var currentEditText: String = EDITTEXT_DEF
    private lateinit var nothingFoundLayout: LinearLayout
    private lateinit var noConnectionLayout: LinearLayout
    private lateinit var refreshButton: Button
    private lateinit var adapter: TrackAdapter
    private var lastSearchQuery: String = ""
    private lateinit var searchHistoryLayout: LinearLayout
    private lateinit var clearHistoryButton: Button
    private lateinit var historyRecyclerView: RecyclerView
    private lateinit var historyAdapter: TrackAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var progressBar: ProgressBar
    private val handler = Handler(Looper.getMainLooper())
    private var isClickAllowed = true
    private val searchRunnable = Runnable { performSearch(currentEditText) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)


        searchInteractor = Creator.provideSearchInteractor()
        historyInteractor = Creator.provideHistoryInteractor(this)

        val backButton = findViewById<Button>(R.id.back)
        val searchEditText = findViewById<EditText>(R.id.searchEditText)
        val clearButton = findViewById<ImageView>(R.id.clear)
        recyclerView = findViewById<RecyclerView>(R.id.recyclerView)

        searchHistoryLayout = findViewById(R.id.searchHistory)
        clearHistoryButton = findViewById(R.id.clearHistoryButton)
        historyRecyclerView = findViewById(R.id.tracksHistory)
        nothingFoundLayout = findViewById(R.id.nothingFound)
        noConnectionLayout = findViewById(R.id.noConnection)
        refreshButton = findViewById(R.id.refreshButton)
        progressBar = findViewById(R.id.progressBar)

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

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        historyRecyclerView.layoutManager = LinearLayoutManager(this)
        historyRecyclerView.adapter = historyAdapter

        backButton.setOnClickListener { finish() }
        clearButton.setOnClickListener {
            searchEditText.text.clear()
            searchEditText.clearFocus()
            hideKeyboard(searchEditText)
            adapter.updateTracks(emptyList())
            clearErrors()
            updateSearchHistoryVisibility()
        }

        clearHistoryButton.setOnClickListener {
            historyInteractor.clearSearchHistory()
            val history = historyInteractor.getSearchHistory()
            historyAdapter.updateTracks(history)
            updateSearchHistoryVisibility()
        }

        refreshButton.setOnClickListener {
            if (lastSearchQuery.isNotEmpty()) {
                performSearch(lastSearchQuery)
            }
        }

        val searchTextWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // empty
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                clearButton.visibility = clearButtonVisibility(s)
                currentEditText = s?.toString() ?: EDITTEXT_DEF
                if (s.isNullOrEmpty()) {
                    clearErrors()
                    adapter.updateTracks(emptyList())
                }
                else {
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
            updateSearchHistoryVisibility()
        }
        searchEditText.requestFocus()
        showKeyboard(searchEditText)

        historyInteractor.getSearchHistory().let { history ->
            historyAdapter.updateTracks(history)
        }
        updateSearchHistoryVisibility()
    }

    override fun consume(foundTracks: List<Track>) {
        handler.post {
            showProgressBar(false)
            if (foundTracks.isNotEmpty()) {
                adapter.updateTracks(foundTracks)
                nothingFoundLayout.visibility = View.GONE
                noConnectionLayout.visibility = View.GONE
            } else {
                if (lastSearchQuery.isNotEmpty()) {
                    showNoConnection()
                } else {
                    showNothingFound()
                }
            }
    }   }


    private fun searchDebounce() {
        handler.removeCallbacks(searchRunnable)
        handler.postDelayed(searchRunnable, SEARCH_DEBOUNCE_DELAY)
    }

    private fun clickDebounce() : Boolean {
        val current = isClickAllowed
        if (isClickAllowed) {
            isClickAllowed = false
            handler.postDelayed({ isClickAllowed = true }, CLICK_DEBOUNCE_DELAY)
        }
        return current
    }

    private fun onTrackClick(track: Track) {
        historyInteractor.addTrackToHistory(track)
        hideKeyboard(findViewById(R.id.searchEditText))
        startActivity(Intent(this, AudioPlayerActivity::class.java).putExtra(
            AudioPlayerActivity.TRACK_EXTRA,
            track
        ))
    }

    private fun updateSearchHistoryVisibility() {
        val searchEditText = findViewById<EditText>(R.id.searchEditText)
        val shouldShowHistory = searchEditText.hasFocus() &&
                searchEditText.text.isEmpty() &&
                historyInteractor.getSearchHistory().isNotEmpty()
        searchHistoryLayout.visibility = if (shouldShowHistory) View.VISIBLE else View.GONE
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

        lastSearchQuery = query
        clearErrors()
        showProgressBar(true)

        searchInteractor.searchTracks(query, this)
    }

    private fun showProgressBar(show: Boolean) {
        progressBar.visibility = if (show) View.VISIBLE else View.GONE
        recyclerView.visibility = if (show) View.GONE else View.VISIBLE
    }

    private fun showNothingFound() {
        nothingFoundLayout.visibility = View.VISIBLE
        noConnectionLayout.visibility = View.GONE
        adapter.updateTracks(emptyList())
        hideKeyboard(findViewById<EditText>(R.id.searchEditText))
    }

    private fun showNoConnection() {
        nothingFoundLayout.visibility = View.GONE
        noConnectionLayout.visibility = View.VISIBLE
        adapter.updateTracks(emptyList())
    }

    private fun clearErrors() {
        nothingFoundLayout.visibility = View.GONE
        noConnectionLayout.visibility = View.GONE
    }

    private fun showKeyboard(editText: EditText) {
        val inputMethodManager = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT)
    }

    private fun hideKeyboard(editText: EditText) {
        val inputMethodManager = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(editText.windowToken, 0)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(EDITTEXT_KEY, currentEditText)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        currentEditText = savedInstanceState.getString(EDITTEXT_KEY, EDITTEXT_DEF)
        findViewById<EditText>(R.id.searchEditText).setText(currentEditText)
        updateSearchHistoryVisibility()
    }

    companion object {
        private const val EDITTEXT_KEY = "EDITTEXT_KEY"
        private const val EDITTEXT_DEF = ""
        private const val SEARCH_DEBOUNCE_DELAY = 2000L
        private const val CLICK_DEBOUNCE_DELAY = 1000L
    }
}