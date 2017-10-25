package press.gfw.gfwhelper;


import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.SwitchPreference;
//import android.support.v7.app.ActionBar;

import java.sql.Timestamp;

/**
 * A {@link PreferenceActivity} that presents a set of application settings. On
 * handset devices, settings are presented as a single list. On tablets,
 * settings are split by category, with category headers shown to the left of
 * the list of settings.
 * <p>
 * See <a href="http://developer.android.com/design/patterns/settings.html">
 * Android Design: Settings</a> for design guidelines and the <a
 * href="http://developer.android.com/guide/topics/ui/settings.html">Settings
 * API Guide</a> for more information on developing a Settings UI.
 */
public class GFWHelperActivity extends AppCompatPreferenceActivity {


    GFWHelperApplication app;
    //GFWHelperActivity activity;
    //boolean inForeground = false;
    /**
     * 开关监听器
     */
    @SuppressWarnings("deprecation")
    private Preference.OnPreferenceChangeListener switchListener = new Preference.OnPreferenceChangeListener() {
        @Override
        public boolean onPreferenceChange(Preference preference, Object newValue) {

            if ((boolean) newValue) {
                app.serverHost = getValue(findPreference("text_server"));
                app.serverPort = getValue(findPreference("text_server_port"));
                app.password = getValue(findPreference("text_password"));
                app.proxyPort = getValue(findPreference("text_proxy_port"));
                log("开关打开");
                app.startService(true);
                //updateNotification();
            } else {
                log("开关关闭");
                app.stopService();
            }

            return true;
        }
    };

    /**
     * 获取配置信息
     *
     * @param preference Preference
     * @return 值
     */
    private static String getValue(Preference preference) {

        return PreferenceManager.getDefaultSharedPreferences(preference.getContext()).getString(preference.getKey(), "");

    }

    /**
     * 打印信息
     *
     * @param o 打印对象
     */
    @SuppressWarnings("unused")
    private static void log(Object o) {

        String time = (new Timestamp(System.currentTimeMillis())).toString().substring(0, 19);
        System.out.println("[" + time + "] " + o.toString());

    }

    /**
     * Helper method to determine if the device has an extra-large screen. For
     * example, 10" tablets are extra-large.
     */
    private static boolean isXLargeTablet(Context context) {
        return (context.getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_XLARGE;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public boolean onIsMultiPane() {
        return isXLargeTablet(this);
    }

    /**
     * This method stops fragment injection in malicious applications.
     * Make sure to deny any unknown fragments here.
     */
    protected boolean isValidFragment(String fragmentName) {
        return PreferenceFragment.class.getName().equals(fragmentName)
                || GeneralPreferenceFragment.class.getName().equals(fragmentName);
    }

    /**
     * This fragment shows general preferences only. It is used when the
     * activity is showing a two-pane settings UI.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static class GeneralPreferenceFragment extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_general);

            setHasOptionsMenu(false);

        }

    }
    /**
     * 配置监听器
     */
    private Preference.OnPreferenceChangeListener valueListener = new Preference.OnPreferenceChangeListener() {

        @Override
        public boolean onPreferenceChange(Preference preference, Object value) {

            String key = preference.getKey();

            String stringValue = value.toString();

            switch (key) {

                case "text_password":

                    String stars = "***********************************";

                    preference.setSummary(stars.substring(0, (stringValue.length() > stars.length() ? stars.length() : stringValue.length())));

                    break;

                default:

                    preference.setSummary(stringValue);

                    break;

            }

            return true;

        }

    };
    /**
     * 设置配置监听器
     *
     * @param preference Preference
     */
    private void setValueListener(Preference preference) {

        preference.setOnPreferenceChangeListener(valueListener);

        valueListener.onPreferenceChange(preference, getValue(preference));

    }

    /**
     * 设置开关监听器
     *
     * @param preference Preference
     */
    private void setSwitchListener(Preference preference) {

        preference.setOnPreferenceChangeListener(switchListener);

        switchListener.onPreferenceChange(preference, ((SwitchPreference) preference).isChecked());

    }

    private void updateNotification() {
        if (app.service != null) {
            app.service.updateNotification();
        }
    }
    /**
     * 设置开关和配置监听器
     */
    @SuppressWarnings("deprecation")
    private void setListener() {

        setValueListener(findPreference("text_server"));
        setValueListener(findPreference("text_server_port"));
        setValueListener(findPreference("text_password"));
        setValueListener(findPreference("text_proxy_port"));

        setSwitchListener(findPreference("switch_gfw"));

    }
    @SuppressWarnings("deprecation")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        app = (GFWHelperApplication) getApplication();
        addPreferencesFromResource(R.xml.pref_general);
        setListener();
    }



}
