package com.app.learnbridge.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.app.learnbridge.R
import com.google.android.material.button.MaterialButton

import androidx.fragment.app.activityViewModels
import com.app.learnbridge.LearnBridgeApplication
import com.app.learnbridge.viewmodel.SurveyViewModel

class SurveyGoalFragment : Fragment() {

    private val surveyViewModel: SurveyViewModel by activityViewModels {
        SurveyViewModel.SurveyViewModelFactory((requireActivity().application as LearnBridgeApplication).userRepository)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_survey_goal, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val btnContinue = view.findViewById<MaterialButton>(R.id.btnContinue)
        val rgGoals = view.findViewById<RadioGroup>(R.id.rgGoals)
        val btnBack = view.findViewById<ImageButton>(R.id.btnBack)
        val btnClose = view.findViewById<ImageButton>(R.id.btnClose)

        btnBack.setOnClickListener { findNavController().popBackStack() }
        btnClose.setOnClickListener { findNavController().navigate(R.id.mainFragment) }

        btnContinue.setOnClickListener {
            val selectedId = rgGoals.checkedRadioButtonId
            if (selectedId != -1) {
                val radioButton = view.findViewById<RadioButton>(selectedId)
                surveyViewModel.goal = radioButton.text.toString()
                findNavController().navigate(R.id.action_surveyGoalFragment_to_surveyExperienceFragment)
            } else {
                Toast.makeText(requireContext(), "Please select a goal", Toast.LENGTH_SHORT).show()
            }
        }
        
        // Handle background selection style for RadioButtons if needed
        rgGoals.setOnCheckedChangeListener { group, checkedId ->
            for (i in 0 until group.childCount) {
                val rb = group.getChildAt(i) as RadioButton
                if (rb.id == checkedId) {
                    rb.setBackgroundResource(R.drawable.bg_survey_item_selected)
                } else {
                    rb.setBackgroundResource(R.drawable.bg_input_field)
                }
            }
        }
    }
}
