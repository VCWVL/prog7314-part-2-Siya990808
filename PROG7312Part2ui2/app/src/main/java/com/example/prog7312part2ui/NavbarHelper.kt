package com.example.prog7312part2ui

import android.app.Activity
import android.content.Intent
import android.view.View
import androidx.appcompat.app.AppCompatActivity

object NavbarHelper {

    /**
     * Sets up navbar click listeners for an activity
     * @param activity The activity containing the navbar
     * @param currentPage The current page identifier to highlight appropriately
     */
    fun setupNavbar(activity: AppCompatActivity, currentPage: NavbarPage) {
        val navbar = activity.findViewById<View>(R.id.navigation_bar) ?: return

        // Notes icon (left)
        navbar.findViewById<View>(R.id.nav_tasks)?.setOnClickListener {
            if (currentPage != NavbarPage.NOTES) {
                navigateToNotes(activity)
            }
        }

        // Calendar icon (center) - Dashboard
        navbar.findViewById<View>(R.id.nav_calendar)?.setOnClickListener {
            if (currentPage != NavbarPage.DASHBOARD) {
                navigateToDashboard(activity)
            }
        }

        // Marketplace icon (right)
        navbar.findViewById<View>(R.id.nav_marketplace)?.setOnClickListener {
            if (currentPage != NavbarPage.MARKETPLACE) {
                navigateToMarketplace(activity)
            }
        }
    }

    private fun navigateToNotes(activity: Activity) {
        // TODO: Create NotesActivity when ready
        // val intent = Intent(activity, NotesActivity::class.java)
        // activity.startActivity(intent)
        android.widget.Toast.makeText(activity, "Notes coming soon!", android.widget.Toast.LENGTH_SHORT).show()
    }

    private fun navigateToDashboard(activity: Activity) {
        val intent = Intent(activity, DashboardActivity::class.java)
        activity.startActivity(intent)
        activity.finish()
    }

    private fun navigateToMarketplace(activity: Activity) {
        // TODO: Create MarketplaceActivity when ready
        // val intent = Intent(activity, MarketplaceActivity::class.java)
        // activity.startActivity(intent)
        android.widget.Toast.makeText(activity, "Marketplace coming soon!", android.widget.Toast.LENGTH_SHORT).show()
    }
}

enum class NavbarPage {
    NOTES,
    DASHBOARD,
    MARKETPLACE
}