package bobo.shanche.fragmengt;

import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;

import bobo.shanche.R;
import bobo.shanche.utils.CommonUtil;

/**
 * Created by bobo1 on 2017/2/13.
 */

public class SettingsFragment extends PreferenceFragment {
    @Override public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preference);
    }

    @Override
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
        super.onPreferenceTreeClick(preferenceScreen, preference);
        switch (preference.getKey()){
            case "donate" :
                CommonUtil.openAlipayPayPage(getActivity());
            default:
        }
        return true;
    }


}
