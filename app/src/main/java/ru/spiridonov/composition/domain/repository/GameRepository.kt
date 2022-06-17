package ru.spiridonov.composition.domain.repository

import ru.spiridonov.composition.domain.entity.GameSettings
import ru.spiridonov.composition.domain.entity.Level
import ru.spiridonov.composition.domain.entity.Question

interface GameRepository {
    fun generateQuestion(maxSumValue: Int, countOfOptions: Int): Question
    fun getGameSettings(level: Level): GameSettings
}