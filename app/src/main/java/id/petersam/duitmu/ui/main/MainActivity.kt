package id.petersam.duitmu.ui.main

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import dagger.hilt.android.AndroidEntryPoint
import id.petersam.duitmu.R
import id.petersam.duitmu.databinding.ActivityMainBinding
import id.petersam.duitmu.util.viewBinding


@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private val binding by viewBinding(ActivityMainBinding::inflate)
    private val vm by viewModels<MainViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController
        binding.bottomNavigationView.setupWithNavController(navController)
    }

    private fun selectMenu(menu: Menu) {
        binding.bottomNavigationView.selectedItemId = menu.id
    }

    internal fun navigateToChartPage() {
        selectMenu(Menu.CHART)
    }

    enum class Menu(val id: Int) {
        HOME(R.id.homeFragment),
        CHART(R.id.chartFragment)
    }
}