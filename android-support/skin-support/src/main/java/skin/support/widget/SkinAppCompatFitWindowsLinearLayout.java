package skin.support.widget;

import android.content.Context;
import android.support.v7.widget.FitWindowsFrameLayout;
import android.support.v7.widget.FitWindowsLinearLayout;
import android.util.AttributeSet;

/**
 * Created by ximsfei on 2017/1/13.
 */

public class SkinAppCompatFitWindowsLinearLayout extends FitWindowsLinearLayout implements SkinCompatSupportable {
    private SkinCompatBackgroundHelper mBackgroundTintHelper;

    public SkinAppCompatFitWindowsLinearLayout(Context context) {
        this(context, null);
    }

    public SkinAppCompatFitWindowsLinearLayout(Context context, AttributeSet attrs) {
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
