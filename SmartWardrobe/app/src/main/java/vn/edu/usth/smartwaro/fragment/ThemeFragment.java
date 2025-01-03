package vn.edu.usth.smartwaro.fragment;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import vn.edu.usth.smartwaro.R;
import vn.edu.usth.smartwaro.SmartWardrobe;

public class ThemeFragment extends Fragment {

    private RadioGroup themeRadioGroup;

    private enum Theme {
        LIGHT("Light", R.id.theme_light),
        DARK("Dark", R.id.theme_dark),
        CLASSIC_LIGHT("Classic Light", R.id.theme_classicLight),
        CLASSIC_DARK("Classic Dark", R.id.theme_classicDark),
        PEARL_DARK("Pearl Dark", R.id.theme_pearlDark);

        private final String name;
        private final int radioId;

        Theme(String name, int radioId) {
            this.name = name;
            this.radioId = radioId;
        }

        public String getName() {
            return name;
        }

        public int getRadioId() {
            return radioId;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_theme, container, false);

        themeRadioGroup = view.findViewById(R.id.theme_radio_group);

        loadSelectedTheme();

        view.findViewById(R.id.apply_button).setOnClickListener(v -> applyTheme());

        return view;
    }

    private void applyTheme() {
        int selectedId = themeRadioGroup.getCheckedRadioButtonId();
        if (selectedId == -1) {
            Toast.makeText(getContext(), "Please select a theme", Toast.LENGTH_SHORT).show();
            return;
        }

        String selectedTheme = getThemeName(selectedId);

        saveSelectedTheme(selectedTheme);
        Toast.makeText(getContext(), "Theme changed to " + selectedTheme, Toast.LENGTH_SHORT).show();

        if (getActivity() instanceof SmartWardrobe) {
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
            String currentTitle = ((SmartWardrobe) getActivity()).getTitle().toString();
            sharedPreferences.edit().putString("toolbar_title", currentTitle).apply();

            getActivity().recreate();
        }
    }

    private String getThemeName(int selectedId) {
        for (Theme theme : Theme.values()) {
            if (theme.getRadioId() == selectedId) {
                return theme.getName();
            }
        }
        return Theme.LIGHT.getName();
    }

    private void saveSelectedTheme(String theme) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        sharedPreferences.edit().putString("theme_key", theme).apply();
    }

    private void loadSelectedTheme() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        String selectedTheme = sharedPreferences.getString("theme_key", Theme.LIGHT.getName());

        for (Theme theme : Theme.values()) {
            if (theme.getName().equals(selectedTheme)) {
                themeRadioGroup.check(theme.getRadioId());
                break;
            }
        }
    }
}
