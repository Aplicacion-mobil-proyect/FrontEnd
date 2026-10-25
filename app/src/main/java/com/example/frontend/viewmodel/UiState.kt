package com.example.frontend.viewmodel

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

sealed interface UiState<out T> {
    object Loading : UiState<Nothing>
    data class Success<T>(val data: T) : UiState<T>
    data class Error(val mensaje: String) : UiState<Nothing>
}

/** Flow de datos -> StateFlow<UiState>: Loading al inicio, Success con cada cambio, Error si el Flow falla. */
fun <T> Flow<T>.toUiState(scope: CoroutineScope): StateFlow<UiState<T>> =
    map<T, UiState<T>> { UiState.Success(it) }
        .catch { emit(UiState.Error(it.message ?: "Ocurrió un error inesperado")) }
        .stateIn(scope, SharingStarted.WhileSubscribed(5_000), UiState.Loading)

/** Flow que falla de inmediato con un mensaje (se muestra como UiState.Error). */
fun <T> flujoConError(mensaje: String): Flow<T> = flow { throw IllegalStateException(mensaje) }
