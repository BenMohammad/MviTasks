package com.benmohammad.mvitasks.intent

interface Intent<T> {
    fun reduce(oldState: T): T

    fun <T> intent(block: T.()-> T): Intent<T> = object : Intent<T> {
        override fun reduce(oldState: T): T {
            return block(oldState)
        }
    }

    fun <T> sideEffect(block: T.() -> Unit): Intent<T> = object : Intent<T> {
        override fun reduce(oldState: T): T {
            return oldState.apply(block)
        }
    }
}