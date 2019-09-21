package wolfheros.life.home.setting;

import android.content.Intent;
import android.content.SharedPreferences;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.RingtonePreference;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.MenuItem;

import wolfheros.life.home.R;
import wolfheros.life.home.WMovieActivity;

public class SettingFragment extends PreferenceFragment implements
        SharedPreferences.OnSharedPreferenceChangeListener{

    public static final String KEY_DELETE = "delete";
    public static final String KEY_NOTIFICATION = "notification";
    public static final String KEY_SHOW = "show";
    public static final String KEY_NOTIFICATION_SOUNDS = "notification_sounds";
    SharedPreferences.OnSharedPreferenceChangeListener sharedPreferenceChangeListener = this;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preference);
        setHasOptionsMenu(true);

        // add preference
        bindPreferenceSummaryToValue(findPreference(KEY_DELETE));
        bindPreferenceSummaryToValue(findPreference(KEY_NOTIFICATION));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home){
            startActivity(new Intent(getActivity(), WMovieActivity.class));
        }
        return super.onOptionsItemSelected(item);
    }


    private static void bindPreferenceSummaryToValue(Preference preference) {
        // Set the listener to watch for value changes.
        preference.setOnPreferenceChangeListener(sOnPreferenceChangeListener);

        // Trigger the listener immediately with the preference's
        // current value.
        sOnPreferenceChangeListener.onPreferenceChange(preference,
                PreferenceManager
                        .getDefaultSharedPreferences(preference.getContext())
                        .getString(preference.getKey(), ""));
    }

    // set listener to preference change.
    private static Preference.OnPreferenceChangeListener sOnPreferenceChangeListener
            = new Preference.OnPreferenceChangeListener() {
        @Override
        public boolean onPreferenceChange(Preference preference, Object newValue) {
            String values = newValue.toString();

            if (preference instanceof ListPreference){
                ListPreference listPreference = (ListPreference)preference;
                int index = listPreference.findIndexOfValue(values);
                // Get summary and set current index's summary
                preference.setSummary(index > -1 ? listPreference.getEntries()[index] : null);
            }else if (preference instanceof RingtonePreference){
                if (TextUtils.isEmpty(values)){
                    preference.setSummary(R.string.notification_slient);
                }else {
                    Ringtone ringtone = RingtoneManager
                            .getRingtone(preference.getContext(), Uri.parse(values));
                    if (ringtone == null){
                        preference.setSummary(null);
                    }else {
                        preference.setSummary(ringtone.getTitle(preference.getContext()));
                    }
                }

            }else {
                // if doesn't match anyone , just set summary with values.
                preference.setSummary(values);
            }

            return true;
        }
    };

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals(KEY_NOTIFICATION)){
            Preference preference = findPreference(key);
            preference.setSummary(sharedPreferences.getString(key,""));
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        getPreferenceScreen().getSharedPreferences()
                .registerOnSharedPreferenceChangeListener(sharedPreferenceChangeListener);
    }

    @Override
    public void onPause() {
        super.onPause();
        getPreferenceScreen().getSharedPreferences()
                .unregisterOnSharedPreferenceChangeListener(sharedPreferenceChangeListener);
    }
}