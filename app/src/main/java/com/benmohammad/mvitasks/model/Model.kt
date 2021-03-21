package com.benmohammad.mvitasks.model

import com.benmohammad.mvitasks.intent.Intent
import io.reactivex.Observable


interface Model<S> {

    fun process(intent: Intent<S>)

    fun modelState(): Observable<S>

}