package id.petersam.dhuwite.di

import android.content.Context
import android.content.SharedPreferences
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import id.petersam.dhuwite.util.PrefConstant
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun providePref(@ApplicationContext context: Context): SharedPreferences {
        return context.getSharedPreferences(PrefConstant.PREF_NAME, Context.MODE_PRIVATE)
    }

    @Singleton
    @Provides
    fun providePrefEditor(sharedPreferences: SharedPreferences): SharedPreferences.Editor {
        return sharedPreferences.edit()
    }
}