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
import com.app.learnbridge.util.SessionManager
import com.app.learnbridge.viewmodel.SurveyViewModel

class SurveyHobbiesFragment : Fragment() {

    private val selectedOptions = mutableSetOf<Int>()
    private val surveyViewModel: SurveyViewModel by activityViewModels {
        SurveyViewModel.SurveyViewModelFactory((requireActivity().application as LearnBridgeApplication).userRepository)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_survey_hobbies, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val btnBack = view.findViewById<ImageButton>(R.id.btnBack)
        val btnClose = view.findViewById<ImageButton>(R.id.btnClose)
        val btnContinue = view.findViewById<MaterialButton>(R.id.btnContinue)
        val tvMaxSelection = view.findViewById<View>(R.id.tvMaxSelection)

        val options = listOf(
            view.findViewById<LinearLayout>(R.id.optionReading),
            view.findViewById<LinearLayout>(R.id.optionArt),
            view.findViewById<LinearLayout>(R.id.optionPhotography),
            view.findViewById<LinearLayout>(R.id.optionSinging)
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
                    R.id.optionReading to "Reading",
                    R.id.optionArt to "Art & Craft",
                    R.id.optionPhotography to "Photography",
                    R.id.optionSinging to "Singing"
                )
                surveyViewModel.hobbies = selectedOptions.map { optionViews[it] }.joinToString(", ")
                
                val userId = SessionManager(requireContext()).getUserId()
                surveyViewModel.saveSurveyResults(userId) {
                    findNavController().navigate(R.id.action_surveyHobbiesFragment_to_mainFragment)
                }
            } else {
                Toast.makeText(requireContext(), "Please select at least one option", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
