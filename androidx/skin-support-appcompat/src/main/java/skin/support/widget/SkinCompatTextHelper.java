package skin.support.widget;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Build;
import androidx.annotation.DrawableRes;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.widget.EditText;
import android.widget.TextView;
import java.lang.reflect.Field;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import skin.support.R;
import skin.support.content.res.SkinCompatResources;
import skin.support.content.res.SkinCompatVectorResources;

/**
 * Created by ximsfei on 2017/1/10.
 */

public class SkinCompatTextHelper extends SkinCompatHelper {
    private static Logger logger = LoggerFactory.getLogger("Skin.SkinCompatTextHelper");
    private static final String TAG = SkinCompatTextHelper.class.getSimpleName();

    public static SkinCompatTextHelper create(TextView textView) {
        if (Build.VERSION.SDK_INT >= 17) {
            return new SkinCompatTextHelperV17(textView);
        }
        return new SkinCompatTextHelper(textView);
    }

    final TextView mView;

    private int mTextColorResId = INVALID_ID;
    private int mTextColorHintResId = INVALID_ID;
    protected int mDrawableBottomResId = INVALID_ID;
    protected int mDrawableLeftResId = INVALID_ID;
    protected int mDrawableRightResId = INVALID_ID;
    protected int mDrawableTopResId = INVALID_ID;
    protected int mCursorDrawableResId = INVALID_ID;

    protected int mTextSizeResId  = INVALID_ID;
    protected int mTextFontResId  = INVALID_ID;
    protected int mLetterSpaceResId = INVALID_ID;

    public SkinCompatTextHelper(TextView view) {
        mView = view;
    }

    public void loadFromAttributes(AttributeSet attrs, int defStyleAttr) {
        final Context context = mView.getContext();

        // First read the TextAppearance style id
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.SkinCompatTextHelper, defStyleAttr, 0);
        final int ap = a.getResourceId(R.styleable.SkinCompatTextHelper_android_textAppearance, INVALID_ID);

        if (a.hasValue(R.styleable.SkinCompatTextHelper_android_drawableLeft)) {
            mDrawableLeftResId = a.getResourceId(R.styleable.SkinCompatTextHelper_android_drawableLeft, INVALID_ID);
        }
        if (a.hasValue(R.styleable.SkinCompatTextHelper_android_drawableTop)) {
            mDrawableTopResId = a.getResourceId(R.styleable.SkinCompatTextHelper_android_drawableTop, INVALID_ID);
        }
        if (a.hasValue(R.styleable.SkinCompatTextHelper_android_drawableRight)) {
            mDrawableRightResId = a.getResourceId(R.styleable.SkinCompatTextHelper_android_drawableRight, INVALID_ID);
        }
        if (a.hasValue(R.styleable.SkinCompatTextHelper_android_drawableBottom)) {
            mDrawableBottomResId = a.getResourceId(R.styleable.SkinCompatTextHelper_android_drawableBottom, INVALID_ID);
        }
        if (a.hasValue(R.styleable.SkinCompatTextHelper_android_textCursorDrawable)) {
            mCursorDrawableResId = a.getResourceId(R.styleable.SkinCompatTextHelper_android_textCursorDrawable, INVALID_ID);
        }
        a.recycle();

        if (ap != INVALID_ID) {
            a = context.obtainStyledAttributes(ap, R.styleable.SkinTextAppearance);
            if (a.hasValue(R.styleable.SkinTextAppearance_android_textColor)) {
                mTextColorResId = a.getResourceId(R.styleable.SkinTextAppearance_android_textColor, INVALID_ID);
            }
            if (a.hasValue(R.styleable.SkinTextAppearance_android_textColorHint)) {
                mTextColorHintResId = a.getResourceId(
                        R.styleable.SkinTextAppearance_android_textColorHint, INVALID_ID);
            }
            a.recycle();
        }

        // Now read the style's values
        a = context.obtainStyledAttributes(attrs, R.styleable.SkinTextAppearance, defStyleAttr, 0);
        if (a.hasValue(R.styleable.SkinTextAppearance_android_textColor)) {
            mTextColorResId = a.getResourceId(R.styleable.SkinTextAppearance_android_textColor, INVALID_ID);
        }
        if (a.hasValue(R.styleable.SkinTextAppearance_android_textColorHint)) {
            mTextColorHintResId = a.getResourceId(
                    R.styleable.SkinTextAppearance_android_textColorHint, INVALID_ID);
        }
//        if(a.hasValue(R.styleable.SkinTextAppearance_android_font)){
//            mTextFontResId  = a.getResourceId(
//                    R.styleable.SkinTextAppearance_android_font, INVALID_ID);
//        }
//        if(mTextFontResId == INVALID_ID && a.hasValue(R.styleable.SkinTextAppearance_android_fontFamily)){
//            mTextFontResId  = a.getResourceId(
//                    R.styleable.SkinTextAppearance_android_fontFamily, INVALID_ID);
//        }
        if(a.hasValue(R.styleable.SkinTextAppearance_android_textSize)){
            mTextSizeResId  = a.getResourceId(
                    R.styleable.SkinTextAppearance_android_textSize, INVALID_ID);
        }
        if(a.hasValue(R.styleable.SkinTextAppearance_android_letterSpacing)){
            mLetterSpaceResId  = a.getResourceId(
                    R.styleable.SkinTextAppearance_android_letterSpacing, INVALID_ID);
        }
        a.recycle();
        applySkin();
    }

    public void onSetTextAppearance(Context context, int resId) {
        final TypedArray a = context.obtainStyledAttributes(resId, R.styleable.SkinTextAppearance);
        if (a.hasValue(R.styleable.SkinTextAppearance_android_textColor)) {
            mTextColorResId = a.getResourceId(R.styleable.SkinTextAppearance_android_textColor, INVALID_ID);
        }
        if (a.hasValue(R.styleable.SkinTextAppearance_android_textColorHint)) {
            mTextColorHintResId = a.getResourceId(R.styleable.SkinTextAppearance_android_textColorHint, INVALID_ID);
        }
        a.recycle();
        applyTextColorResource();
        applyTextColorHintResource();
    }

    private void applyTextColorHintResource() {
        mTextColorHintResId = checkResourceId(mTextColorHintResId);
        if (mTextColorHintResId != INVALID_ID) {
            // TODO: HTC_U-3u OS:8.0上调用framework的getColorStateList方法，有可能抛出异常，暂时没有找到更好的解决办法.
            // issue: https://github.com/ximsfei/Android-skin-support/issues/110
            try {
                ColorStateList color = SkinCompatResources.getColorStateList(mView.getContext(), mTextColorHintResId);
                mView.setHintTextColor(color);
            } catch (Exception e) {
            }
        }
    }

    private void applyTextColorResource() {
        mTextColorResId = checkResourceId(mTextColorResId);
        if (mTextColorResId != INVALID_ID) {
            // TODO: HTC_U-3u OS:8.0上调用framework的getColorStateList方法，有可能抛出异常，暂时没有找到更好的解决办法.
            // issue: https://github.com/ximsfei/Android-skin-support/issues/110
            try {
                ColorStateList color = SkinCompatResources.getColorStateList(mView.getContext(), mTextColorResId);
                mView.setTextColor(color);
            } catch (Exception e) {
                logger.warn("applyTextColorResource error resId = {},error:{} ",mTextColorResId,e.toString());
            }
        }
    }

    public void onSetCompoundDrawablesRelativeWithIntrinsicBounds(
            @DrawableRes int start, @DrawableRes int top, @DrawableRes int end, @DrawableRes int bottom) {
        mDrawableLeftResId = start;
        mDrawableTopResId = top;
        mDrawableRightResId = end;
        mDrawableBottomResId = bottom;
        applyCompoundDrawablesRelativeResource();
    }

    public void onSetCompoundDrawablesWithIntrinsicBounds(
            @DrawableRes int left, @DrawableRes int top, @DrawableRes int right, @DrawableRes int bottom) {
        mDrawableLeftResId = left;
        mDrawableTopResId = top;
        mDrawableRightResId = right;
        mDrawableBottomResId = bottom;
        applyCompoundDrawablesResource();
    }

    protected void applyCompoundDrawablesRelativeResource() {
        applyCompoundDrawablesResource();
    }

    protected void applyCompoundDrawablesResource() {
        Drawable drawableLeft = null, drawableTop = null, drawableRight = null, drawableBottom = null;
        mDrawableLeftResId = checkResourceId(mDrawableLeftResId);
        if (mDrawableLeftResId != INVALID_ID) {
            drawableLeft = SkinCompatVectorResources.getDrawableCompat(mView.getContext(), mDrawableLeftResId);
        }
        mDrawableTopResId = checkResourceId(mDrawableTopResId);
        if (mDrawableTopResId != INVALID_ID) {
            drawableTop = SkinCompatVectorResources.getDrawableCompat(mView.getContext(), mDrawableTopResId);
        }
        mDrawableRightResId = checkResourceId(mDrawableRightResId);
        if (mDrawableRightResId != INVALID_ID) {
            drawableRight = SkinCompatVectorResources.getDrawableCompat(mView.getContext(), mDrawableRightResId);
        }
        mDrawableBottomResId = checkResourceId(mDrawableBottomResId);
        if (mDrawableBottomResId != INVALID_ID) {
            drawableBottom = SkinCompatVectorResources.getDrawableCompat(mView.getContext(), mDrawableBottomResId);
        }
        if (mDrawableLeftResId != INVALID_ID
                || mDrawableTopResId != INVALID_ID
                || mDrawableRightResId != INVALID_ID
                || mDrawableBottomResId != INVALID_ID) {
            mView.setCompoundDrawablesWithIntrinsicBounds(drawableLeft, drawableTop, drawableRight, drawableBottom);
        }
    }

    public int getTextColorResId() {
        return mTextColorResId;
    }

    protected void applyTextCursorDrawableResource()
    {
        if(mCursorDrawableResId != INVALID_ID && mView instanceof EditText)
        {
            Class textViewCls =  mView.getClass();
            while (textViewCls != TextView.class){
                textViewCls =  textViewCls.getSuperclass();
            }
            if(textViewCls == TextView.class)
            {
                try {
                    Field fld = textViewCls.getDeclaredField("mEditor");
                    if(fld != null)
                    {
                        fld.setAccessible(true);
                        Object editorObj = fld.get(mView);
                        if(editorObj != null)
                        {
                            Class editClass = editorObj.getClass();
                            Field mCursorField = null;
                            do {
                                try {
                                    mCursorField = editClass.getDeclaredField("mCursorDrawable");
                                } catch (NoSuchFieldException e)
                                {
                                    editClass = editClass.getSuperclass();
                                }
                            }while (mCursorField == null && editClass != Object.class);

                            if(mCursorField == null){
                                editClass = editorObj.getClass();
                                do {
                                    try {
                                        mCursorField = editClass.getDeclaredField("mDrawableForCursor");
                                    } catch (NoSuchFieldException e)
                                    {
                                        editClass = editClass.getSuperclass();
                                    }
                                }while (mCursorField == null && editClass != Object.class);
                            }

                            if(mCursorField != null)
                            {
                                mCursorField.setAccessible(true);
                                Object cursorObj = mCursorField.get(editorObj);
                                Drawable textCursorDrawable = SkinCompatResources.getDrawable(mView.getContext(), mCursorDrawableResId);
                                if(cursorObj != null )
                                {
                                    if( cursorObj instanceof Drawable[]) {
                                        Drawable[] drawables = (Drawable[]) cursorObj;
                                        int lenght = drawables.length;
                                        for (int i = 0; i < lenght; i++) {
                                            drawables[i] = textCursorDrawable;
                                        }
                                    }else if(cursorObj instanceof  Drawable){
                                        mCursorField.set(editorObj,textCursorDrawable);
                                    }
                                }else{
                                    mCursorField.set(editorObj,textCursorDrawable);
                                }
                            }
                        }
                    }
                }catch (Exception e){
                    Log.w(TAG,e+"");
                }
            }
        }
    }

    protected void applyTextSizeFontResource(){
//        mTextFontResId = checkResourceId(mTextFontResId);
//        if (mTextFontResId != INVALID_ID) {
//            Typeface font = SkinCompatResources.getFont(mView.getContext(), mTextFontResId);
//            if(font != null){
//                mView.setTypeface(font);
//            }
//        }

        mTextSizeResId = checkResourceId(mTextSizeResId);
        if (mTextSizeResId != INVALID_ID) {
            float size = SkinCompatResources.getDimension(mView.getContext(), mTextSizeResId);
            if(size != 0f){
                mView.setTextSize(TypedValue.COMPLEX_UNIT_PX,size);
            }
        }

        mLetterSpaceResId = checkResourceId(mLetterSpaceResId);
        if(mLetterSpaceResId != INVALID_ID){
            TypedValue fl = new TypedValue();
            SkinCompatResources.getValue(mView.getContext(),mLetterSpaceResId, fl,true);
            float sp = fl.getFloat();
            mView.setLetterSpacing(sp);
        }
    }

    @Override
    public void applySkin() {
        applyCompoundDrawablesRelativeResource();
        applyTextColorResource();
        applyTextColorHintResource();
        applyTextCursorDrawableResource();
        applyTextSizeFontResource();
    }
}
