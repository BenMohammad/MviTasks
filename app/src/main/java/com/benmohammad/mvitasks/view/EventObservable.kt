package com.benmohammad.mvitasks.view

import io.reactivex.Observable

interface EventObservable<E> {
    fun events(): Observable<E>
}