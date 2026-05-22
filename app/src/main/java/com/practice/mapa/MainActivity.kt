package com.practice.mapa

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavController
import androidx.navigation.NavOptions
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.navigation.NavigationView
import com.practice.mapa.data.SessionManager
import com.practice.mapa.data.catalog.CartRepository
import com.practice.mapa.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var navController: NavController

    @Inject
    lateinit var sessionManager: SessionManager

    @Inject
    lateinit var cartRepository: CartRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val toolbar: Toolbar = binding.mainToolbar
        setSupportActionBar(toolbar)

        // FragmentContainerView defers its fragment transaction, so Activity.findNavController()
        // fails in onCreate(). Go through the NavHostFragment directly instead.
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.main_nav_host) as NavHostFragment
        navController = navHostFragment.navController

        val drawerLayout: DrawerLayout = binding.mainDrawerLayout
        val bottomNav: BottomNavigationView = binding.mainBottomNav
        val sideNav: NavigationView = binding.mainSideNav

        val topLevelDestinations = setOf(
            R.id.homeFragment,
            R.id.catalogFragment,
            R.id.cartFragment,
            R.id.formsFragment,
            R.id.profileFragment
        )
        appBarConfiguration = AppBarConfiguration(topLevelDestinations, drawerLayout)

        setupActionBarWithNavController(navController, appBarConfiguration)
        bottomNav.setupWithNavController(navController)

        // Cart badge
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                cartRepository.cartItemCount.collect { count ->
                    val badge = bottomNav.getOrCreateBadge(R.id.cartFragment)
                    badge.isVisible = count > 0
                    badge.number = count
                }
            }
        }

        // Override the NavigationView listener so we can intercept the Logout item.
        // For all other items, delegate to NavigationUI so the existing nav wiring is preserved.
        sideNav.setNavigationItemSelectedListener { item ->
            if (item.itemId == R.id.drawer_logout) {
                drawerLayout.closeDrawers()
                showLogoutConfirmDialog()
                true
            } else {
                val handled = NavigationUI.onNavDestinationSelected(item, navController)
                if (handled) drawerLayout.closeDrawers()
                handled
            }
        }

        // Hide the toolbar and bottom nav on auth screens; lock the drawer so it
        // can't be swiped open while on Login or Register.
        val authScreenIds = setOf(
            R.id.loginFragment,
            R.id.registerFragment,
            R.id.forgotPasswordDialog
        )
        navController.addOnDestinationChangedListener { _, destination, _ ->
            val isAuthScreen = destination.id in authScreenIds
            binding.mainToolbar.visibility = if (isAuthScreen) View.GONE else View.VISIBLE
            bottomNav.visibility           = if (isAuthScreen) View.GONE else View.VISIBLE
            drawerLayout.setDrawerLockMode(
                if (isAuthScreen) DrawerLayout.LOCK_MODE_LOCKED_CLOSED
                else              DrawerLayout.LOCK_MODE_UNLOCKED
            )
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    private fun showLogoutConfirmDialog() {
        MaterialAlertDialogBuilder(this)
            .setTitle(R.string.logout_confirm_title)
            .setMessage(R.string.logout_confirm_message)
            .setPositiveButton(R.string.logout_confirm_yes) { _, _ -> performLogout() }
            .setNegativeButton(R.string.logout_confirm_no, null)
            .show()
    }

    private fun performLogout() {
        lifecycleScope.launch {
            sessionManager.clearSession()
            // Pop the entire back stack and land on Login
            navController.navigate(
                R.id.loginFragment,
                null,
                NavOptions.Builder()
                    .setPopUpTo(navController.graph.id, true)
                    .build()
            )
        }
    }
}
