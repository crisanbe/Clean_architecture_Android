package com.platzi.android.rickandmorty.presentation

//<out T> para utizar esta clase donde queramos en toda la app
data class Event<out T>(private  val content: T) {

    private  var hasBeenHandled = false

    fun getContentIfNotHandled(): T? = if (hasBeenHandled){
        null
    }else{
        hasBeenHandled = true
        content
    }
}