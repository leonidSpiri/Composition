package ru.spiridonov.composition.presentation

import android.app.Application
import android.os.CountDownTimer
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import ru.spiridonov.composition.R
import ru.spiridonov.composition.data.GameRepositoryImpl
import ru.spiridonov.composition.domain.entity.GameResult
import ru.spiridonov.composition.domain.entity.GameSettings
import ru.spiridonov.composition.domain.entity.Level
import ru.spiridonov.composition.domain.entity.Question
import ru.spiridonov.composition.domain.usecases.GenerateQuestionUseCase
import ru.spiridonov.composition.domain.usecases.GetGameSettingsUseCase

class GameViewModel(application: Application) : AndroidViewModel(application) {
    private val context = application
    private val repository = GameRepositoryImpl
    private val getQuestionUseCase = GenerateQuestionUseCase(repository)
    private val getGameSettingsUseCase = GetGameSettingsUseCase(repository)
    private lateinit var gameSettings: GameSettings
    private lateinit var level: Level
    private var timer: CountDownTimer? = null
    private var countOfRightAnswers = 0
    private var countOfQuestions = 0

    private val _formattedTime = MutableLiveData<String>()
    val formattedTime: LiveData<String>
        get() = _formattedTime

    private val _question = MutableLiveData<Question>()
    val question: LiveData<Question>
        get() = _question

    private val _percentOfRightQuestions = MutableLiveData<Int>()
    val percentOfRightQuestions: LiveData<Int>
        get() = _percentOfRightQuestions

    private val _minPercent = MutableLiveData<Int>()
    val minPercent: LiveData<Int>
        get() = _minPercent

    private val _gameResult = MutableLiveData<GameResult>()
    val gameResult: LiveData<GameResult>
        get() = _gameResult

    private val _progressAnswers = MutableLiveData<String>()
    val progressAnswers: LiveData<String>
        get() = _progressAnswers

    private val _enoughCount = MutableLiveData<Boolean>()
    val enoughCount: LiveData<Boolean>
        get() = _enoughCount

    private val _enoughPercent = MutableLiveData<Boolean>()
    val enoughPercent: LiveData<Boolean>
        get() = _enoughPercent


    fun startGame(level: Level) {
        getGameSettings(level)
        startTimer()
        generateQuestion()
        updateProgress()
    }

    fun chooseAnswer(number: Int) {
        checkAnswer(number)
        updateProgress()
        generateQuestion()
    }

    private fun getGameSettings(level: Level) {
        this.level = level
        this.gameSettings = getGameSettingsUseCase(level)
        _minPercent.value = gameSettings.minPercentOfRightAnswers
    }

    private fun startTimer() {
        timer = object : CountDownTimer(
            gameSettings.gameTimeInSeconds * MILLIS_IN_SECONDS, MILLIS_IN_SECONDS
        ) {
            override fun onTick(p0: Long) {
                _formattedTime.value = formatTime(p0)
            }

            override fun onFinish() {
                finishGame()
            }

        }
        timer?.start()
    }

    private fun formatTime(millis: Long): String {
        val second = millis / MILLIS_IN_SECONDS
        val minute = second / SECONDS_IN_MINUTES
        val leftSeconds = second - (minute * SECONDS_IN_MINUTES)
        return String.format("%02d:%02d", minute, leftSeconds)
    }

    private fun generateQuestion() {
        _question.value = getQuestionUseCase(gameSettings.maxSumValue)
    }

    private fun updateProgress() {
        val percent = calculatePercentOfRightAnswers()
        _percentOfRightQuestions.value = percent
        _progressAnswers.value = String.format(
            context.resources.getString(R.string.progress_answers),
            countOfRightAnswers,
            gameSettings.minCountOfRightAnswers
        )
        _enoughCount.value =
            countOfRightAnswers >= gameSettings.minCountOfRightAnswers
        _enoughPercent.value = percent >= gameSettings.minPercentOfRightAnswers

    }

    private fun calculatePercentOfRightAnswers(): Int =
        if (countOfQuestions == 0) 0 else
            (countOfRightAnswers / countOfQuestions.toDouble() * 100).toInt()

    private fun checkAnswer(number: Int) {
        val rightAnswer = question.value?.rightAnswer
        if (number == rightAnswer) {
            countOfRightAnswers++
        }
        countOfQuestions++
    }

    private fun finishGame() {
        _gameResult.value = GameResult(
            winner = enoughCount.value == true && enoughPercent.value == true,
            countOfRightAnswers = countOfRightAnswers,
            countOfQuestions = countOfQuestions,
            gameSettings = gameSettings
        )
    }

    override fun onCleared() {
        super.onCleared()
        timer?.cancel()
    }

    companion object {
        private const val MILLIS_IN_SECONDS = 1000L
        private const val SECONDS_IN_MINUTES = 60
    }
}