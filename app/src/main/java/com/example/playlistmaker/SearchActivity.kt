package com.example.playlistmaker

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
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
class SearchActivity : AppCompatActivity() {
    private var currentEditText: String = EDITTEXT_DEF
    private val retrofit = Retrofit.Builder()
        .baseUrl(ITUNES_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    private val itunesService = retrofit.create(ItunesApi::class.java)
    private lateinit var nothingFoundLayout: LinearLayout
    private lateinit var noConnectionLayout: LinearLayout
    private lateinit var refreshButton: Button
    private lateinit var adapter: TrackAdapter
    private var lastSearchQuery: String = ""
    private lateinit var searchHistoryLayout: LinearLayout
    private lateinit var clearHistoryButton: Button
    private lateinit var historyRecyclerView: RecyclerView
    private lateinit var historyAdapter: TrackAdapter
    private lateinit var searchHistory: SearchHistory

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        val backButton = findViewById<Button>(R.id.back)
        val searchEditText = findViewById<EditText>(R.id.searchEditText)
        val clearButton = findViewById<ImageView>(R.id.clear)
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        val sharedPreferences = getSharedPreferences("search_history", MODE_PRIVATE)

        searchHistory = SearchHistory(sharedPreferences)
        searchHistoryLayout = findViewById(R.id.searchHistory)
        clearHistoryButton = findViewById(R.id.clearHistoryButton)
        historyRecyclerView = findViewById(R.id.tracksHistory)
        nothingFoundLayout = findViewById(R.id.nothingFound)
        noConnectionLayout = findViewById(R.id.noConnection)
        refreshButton = findViewById(R.id.refreshButton)

        adapter = TrackAdapter(emptyList()) { track -> onTrackClick(track) }
        historyAdapter = TrackAdapter(emptyList()) { track -> onTrackClick(track) }

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        historyRecyclerView.layoutManager = LinearLayoutManager(this)
        historyRecyclerView.adapter = historyAdapter

        backButton.setOnClickListener {finish()}
        clearButton.setOnClickListener {
            searchEditText.text.clear()
            searchEditText.clearFocus()
            hideKeyboard(searchEditText)
            adapter.updateTracks(emptyList())
            clearErrors()
            updateSearchHistoryVisibility()
        }

        clearHistoryButton.setOnClickListener {
            searchHistory.clearSearchHistory()
            updateHistory()
            updateSearchHistoryVisibility()
        }

        refreshButton.setOnClickListener {
            if (lastSearchQuery.isNotEmpty()) {
                performSearch(lastSearchQuery, adapter)
            }
        }
        searchEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == android.view.inputmethod.EditorInfo.IME_ACTION_DONE) {
                performSearch(searchEditText.text.toString(), adapter)
                hideKeyboard(searchEditText)
                true
            }
            false
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
        updateHistory()
        updateSearchHistoryVisibility()
    }
    private fun onTrackClick(track: Track) {
        searchHistory.addTrackToHistory(track)
        updateHistory()
        hideKeyboard(findViewById(R.id.searchEditText))
        startActivity(Intent(this, AudioPlayerActivity::class.java).putExtra(
            AudioPlayerActivity.TRACK_EXTRA,
            track
        ))
    }
    private fun updateHistory() {
        val historyTracks = searchHistory.getSavedTracks()
        historyAdapter.updateTracks(historyTracks)
    }
    private fun updateSearchHistoryVisibility() {
        val searchEditText = findViewById<EditText>(R.id.searchEditText)
        val shouldShowHistory = searchEditText.hasFocus() &&
                searchEditText.text.isEmpty() &&
                !searchHistory.isHistoryEmpty()
        searchHistoryLayout.visibility = if (shouldShowHistory) View.VISIBLE else View.GONE
    }
    private fun clearButtonVisibility(s: CharSequence?): Int {
        return if (s.isNullOrEmpty()) {
            View.GONE
        } else {
            View.VISIBLE
        }
    }
    private fun performSearch(query: String, adapter: TrackAdapter) {
        if (query.isBlank()) return

        lastSearchQuery = query
        clearErrors()

        itunesService.findTrack(query).enqueue(object : Callback<TrackResponse> {
            override fun onResponse(call: Call<TrackResponse>, response: Response<TrackResponse>) {
                if (response.isSuccessful) {
                    val results = response.body()?.results
                    if (results?.isNotEmpty() == true) {
                        adapter.updateTracks(results)
                    } else {
                        showNothingFound()
                    }
                } else {
                    showNoConnection()
                }
            }

            override fun onFailure(call: Call<TrackResponse>, t: Throwable) {
                showNoConnection()
            }
        })
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
        private const val ITUNES_URL = "https://itunes.apple.com"
    }
}
