package com.etu.duckietownandroid

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.webkit.URLUtil
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.EditTextPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat

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

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_settings, menu)
        return true
    }

    class SettingsFragment : PreferenceFragmentCompat() {

        private val urlListener = { _: Preference, newValue: Any ->
            if(URLUtil.isValidUrl(newValue.toString())){
                Toast.makeText(activity, "Valid url", Toast.LENGTH_SHORT).show()
                true
            }else{
                Toast.makeText(activity, "NOT valid URL", Toast.LENGTH_SHORT).show()
                false
            }
        }

        private val partialUrlListener = { _: Preference, newValue: Any ->
            if(checkUrl(newValue.toString())) {
                Toast.makeText(activity, "Valid url", Toast.LENGTH_SHORT).show()
                true
            }else{
                Toast.makeText(activity, "NOT valid URL", Toast.LENGTH_SHORT).show()
                false
            }
        }

        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey)
            findPreference<EditTextPreference>("update_time")?.setOnPreferenceChangeListener { preference, newValue ->
                if(checkNumber(newValue)){
                    true
                }else{
                    Toast.makeText(activity, "Update should be positive integer", Toast.LENGTH_SHORT).show()
                    false
                }
            }

            findPreference<EditTextPreference>("local_url")?.setOnPreferenceChangeListener(urlListener)
            findPreference<EditTextPreference>("external_url")?.setOnPreferenceChangeListener(urlListener)

            findPreference<EditTextPreference>("bot_url")?.setOnPreferenceChangeListener(partialUrlListener)
            findPreference<EditTextPreference>("watchtower_url")?.setOnPreferenceChangeListener(partialUrlListener)
            findPreference<EditTextPreference>("camera_url")?.setOnPreferenceChangeListener(partialUrlListener)
        }

        private fun checkNumber(newValue: Any): Boolean{
            return newValue.toString() != "" && newValue.toString().matches(Regex("\\d*")) && newValue.toString().toLong() > 0
        }

        private fun checkUrl(url: String): Boolean {
            return url.matches(Regex("([/][\\w&&[^_1-9]]+([.][\\w&&[^_1-9]]+)?){0,}[\\w&&[^_0-9]](([_][0])|([0]{2}))([/][\\w&&[^_1-9]]+([.][\\w&&[^_0-9]]+)?){1,}"))
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.settings_about_item -> {
                val alert = AlertDialog.Builder(this).setTitle(R.string.menu_about_title)
                    .setMessage(R.string.menu_about_text)
                    .setPositiveButton(R.string.menu_about_positive_button_text) { dialog, _ -> dialog.dismiss() }
                alert.show()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}