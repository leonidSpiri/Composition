package ru.spiridonov.composition.domain.usecases

import ru.spiridonov.composition.domain.entity.GameSettings
import ru.spiridonov.composition.domain.entity.Level
import ru.spiridonov.composition.domain.repository.GameRepository

class GetGameSettingsUseCase(
    private val repository: GameRepository
) {
    operator fun invoke(level: Level): GameSettings
    = repository.getGameSettings(level)

}