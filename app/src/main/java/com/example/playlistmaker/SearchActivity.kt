package com.example.playlistmaker


import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity

class SearchActivity : AppCompatActivity() {
    private var currentEditText: String = EDITTEXT_DEF
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)
        val backButton = findViewById<Button>(R.id.back)
        val searchEditText = findViewById<EditText>(R.id.searchEditText)
        val clearButton = findViewById<ImageView>(R.id.clear)
        backButton.setOnClickListener {finish()}
        clearButton.setOnClickListener {
            searchEditText.text.clear()
            searchEditText.clearFocus()
            hideKeyboard(searchEditText)
        }
        val searchTextWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // empty
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                clearButton.visibility = clearButtonVisibility(s)
                currentEditText = s?.toString() ?: EDITTEXT_DEF
            }
            override fun afterTextChanged(s: Editable?) {
                // empty
            }
        }
        searchEditText.addTextChangedListener(searchTextWatcher)
        searchEditText.requestFocus()
        showKeyboard(searchEditText)
    }
    private fun clearButtonVisibility(s: CharSequence?): Int {
        return if (s.isNullOrEmpty()) {
            android.view.View.GONE
        } else {
            android.view.View.VISIBLE
        }
    }
    private fun performSearch(query: String) {
          // empty
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
    }

    companion object {
        private const val EDITTEXT_KEY = "EDITTEXT_KEY"
        private const val EDITTEXT_DEF = ""
    }
    }
