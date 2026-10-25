package com.example.frontend.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.frontend.database.AppDatabase
import com.example.frontend.model.Recinto
import com.example.frontend.repository.PatrimonioRepository
import kotlinx.coroutines.flow.StateFlow

class MapaViewModel(application: Application) : AndroidViewModel(application) {
    private val repo = PatrimonioRepository(AppDatabase.getDatabase(application).patrimonioDao())
    val uiState: StateFlow<UiState<List<Recinto>>> = repo.recintos().toUiState(viewModelScope)
}
