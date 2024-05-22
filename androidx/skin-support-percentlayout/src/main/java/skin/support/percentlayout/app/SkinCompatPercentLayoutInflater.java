package skin.support.percentlayout.app;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.NonNull;
import skin.support.app.SkinLayoutInflater;
import skin.support.percentlayout.widget.SkinCompatPercentFrameLayout;
import skin.support.percentlayout.widget.SkinCompatPercentRelativeLayout;

/**
 * Created by ximsf on 2017/3/5.
 */

public class SkinCompatPercentLayoutInflater implements SkinLayoutInflater {
    @Override
    public View createView(@NonNull Context context, final String name, @NonNull AttributeSet attrs) {
        View view = null;
        switch (name) {
            case "androidx.percentlayout.widget.PercentRelativeLayout":
                view = new SkinCompatPercentRelativeLayout(context, attrs);
                break;
            case "androidx.percentlayout.widget.PercentFrameLayout":
                view = new SkinCompatPercentFrameLayout(context, attrs);
                break;
            default:
                break;
        }
        return view;
    }
}
