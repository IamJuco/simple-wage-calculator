package com.juco.data.repository

import com.juco.data.datasource.WorkPlaceDataSource
import com.juco.data.mapper.toDomain
import com.juco.data.mapper.toEntity
import com.juco.domain.model.PayDay
import com.juco.domain.model.WorkPlace
import com.juco.domain.repository.WorkPlaceRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDate
import javax.inject.Inject

class WorkPlaceRepositoryImpl @Inject constructor(
    private val workPlaceDataSource: WorkPlaceDataSource
) : WorkPlaceRepository {

    override suspend fun saveWorkPlace(
        name: String,
        wage: Long,
        workDays: List<LocalDate>,
        payDay: PayDay
    ): Long {
        return workPlaceDataSource.saveWorkPlace(name, wage, workDays, payDay)
    }

    override suspend fun getWorkPlaceById(id: Int): WorkPlace? {
        return workPlaceDataSource.getWorkPlaceById(id)?.toDomain()
    }

    override suspend fun deleteWorkPlace(workPlace: WorkPlace) {
        workPlaceDataSource.deleteWorkPlace(workPlace.toEntity())
    }

    override fun observeWorkPlaces(): Flow<List<WorkPlace>> {
        return workPlaceDataSource.getAllWorkPlaces().map { list ->
            list.map { it.toDomain() }
        }
    }
}