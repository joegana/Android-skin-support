package skin.support.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import androidx.appcompat.widget.FitWindowsFrameLayout;

/**
 * Created by ximsfei on 2017/1/13.
 */

@SuppressLint("RestrictedApi")
public class SkinAppCompatFitWindowsFrameLayout extends FitWindowsFrameLayout implements SkinCompatSupportable {
    private SkinCompatBackgroundHelper mBackgroundTintHelper;

    public SkinAppCompatFitWindowsFrameLayout(Context context) {
        this(context, null);
    }

    public SkinAppCompatFitWindowsFrameLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        mBackgroundTintHelper = new SkinCompatBackgroundHelper(this);
        mBackgroundTintHelper.loadFromAttributes(attrs, 0);
    }

    @Override
    public void applySkin() {
        if (mBackgroundTintHelper != null) {
            mBackgroundTintHelper.applySkin();
        }
    }

}
