package com.example.myapplication.utils.coroutines

import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

interface ICoroutineSupport {
    private val coroutineScope: CoroutineScope
        get() = buildScope()

    private val coroutineContext
        get() = buildContext()

    fun launch(block: suspend (CoroutineScope) -> Unit): Job {
        return coroutineScope.launch(coroutineContext)
        {
            block(this)
        }
    }

    private fun buildContext(): CoroutineContext {
        return Dispatchers.IO + buildJob()
    }

    private fun buildJob(): Job {
        return SupervisorJob()
    }

    private fun buildScope(): CoroutineScope {
        return CoroutineScope(coroutineContext)
    }

}