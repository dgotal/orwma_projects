package com.davorgotal.gotal_smartplanner.diagram

import com.davorgotal.gotal_smartplanner.model.Quote

interface QuoteListener {
    fun getQuote(randomQuote: Quote)
    fun handleError()
}
