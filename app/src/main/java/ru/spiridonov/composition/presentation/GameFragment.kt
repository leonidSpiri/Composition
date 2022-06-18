package ru.spiridonov.composition.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import ru.spiridonov.composition.R
import ru.spiridonov.composition.databinding.FragmentGameBinding
import ru.spiridonov.composition.domain.entity.GameResult
import ru.spiridonov.composition.domain.entity.GameSettings
import ru.spiridonov.composition.domain.entity.Level

class GameFragment : Fragment() {
    private var _binding: FragmentGameBinding? = null
    private val binding: FragmentGameBinding
        get() = _binding ?: throw RuntimeException("FragmentGameBinding == null")
    private lateinit var level: Level
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        parseArgs()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentGameBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.tvSum.setOnClickListener {
            launchGameFinishedFragment(
                GameResult(
                    true, 20, 20,
                    GameSettings(20, 10, 50, 30)
                )
            )
        }
    }

    private fun parseArgs() {
        level = requireArguments().getSerializable(KEY_LEVEL) as Level
    }

    private fun launchGameFinishedFragment(gameResult: GameResult) {
        requireActivity().supportFragmentManager.beginTransaction()
            .replace(R.id.main_container, GameFinishedFragment.newInstance(gameResult))
            .addToBackStack(null)
            .commit()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val KEY_LEVEL = "level"
        const val KEY_NAME = "GameFragment"
        fun newInstance(level: Level): GameFragment = GameFragment().apply {
            arguments = Bundle().apply {
                putSerializable(KEY_LEVEL, level)
            }
        }

    }
}