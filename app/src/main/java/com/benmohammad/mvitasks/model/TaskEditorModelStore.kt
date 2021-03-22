package com.benmohammad.mvitasks.model

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TaskEditorModelStore @Inject constructor() :
        ModelStore<TaskEditorState>(TaskEditorState.Closed)