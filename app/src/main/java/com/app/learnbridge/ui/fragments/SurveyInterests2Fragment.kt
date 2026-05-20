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

class SurveyInterests2Fragment : Fragment() {

    private val selectedOptions = mutableSetOf<Int>()
    private val surveyViewModel: SurveyViewModel by activityViewModels {
        SurveyViewModel.SurveyViewModelFactory((requireActivity().application as LearnBridgeApplication).userRepository)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_survey_interests2, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val btnBack = view.findViewById<ImageButton>(R.id.btnBack)
        val btnClose = view.findViewById<ImageButton>(R.id.btnClose)
        val btnContinue = view.findViewById<MaterialButton>(R.id.btnContinue)
        val tvMaxSelection = view.findViewById<View>(R.id.tvMaxSelection)

        val options = listOf(
            view.findViewById<LinearLayout>(R.id.optionIT),
            view.findViewById<LinearLayout>(R.id.optionHealth),
            view.findViewById<LinearLayout>(R.id.optionArts),
            view.findViewById<LinearLayout>(R.id.optionTourism)
        )

        options.forEach { layout ->
            layout.setOnClickListener {
                if (selectedOptions.contains(layout.id)) {
                    selectedOptions.remove(layout.id)
                    layout.setBackgroundResource(R.drawable.bg_input_field)
                    tvMaxSelection.visibility = View.GONE
                } else if (selectedOptions.size < 3) {
                    selectedOptions.add(layout.id)
                    layout.setBackgroundResource(R.drawable.bg_survey_item_selected)
                    if (selectedOptions.size == 3) {
                        tvMaxSelection.visibility = View.VISIBLE
                    }
                } else {
                    Toast.makeText(requireContext(), "You can select up to 3 options", Toast.LENGTH_SHORT).show()
                }
            }
        }

        btnBack.setOnClickListener { findNavController().popBackStack() }
        btnClose.setOnClickListener { findNavController().navigate(R.id.mainFragment) }

        btnContinue.setOnClickListener {
            if (selectedOptions.isNotEmpty()) {
                val optionViews = mapOf(
                    R.id.optionIT to "IT & Software",
                    R.id.optionHealth to "Healthcare",
                    R.id.optionArts to "Arts & Design",
                    R.id.optionTourism to "Tourism & Hospitality"
                )
                val currentInterests = surveyViewModel.interestArea
                val newInterests = selectedOptions.map { optionViews[it] }.joinToString(", ")
                surveyViewModel.interestArea = if (currentInterests.isNullOrEmpty()) newInterests else "$currentInterests, $newInterests"
                findNavController().navigate(R.id.action_surveyInterests2Fragment_to_surveyCareerStageFragment)
            } else {
                Toast.makeText(requireContext(), "Please select at least one option", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
