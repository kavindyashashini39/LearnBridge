package com.app.learnbridge.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.app.learnbridge.R
import com.google.android.material.button.MaterialButton

import androidx.fragment.app.activityViewModels
import com.app.learnbridge.LearnBridgeApplication
import com.app.learnbridge.viewmodel.SurveyViewModel

class SurveyInterests1Fragment : Fragment() {

    private val selectedOptions = mutableSetOf<Int>()
    private val surveyViewModel: SurveyViewModel by activityViewModels {
        SurveyViewModel.SurveyViewModelFactory((requireActivity().application as LearnBridgeApplication).userRepository)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_survey_interests1, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val btnBack = view.findViewById<ImageButton>(R.id.btnBack)
        val btnClose = view.findViewById<ImageButton>(R.id.btnClose)
        val btnContinue = view.findViewById<MaterialButton>(R.id.btnContinue)

        val options = listOf(
            view.findViewById<LinearLayout>(R.id.optionHelping),
            view.findViewById<LinearLayout>(R.id.optionImproving),
            view.findViewById<LinearLayout>(R.id.optionSafety),
            view.findViewById<LinearLayout>(R.id.optionManaging)
        )

        options.forEach { layout ->
            layout.setOnClickListener {
                if (selectedOptions.contains(layout.id)) {
                    selectedOptions.remove(layout.id)
                    layout.setBackgroundResource(R.drawable.bg_input_field)
                } else {
                    selectedOptions.add(layout.id)
                    layout.setBackgroundResource(R.drawable.bg_survey_item_selected)
                }
            }
        }

        btnBack.setOnClickListener { findNavController().popBackStack() }
        btnClose.setOnClickListener { findNavController().navigate(R.id.mainFragment) }

        btnContinue.setOnClickListener {
            if (selectedOptions.isNotEmpty()) {
                val optionViews = mapOf(
                    R.id.optionHelping to "Helping others",
                    R.id.optionImproving to "Improving processes",
                    R.id.optionSafety to "Ensuring safety",
                    R.id.optionManaging to "Managing teams"
                )
                surveyViewModel.interestArea = selectedOptions.map { optionViews[it] }.joinToString(", ")
                findNavController().navigate(R.id.action_surveyInterests1Fragment_to_surveyInterests2Fragment)
            } else {
                Toast.makeText(requireContext(), "Please select at least one option", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
