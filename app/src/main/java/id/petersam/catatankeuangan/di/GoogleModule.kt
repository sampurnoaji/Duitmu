package id.petersam.catatankeuangan.di

import android.content.Context
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import id.petersam.catatankeuangan.data.BackupRepository
import id.petersam.catatankeuangan.data.google.GoogleDrive
import id.petersam.catatankeuangan.data.google.GoogleDriveService
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object GoogleModule {

    @Singleton
    @Provides
    fun provideGoogleSignInClient(@ApplicationContext context: Context): GoogleSignInClient {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .build()
        return GoogleSignIn.getClient(context, gso)
    }

    @Provides
    fun provideGoogleDriveService(
        @ApplicationContext context: Context,
        googleSignInClient: GoogleSignInClient,
        repository: BackupRepository
    ): GoogleDrive = GoogleDriveService(context, googleSignInClient, repository)
}