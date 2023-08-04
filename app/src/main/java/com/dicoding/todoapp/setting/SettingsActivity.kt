package com.dicoding.todoapp.setting

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreference
import androidx.work.*
import com.dicoding.todoapp.R
import com.dicoding.todoapp.notification.NotificationWorker
import java.util.concurrent.TimeUnit

class SettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.settings_activity)
        if (savedInstanceState == null) {
            supportFragmentManager
                .beginTransaction()
                .replace(R.id.settings, SettingsFragment())
                .commit()
        }
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    class SettingsFragment : PreferenceFragmentCompat() {
        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey)

            val prefNotification = findPreference<SwitchPreference>(getString(R.string.pref_key_notify))
            prefNotification?.setOnPreferenceChangeListener { preference, newValue ->
                val channelName = getString(R.string.notify_channel_name)
                //TODO 13 : Schedule and cancel daily reminder using WorkManager with data channelName
                if (newValue is Boolean) {
                    if (newValue) {
                        // Menjadwalkan pengingat harian
                        scheduleDailyReminder(channelName)
                    } else {
                        // Membatalkan pengingat harian
                        cancelDailyReminder()
                    }
                }

                true
            }
        }

        private fun scheduleDailyReminder(channelName: String) {
            val constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.NOT_REQUIRED)
                .build()

            val periodicWorkRequest = PeriodicWorkRequest.Builder(
                NotificationWorker::class.java,
                1, TimeUnit.DAYS
            )

                .setInputData(Data.Builder().putString("channel_name", channelName).build())
                .setConstraints(constraints)
                .build()


            WorkManager.getInstance(requireContext())
                .enqueueUniquePeriodicWork(
                    "daily_reminder_work",
                    ExistingPeriodicWorkPolicy.REPLACE,
                    periodicWorkRequest
                )
        }


        private fun cancelDailyReminder() {
            WorkManager.getInstance(requireContext()).cancelAllWorkByTag("daily_reminder")
        }

        private fun updateTheme(mode: Int): Boolean {
            AppCompatDelegate.setDefaultNightMode(mode)
            requireActivity().recreate()
            return true
        }
    }
}