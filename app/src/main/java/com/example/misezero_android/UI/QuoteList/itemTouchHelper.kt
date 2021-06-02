package com.example.misezero_android.UI.QuoteList

interface itemTouchHelper {
    fun onItemMove(from : Int, to: Int)
    fun onItemSwipe(position : Int)
    fun onItemClick(position: Int)
}