package skin.support.utils;

import android.content.Context;
import com.moorgen.sdk.common.SharedPreferencesHelper;
import skin.support.SkinCompatManager;

/**
 * Created by ximsfei on 2017/1/10.
 */

public class SkinPreference {
    private static final String FILE_NAME = "meta-data";

    public static final String  DEFAULT_SKIN_NAME = "default";
    private static final String KEY_SKIN_NAME = "skin-name";
    private static final String KEY_SKIN_STRATEGY = "skin-strategy";
    private static final String KEY_SKIN_USER_THEME = "skin-user-theme-json";
    private static SkinPreference sInstance;
    private final Context mApp;
    private final SharedPreferencesHelper mPref;

    public static void init(Context context) {
        if (sInstance == null) {
            synchronized (SkinPreference.class) {
                if (sInstance == null) {
                    sInstance = new SkinPreference(context.getApplicationContext());
                }
            }
        }
    }

    public static SkinPreference getInstance() {
        return sInstance;
    }

    private SkinPreference(Context applicationContext) {
        mApp = applicationContext;
        mPref = new SharedPreferencesHelper(applicationContext,FILE_NAME);
    }

    public SkinPreference setSkinName(String skinName) {
        mPref.put(KEY_SKIN_NAME, skinName);
        return this;
    }

    public String getSkinName() {
        return (String) mPref.get(KEY_SKIN_NAME, "");
    }

    public SkinPreference setSkinStrategy(int strategy) {
        mPref.put(KEY_SKIN_STRATEGY, strategy);
        return this;
    }

    public int getSkinStrategy() {
        return (int)mPref.get(KEY_SKIN_STRATEGY, SkinCompatManager.SKIN_LOADER_STRATEGY_NONE);
    }

    public SkinPreference setUserTheme(String themeJson) {
        mPref.put(KEY_SKIN_USER_THEME, themeJson);
        return this;
    }

    public String getUserTheme() {
        return (String) mPref.get(KEY_SKIN_USER_THEME, "");
    }
}
