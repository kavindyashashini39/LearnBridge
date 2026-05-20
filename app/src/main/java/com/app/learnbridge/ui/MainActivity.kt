package com.app.learnbridge.ui

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.app.learnbridge.R
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController
        val bottomNavigation = findViewById<BottomNavigationView>(R.id.bottomNavigation)
        var updatingBottomNav = false

        bottomNavigation.setOnItemSelectedListener { item ->
            if (!updatingBottomNav) {
                when (item.itemId) {
                    R.id.forYou -> navigateIfNeeded(navController, R.id.mainFragment)
                    R.id.explore -> navigateIfNeeded(navController, R.id.recommendationsFragment)
                    R.id.learn -> navigateIfNeeded(navController, R.id.certificatesFragment)
                    R.id.dashboard -> navigateIfNeeded(navController, R.id.dashboardFragment)
                    R.id.shop -> navigateIfNeeded(navController, R.id.earnFragment)
                }
            }
            true
        }

        navController.addOnDestinationChangedListener { _, destination, _ ->
            bottomNavigation.visibility = when (destination.id) {
                R.id.splashFragment,
                R.id.onboardingFragment,
                R.id.authFragment,
                R.id.googleSignInFragment,
                R.id.xpRewardFragment,
                R.id.recommendationIntroFragment,
                R.id.surveyGoalFragment,
                R.id.surveyExperienceFragment,
                R.id.surveyInterests1Fragment,
                R.id.surveyInterests2Fragment,
                R.id.surveyCareerStageFragment,
                R.id.surveyHobbiesFragment -> View.GONE
                else -> View.VISIBLE
            }
            updatingBottomNav = true
            bottomNavigation.selectedItemId = when (destination.id) {
                R.id.mainFragment -> R.id.forYou
                R.id.recommendationsFragment,
                R.id.recommendationIntroFragment -> R.id.explore
                R.id.certificatesFragment -> R.id.learn
                R.id.dashboardFragment -> R.id.dashboard
                R.id.earnFragment -> R.id.shop
                else -> bottomNavigation.selectedItemId
            }
            updatingBottomNav = false
        }
    }

    private fun navigateIfNeeded(navController: NavController, destinationId: Int) {
        if (navController.currentDestination?.id != destinationId) {
            navController.navigate(destinationId)
        }
    }
}
