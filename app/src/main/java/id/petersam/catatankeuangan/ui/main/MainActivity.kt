package id.petersam.catatankeuangan.ui.main

import android.app.SearchManager
import android.content.Context
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.core.view.children
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import dagger.hilt.android.AndroidEntryPoint
import id.petersam.catatankeuangan.R
import id.petersam.catatankeuangan.databinding.ActivityMainBinding
import id.petersam.catatankeuangan.model.Transaction
import id.petersam.catatankeuangan.ui.chart.ChartFragment
import id.petersam.catatankeuangan.ui.home.HomeFragment
import id.petersam.catatankeuangan.util.LoadState
import id.petersam.catatankeuangan.util.alertDialog
import id.petersam.catatankeuangan.util.snackBar
import id.petersam.catatankeuangan.util.viewBinding


@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private val binding by viewBinding(ActivityMainBinding::inflate)
    private val vm by viewModels<MainViewModel>()

    private val homeFragment by lazy { HomeFragment() }
    private val chartFragment by lazy { ChartFragment() }
    private var visibleFragment: Fragment = homeFragment

    private var menu: Menu? = null

    private val syncSignInLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            vm.sync()
        }

    private val backupSignInLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            vm.backup()
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setupFragments()

        observeGoogleLogin()
        observeGoogleBackup()
        observeGoogleSync()
    }

    private fun setupFragments() {
        showFragment(homeFragment, Page.HOME.tag)
        binding.bottomNavigationView.setOnItemSelectedListener {
            when (it.itemId) {
                Page.HOME.id -> {
                    showFragment(homeFragment, Page.HOME.tag)
                    true
                }
                Page.CHART.id -> {
                    if (!chartFragment.isAdded) vm.onTypeChanged(Transaction.Type.EXPENSE)
                    showFragment(chartFragment, Page.CHART.tag)
                    true
                }
                else -> false
            }
        }
    }

    private fun showFragment(fragment: Fragment, tag: String) {
        supportFragmentManager.commit {
            if (!fragment.isAdded) {
                add(R.id.container, fragment, tag)
            }
            hide(visibleFragment)
            show(fragment)
        }
        visibleFragment = fragment
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        this.menu = menu
        menu?.findItem(R.id.menuSync)?.let { item ->
            item.isVisible = false
        }
        menu?.findItem(R.id.menuBackup)?.let { item ->
            item.isVisible = false
        }

        val searchManager = getSystemService(Context.SEARCH_SERVICE) as SearchManager
        (menu?.findItem(R.id.search)?.actionView as SearchView).apply {
            changeSearchViewTextColor(this)
            setSearchableInfo(searchManager.getSearchableInfo(componentName))
            setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean = false
                override fun onQueryTextChange(newText: String?): Boolean {
                    vm.onQuerySearchChanged(newText.toString())
                    return true
                }
            })
        }
        observeRemoteConfig()
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menuBackup -> {
                if (vm.canBackupContent()) {
                    if (vm.backup.value is LoadState.Loading) {
                        snackBar(binding.root, getString(R.string.google_backup_in_process))
                    } else {
                        showBackupConfirmationDialog()
                    }

                } else {
                    snackBar(binding.root, getString(R.string.google_backup_already_done))
                }
                return true
            }
            R.id.menuSync -> {
                if (vm.sync.value is LoadState.Loading) {
                    snackBar(binding.root, getString(R.string.google_sync_in_process))
                } else {
                    showSyncConfirmationDialog()
                }
                return true
            }
        }
        return false
    }

    private fun observeGoogleLogin() {
        vm.loginIntent.observe(this) {
            if (it.first == MainViewModel.GoogleAction.BACKUP)
                backupSignInLauncher.launch(it.second)
            else
                syncSignInLauncher.launch(it.second)
        }
    }

    private fun observeGoogleBackup() {
        vm.backup.observe(this) {
            when (it) {
                is LoadState.Loading -> {
                    snackBar(binding.root, getString(R.string.google_backup_in_process))
                }
                is LoadState.Success -> {
                    snackBar(
                        binding.root,
                        getString(R.string.google_backup_success),
                        color = R.color.green_text
                    )
                }
                is LoadState.Error -> {
                    snackBar(
                        binding.root,
                        getString(R.string.google_backup_failed),
                        color = R.color.red_text
                    )
                }
            }
        }
    }

    private fun observeGoogleSync() {
        vm.sync.observe(this) {
            when (it) {
                is LoadState.Loading -> {
                    snackBar(binding.root, getString(R.string.google_sync_in_process))
                }
                is LoadState.Success -> {
                    snackBar(
                        binding.root,
                        getString(R.string.google_sync_success),
                        color = R.color.green_text
                    )
                }
                is LoadState.Error -> {
                    snackBar(
                        binding.root,
                        getString(R.string.google_sync_failed),
                        color = R.color.red_text
                    )
                }
            }
        }
    }

    private fun observeRemoteConfig() {
        vm.isAllowGoogleDriveAction.observe(this) {
            menu?.findItem(R.id.menuSync)?.let { item ->
                item.isVisible = it
            }
            menu?.findItem(R.id.menuBackup)?.let { item ->
                item.isVisible = it
            }
        }
    }

    private fun showBackupConfirmationDialog() {
        alertDialog(
            title = getString(R.string.google_backup_dialog_title),
            message = getString(R.string.google_backup_dialog_message),
            positiveButtonText = getString(R.string.backup),
            positiveAction = { vm.backup() },
            negativeButtonText = getString(R.string.cancel),
        )
    }

    private fun showSyncConfirmationDialog() {
        alertDialog(
            title = getString(R.string.google_sync_dialog_title),
            message = getString(R.string.google_sync_dialog_message),
            positiveButtonText = getString(R.string.sync),
            positiveAction = { vm.sync() },
            negativeButtonText = getString(R.string.cancel),
        )
    }

    private fun changeSearchViewTextColor(view: View?) {
        val whiteColor = ContextCompat.getColor(this, R.color.white)
        when (view) {
            is TextView -> view.setTextColor(whiteColor)
            is ImageView -> view.setColorFilter(whiteColor)
            is ViewGroup -> {
                for (v in view.children) {
                    changeSearchViewTextColor(v)
                }
            }
        }
    }

    override fun onBackPressed() {
        if (visibleFragment != homeFragment) navigateTo(Page.HOME)
        else super.onBackPressed()
    }

    internal fun navigateTo(page: Page) {
        binding.bottomNavigationView.selectedItemId = page.id
    }

    enum class Page(val id: Int, val tag: String) {
        HOME(R.id.homeFragment, "home"),
        CHART(R.id.chartFragment, "chart"),
    }
}