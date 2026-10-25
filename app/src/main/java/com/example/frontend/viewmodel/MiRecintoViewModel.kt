package com.example.frontend.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.frontend.database.AppDatabase
import com.example.frontend.model.Horario
import com.example.frontend.model.Recinto
import com.example.frontend.repository.PatrimonioRepository
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine

data class MiRecintoData(val recinto: Recinto, val horarios: List<Horario>)

class MiRecintoViewModel(application: Application) : AndroidViewModel(application) {

    private val repo = PatrimonioRepository(AppDatabase.getDatabase(application).patrimonioDao())
    private val recintoId: Int? = recintoIdDelEncargado(application)

    val uiState: StateFlow<UiState<MiRecintoData>> =
        (if (recintoId == null) flujoConError<MiRecintoData>(MSG_SIN_RECINTO)
        else combine(repo.recinto(recintoId), repo.horarios(recintoId)) { recinto, horarios ->
            MiRecintoData(checkNotNull(recinto) { "El recinto asignado ya no existe" }, horarios)
        }).toUiState(viewModelScope)
}
