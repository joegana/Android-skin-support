package skin.support.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import androidx.appcompat.widget.FitWindowsLinearLayout;

/**
 * Created by ximsfei on 2017/1/13.
 */

@SuppressLint("RestrictedApi")
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
