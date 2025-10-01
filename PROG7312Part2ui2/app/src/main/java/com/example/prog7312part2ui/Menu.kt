package com.example.prog7312part2ui

import android.animation.ObjectAnimator
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import com.example.prog7312part2ui.R

class MenuComponent @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    private var menuContainer: LinearLayout
    private var overlay: View
    private var isMenuOpen = false

    var onMenuItemClickListener: ((MenuItem) -> Unit)? = null

    enum class MenuItem {
        PROFILE,
        BOOKSHOP_MARKET,
        CALENDAR,
        CALCULATOR,
        CLASSROOMS,
        STUDY_HALL,
        STUDYHIVE_NEWS,
        SETTINGS,
        SIGN_OUT
    }

    init {
        LayoutInflater.from(context).inflate(R.layout.layout_menu, this, true)

        overlay = findViewById(R.id.menu_overlay)
        menuContainer = findViewById(R.id.menu_container)

        setupClickListeners()

        // Initially hide the menu
        visibility = View.GONE
    }

    private fun setupClickListeners() {
        // Close menu when overlay is clicked
        overlay.setOnClickListener {
            closeMenu()
        }


        findViewById<LinearLayout>(R.id.menu_item_profile).setOnClickListener {
            onMenuItemClickListener?.invoke(MenuItem.PROFILE)
            closeMenu()
        }

        findViewById<LinearLayout>(R.id.menu_item_bookshop).setOnClickListener {
            onMenuItemClickListener?.invoke(MenuItem.BOOKSHOP_MARKET)
            closeMenu()
        }

        findViewById<LinearLayout>(R.id.menu_item_calendar).setOnClickListener {
            onMenuItemClickListener?.invoke(MenuItem.CALENDAR)
            closeMenu()
        }

        findViewById<LinearLayout>(R.id.menu_item_calculator).setOnClickListener {
            onMenuItemClickListener?.invoke(MenuItem.CALCULATOR)
            closeMenu()
        }

        findViewById<LinearLayout>(R.id.menu_item_classrooms).setOnClickListener {
            onMenuItemClickListener?.invoke(MenuItem.CLASSROOMS)
            closeMenu()
        }

        findViewById<LinearLayout>(R.id.menu_item_study_hall).setOnClickListener {
            onMenuItemClickListener?.invoke(MenuItem.STUDY_HALL)
            closeMenu()
        }

        findViewById<LinearLayout>(R.id.menu_item_news).setOnClickListener {
            onMenuItemClickListener?.invoke(MenuItem.STUDYHIVE_NEWS)
            closeMenu()
        }

        findViewById<LinearLayout>(R.id.menu_item_settings).setOnClickListener {
            onMenuItemClickListener?.invoke(MenuItem.SETTINGS)
            closeMenu()
        }

        findViewById<LinearLayout>(R.id.menu_item_signout).setOnClickListener {
            onMenuItemClickListener?.invoke(MenuItem.SIGN_OUT)
            closeMenu()
        }
    }

    fun openMenu() {
        if (isMenuOpen) return

        isMenuOpen = true
        visibility = View.VISIBLE

        // Slide in animation
        menuContainer.translationX = -menuContainer.width.toFloat()

        ObjectAnimator.ofFloat(menuContainer, "translationX", 0f).apply {
            duration = 300
            start()
        }

        // Fade in overlay
        overlay.alpha = 0f
        ObjectAnimator.ofFloat(overlay, "alpha", 0.5f).apply {
            duration = 300
            start()
        }
    }

    fun closeMenu() {
        if (!isMenuOpen) return

        isMenuOpen = false

        // Slide out animation
        ObjectAnimator.ofFloat(menuContainer, "translationX", -menuContainer.width.toFloat()).apply {
            duration = 300
            start()
        }

        // Fade out overlay
        ObjectAnimator.ofFloat(overlay, "alpha", 0f).apply {
            duration = 300
            start()
        }

        // Hide after animation
        postDelayed({
            visibility = View.GONE
        }, 300)
    }

    fun toggleMenu() {
        if (isMenuOpen) {
            closeMenu()
        } else {
            openMenu()
        }
    }

    fun isOpen(): Boolean = isMenuOpen
}