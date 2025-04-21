package skin.support.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import androidx.annotation.DrawableRes;
import androidx.appcompat.widget.SwitchCompat;
import androidx.appcompat.R;
import skin.support.content.res.SkinCompatResources;

public class SkinSwitchCompat extends SwitchCompat implements SkinCompatSupportable {
    private int thumbDrawbaleResId = 0;
    private int trackDrawableResId = 0;
    private int thumbTintColorStateListResId = 0;
    private int trackTintColorStateListResId = 0;
    private SkinCompatTextHelper mTextHelper;
    private SkinCompatBackgroundHelper mBackgroundTintHelper;
    private SkinCompatCompoundButtonHelper mCompoundButtonHelper;

    public SkinSwitchCompat(Context context) {
        this(context, null);
    }

    public SkinSwitchCompat(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SkinSwitchCompat(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mBackgroundTintHelper = new SkinCompatBackgroundHelper(this);
        mBackgroundTintHelper.loadFromAttributes(attrs, defStyleAttr);
        mTextHelper = SkinCompatTextHelper.create(this);
        mTextHelper.loadFromAttributes(attrs, defStyleAttr);
        mCompoundButtonHelper = new SkinCompatCompoundButtonHelper(this);
        mCompoundButtonHelper.loadFromAttributes(attrs, defStyleAttr);
        final TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.SwitchCompat, defStyleAttr, 0);
        if (a.hasValue(R.styleable.SwitchCompat_android_thumb)) {
            thumbDrawbaleResId = a.getResourceId(R.styleable.SwitchCompat_android_thumb, thumbDrawbaleResId);
        }
        if (a.hasValue(R.styleable.SwitchCompat_track)) {
            trackDrawableResId = a.getResourceId(R.styleable.SwitchCompat_track, trackDrawableResId);
        }
        if (a.hasValue(R.styleable.SwitchCompat_thumbTint)) {
            thumbTintColorStateListResId = a.getResourceId(R.styleable.SwitchCompat_thumbTint, thumbTintColorStateListResId);
        }
        if (a.hasValue(R.styleable.SwitchCompat_trackTint)) {
            trackTintColorStateListResId = a.getResourceId(R.styleable.SwitchCompat_trackTint, trackTintColorStateListResId);
        }
        a.recycle();
        applyThumb();
        applyThumbTint();
        applyTrack();
        applyTrackTint();
    }

    @Override
    public void setBackgroundResource(@DrawableRes int resId) {
        super.setBackgroundResource(resId);
        if (mBackgroundTintHelper != null) {
            mBackgroundTintHelper.onSetBackgroundResource(resId);
        }
    }

    @Override
    public void setTextAppearance(int resId) {
        setTextAppearance(getContext(), resId);
    }

    @Override
    public void setTextAppearance(Context context, int resId) {
        super.setTextAppearance(context, resId);
        if (mTextHelper != null) {
            mTextHelper.onSetTextAppearance(context, resId);
        }
    }

    @Override
    public void setCompoundDrawablesRelativeWithIntrinsicBounds(
            @DrawableRes int start, @DrawableRes int top, @DrawableRes int end, @DrawableRes int bottom) {
        super.setCompoundDrawablesRelativeWithIntrinsicBounds(start, top, end, bottom);
        if (mTextHelper != null) {
            mTextHelper.onSetCompoundDrawablesRelativeWithIntrinsicBounds(start, top, end, bottom);
        }
    }

    @Override
    public void setCompoundDrawablesWithIntrinsicBounds(
            @DrawableRes int left, @DrawableRes int top, @DrawableRes int right, @DrawableRes int bottom) {
        super.setCompoundDrawablesWithIntrinsicBounds(left, top, right, bottom);
        if (mTextHelper != null) {
            mTextHelper.onSetCompoundDrawablesWithIntrinsicBounds(left, top, right, bottom);
        }
    }

    @Override
    public void setButtonDrawable(@DrawableRes int resId) {
        super.setButtonDrawable(resId);
        if (mCompoundButtonHelper != null) {
            mCompoundButtonHelper.setButtonDrawable(resId);
        }
    }

    @Override
    public void setThumbResource(int resId) {
        super.setThumbResource(resId);
        thumbDrawbaleResId = resId;
        applyThumb();
    }

    @Override
    public void setTrackResource(int resId) {
        super.setTrackResource(resId);
        trackDrawableResId = resId;
        applyTrack();
    }

    private void applyThumb()
    {
        thumbDrawbaleResId = SkinCompatHelper.checkResourceId(thumbDrawbaleResId);
        if(thumbDrawbaleResId != SkinCompatHelper.INVALID_ID)
        {
            setThumbDrawable(SkinCompatResources.getDrawable(getContext(),thumbDrawbaleResId));
        }
    }

    private void applyTrack()
    {
        trackDrawableResId = SkinCompatHelper.checkResourceId(trackDrawableResId);
        if(trackDrawableResId != SkinCompatHelper.INVALID_ID)
        {
            setTrackDrawable(SkinCompatResources.getDrawable(getContext(),trackDrawableResId));
        }
    }


    private void applyThumbTint()
    {
        thumbTintColorStateListResId = SkinCompatHelper.checkResourceId(thumbTintColorStateListResId);
        if(thumbTintColorStateListResId != SkinCompatHelper.INVALID_ID)
        {
            setThumbTintList(SkinCompatResources.getColorStateList(getContext(),thumbTintColorStateListResId));
        }
    }

    private void applyTrackTint()
    {
        trackTintColorStateListResId = SkinCompatHelper.checkResourceId(trackTintColorStateListResId);
        if(trackTintColorStateListResId != SkinCompatHelper.INVALID_ID)
        {
            setTrackTintList(SkinCompatResources.getColorStateList(getContext(),trackTintColorStateListResId));
        }
    }

    @Override
    public void applySkin() {
        if (mBackgroundTintHelper != null) {
            mBackgroundTintHelper.applySkin();
        }
        if (mCompoundButtonHelper != null) {
            mCompoundButtonHelper.applySkin();
        }
        if (mTextHelper != null) {
            mTextHelper.applySkin();
        }
        applyThumb();
        applyThumbTint();
        applyTrack();
        applyTrackTint();
    }
}

