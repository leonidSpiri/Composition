package ru.spiridonov.composition.presentation

import android.app.Application
import android.graphics.drawable.Drawable
import androidx.core.content.ContextCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import ru.spiridonov.composition.R
import ru.spiridonov.composition.domain.entity.GameResult
import ru.spiridonov.composition.domain.entity.GameSettings

class GameFinishedViewModel(application: Application) : AndroidViewModel(application) {
    private var context = application
    private lateinit var gameSettings: GameSettings
    private lateinit var gameResult: GameResult
    private var percent = 0

    private val _minPercent = MutableLiveData<String>()
    val minPercent: LiveData<String>
        get() = _minPercent

    private val _minCount = MutableLiveData<String>()
    val minCount: LiveData<String>
        get() = _minCount

    private val _percentOfRightQuestions = MutableLiveData<String>()
    val percentOfRightQuestions: LiveData<String>
        get() = _percentOfRightQuestions

    private val _countOfRightQuestions = MutableLiveData<String>()
    val countOfRightQuestions: LiveData<String>
        get() = _countOfRightQuestions

    private val _icSmile = MutableLiveData<Drawable>()
    val icSmile: LiveData<Drawable>
        get() = _icSmile


    fun start(gameResult: GameResult) {
        getGameResult(gameResult)
        setIconSmile()
    }


    private fun getGameResult(gameResult: GameResult) {
        this.gameResult = gameResult
        this.gameSettings = gameResult.gameSettings
        val countOfQuestions = gameResult.countOfQuestions
        val countOfRightAnswers = gameResult.countOfRightAnswers
        percent =
            calculatePercentOfRightAnswers(countOfQuestions, countOfRightAnswers)

        _percentOfRightQuestions.value = String.format(
            context.resources.getString(R.string.score_percentage),
            percent
        )

        _countOfRightQuestions.value = String.format(
            context.resources.getString(R.string.score_answers),
            countOfRightAnswers
        )

        _minPercent.value = String.format(
            context.resources.getString(R.string.required_percentage),
            gameSettings.minPercentOfRightAnswers
        )
        _minCount.value = String.format(
            context.resources.getString(R.string.required_score),
            gameSettings.minCountOfRightAnswers
        )
    }

    private fun setIconSmile() {
        _icSmile.value = if (gameResult.winner)
            ContextCompat.getDrawable(context, R.drawable.ic_smile)
        else
            ContextCompat.getDrawable(context, R.drawable.ic_sad)
    }

    private fun calculatePercentOfRightAnswers(
        countOfQuestions: Int,
        countOfRightAnswers: Int
    ): Int =
        if (countOfQuestions == 0) 0 else
            (countOfRightAnswers / countOfQuestions.toDouble() * 100).toInt()
}