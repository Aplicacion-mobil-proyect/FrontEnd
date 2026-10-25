package com.example.frontend.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.frontend.database.AppDatabase
import com.example.frontend.model.EventoConRecinto
import com.example.frontend.repository.PatrimonioRepository
import com.example.frontend.util.FechasUtil
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine

class EventosViewModel(application: Application) : AndroidViewModel(application) {

    private val repo = PatrimonioRepository(AppDatabase.getDatabase(application).patrimonioDao())

    private val _tipo = MutableStateFlow<String?>(null)
    val tipo: StateFlow<String?> = _tipo.asStateFlow()

    // Solo eventos desde hoy en adelante, filtrados por tipo si hay uno seleccionado
    val uiState: StateFlow<UiState<List<EventoConRecinto>>> =
        combine(repo.eventosDesde(FechasUtil.hoyIso()), _tipo) { eventos, tipo ->
            if (tipo == null) eventos else eventos.filter { it.tipo == tipo }
        }.toUiState(viewModelScope)

    fun seleccionarTipo(tipo: String?) { _tipo.value = tipo }
}
