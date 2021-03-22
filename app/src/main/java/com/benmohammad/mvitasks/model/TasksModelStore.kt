package com.benmohammad.mvitasks.model

import com.benmohammad.mvitasks.model.SyncState.TasksState
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TasksModelStore @Inject constructor() :
        ModelStore<TasksState>(
                TasksState(
                        emptyList(),
                        FilterType.ANY,
                        SyncState.IDLE
                )
        )