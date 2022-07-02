package id.petersam.duitmu.ui.main

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import dagger.hilt.android.AndroidEntryPoint
import id.petersam.duitmu.R
import id.petersam.duitmu.databinding.ActivityMainBinding
import id.petersam.duitmu.util.LoadState
import id.petersam.duitmu.util.alertDialog
import id.petersam.duitmu.util.snackBar
import id.petersam.duitmu.util.viewBinding


@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private val binding by viewBinding(ActivityMainBinding::inflate)
    private val vm by viewModels<MainViewModel>()

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
        setupNavigation()

        observeGoogleLogin()
        observeGoogleBackup()
        observeGoogleSync()
    }

    private fun setupNavigation() {
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController
        binding.bottomNavigationView.setupWithNavController(navController)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
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

    internal fun navigateToChartPage() {
        binding.bottomNavigationView.selectedItemId = R.id.chartFragment
    }
}