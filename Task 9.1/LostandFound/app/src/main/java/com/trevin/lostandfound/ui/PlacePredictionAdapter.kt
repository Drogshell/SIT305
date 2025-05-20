package com.trevin.lostandfound.ui

import android.content.Context
import android.text.SpannableString
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import com.google.android.libraries.places.api.model.AutocompletePrediction
import com.trevin.lostandfound.R
import com.trevin.lostandfound.databinding.PlacePredictionItemBinding

class PlacePredictionAdapter(
    context: Context,
    private val predictions: List<AutocompletePrediction>
) : ArrayAdapter<AutocompletePrediction>(context, 0, predictions) {

    var currentQuery: String = ""

    override fun getCount(): Int = predictions.size

    override fun getItem(position: Int): AutocompletePrediction? = predictions.getOrNull(position)

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val binding = if (convertView == null) {
            PlacePredictionItemBinding.inflate(LayoutInflater.from(context), parent, false)
        } else {
            PlacePredictionItemBinding.bind(convertView)
        }

        val prediction = getItem(position)
        val primary = prediction?.getPrimaryText(null)?.toString() ?: ""
        binding.textViewAddress.text = prediction?.getSecondaryText(null).toString()
        val highlightColor = context.getColor(R.color.md_theme_primary)

        binding.textViewPlaceName.text = getHighLighted(primary, currentQuery, highlightColor)
        binding.imageViewPlace.setImageResource(R.drawable.ic_map_pin)

        return binding.root
    }

    private fun getHighLighted(text: String, query: String, color: Int): SpannableString {
        val spannable = SpannableString(text)
        if (query.isBlank()) return spannable

        val pattern = Regex(Regex.escape(query), RegexOption.IGNORE_CASE)
        pattern.findAll(text).forEach {
            spannable.setSpan(
                ForegroundColorSpan(color),
                it.range.first,
                it.range.last + 1,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }

        return spannable
    }

    fun sortByBestMatch() {
        if (currentQuery.isBlank()) return
        val lowerQuery = currentQuery.lowercase()
        (predictions as MutableList).sortWith((compareByDescending<AutocompletePrediction> {
            longestMatch(it.getPrimaryText(null).toString().lowercase(), lowerQuery)
        }.thenBy { it.getPrimaryText(null).toString() }))
    }

    // Finds the longest substring in 'text' that matches any part of 'query'
    private fun longestMatch(text: String, query: String): Int {
        var maxLen = 0
        for (i in query.indices) {
            for (j in i + 1..query.length) {
                val sub = query.substring(i, j)
                if (sub.length > maxLen && text.contains(sub, ignoreCase = true)) {
                    maxLen = sub.length
                }
            }
        }
        return maxLen
    }

}