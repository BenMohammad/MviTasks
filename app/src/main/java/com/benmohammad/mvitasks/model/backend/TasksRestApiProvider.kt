package com.benmohammad.mvitasks.model.backend

import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.benmohammad.mvitasks.model.Task
import io.reactivex.Observable
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import toothpick.ProvidesSingletonInScope
import toothpick.config.Module
import java.util.*
import javax.inject.Inject
import javax.inject.Provider
import javax.inject.Singleton

typealias BaseUrl = String

object TasksRestApiModule: Module() {
    init {
        bind(BaseUrl::class.java).toInstance("https://casterdemoendpoints.firebaseio.com/")
        bind(TasksRestApi::class.java).toProvider(TasksRestApiProvider::class.java)
    }
}

interface TasksRestApi {
    @GET("tasks.json")
    fun getTasks(): Observable<Map<String, Task>>
}

@Singleton @ProvidesSingletonInScope
class TasksRestApiProvider @Inject constructor(baseUrl: BaseUrl): Provider<TasksRestApi> {
    override fun get(): TasksRestApi {
        return retrofit.create(TasksRestApi::class.java)
    }

    private val retrofit: Retrofit = Retrofit.Builder()
            .baseUrl(baseUrl)
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
}