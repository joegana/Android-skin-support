package skin.support.content.res;

import static skin.support.utils.SkinPreference.DEFAULT_SKIN_NAME;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.content.res.XmlResourceParser;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.text.TextUtils;
import android.util.TypedValue;
import androidx.annotation.ColorRes;
import androidx.annotation.DimenRes;
import androidx.annotation.FontRes;
import androidx.annotation.XmlRes;
import androidx.core.content.res.ResourcesCompat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import  skin.support.SkinCompatManager.SkinLoaderStrategy;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import skin.support.SkinCompatManager;
import skin.support.annotation.AnyRes;
import skin.support.annotation.DrawableRes;
import skin.support.app.SkinActivityLifecycle;

public class SkinCompatResources {
    private static Logger logger = LoggerFactory.getLogger("Skin.SkinCompatResources");
    private static volatile SkinCompatResources sInstance;
    private Resources mResources;
    private HashMap<String,Resources> mResourcesMap = new HashMap<>();
    private HashMap<String,String> mSkinPackageName = new HashMap<>();
    private HashMap<String,SkinLoaderStrategy> mSkinStrategy = new HashMap<>();

    private String mSkinPkgName = "";
    private String mSkinName = "";
    private SkinLoaderStrategy mStrategy;
    private boolean isDefaultSkin = true;
    private List<SkinResources> mSkinResources = new ArrayList<>();

    private SkinCompatResources() {
    }

    public static SkinCompatResources getInstance() {
        if (sInstance == null) {
            synchronized (SkinCompatResources.class) {
                if (sInstance == null) {
                    sInstance = new SkinCompatResources();
                }
            }
        }
        return sInstance;
    }

    void addSkinResources(SkinResources resources) {
        mSkinResources.add(resources);
    }

    public void reset() {
        reset(SkinCompatManager.getInstance().getStrategies().get(SkinCompatManager.SKIN_LOADER_STRATEGY_NONE));
    }

    public void reset(SkinCompatManager.SkinLoaderStrategy strategy) {
        mResources = SkinCompatManager.getInstance().getContext().getResources();
        mSkinPkgName = "";
        mSkinName = "";
        mStrategy = strategy;
        mResourcesMap.put(DEFAULT_SKIN_NAME,mResources);
        mSkinPackageName.put(DEFAULT_SKIN_NAME, SkinCompatManager.getInstance().getContext().getPackageName());
        mSkinStrategy.put(DEFAULT_SKIN_NAME,strategy);
        isDefaultSkin = true;
        SkinCompatUserThemeManager.get().clearCaches();
        for (SkinResources skinResources : mSkinResources) {
            skinResources.clear();
        }
    }

    public void setupSkin(Resources resources, String pkgName, String skinName, SkinCompatManager.SkinLoaderStrategy strategy) {
        if (resources == null || TextUtils.isEmpty(pkgName) || TextUtils.isEmpty(skinName)) {
            reset(strategy);
            return;
        }
        mResources = resources;
        mSkinPkgName = pkgName;
        mSkinName = skinName;
        mStrategy = strategy;
        mResourcesMap.put(skinName,resources);
        mSkinPackageName.put(skinName,pkgName);
        mSkinStrategy.put(skinName,strategy);
        isDefaultSkin = false;
        SkinCompatUserThemeManager.get().clearCaches();
        for (SkinResources skinResources : mSkinResources) {
            skinResources.clear();
        }
    }

    public Resources getSkinResources() {
        return mResources;
    }

    public Resources getSkinResources(String skinName) {
        return mResourcesMap.get(skinName);
    }

    public String getSkinPkgName() {
        return mSkinPkgName;
    }

    public String getSkinPkgName(String skinName) {
        return mSkinPackageName.get(skinName);
    }

    public String getSkinName(){
        return  mSkinName;
    }

    private Resources getSkinableResource(String skinName){
        Resources ret = mResources;
        if(!TextUtils.isEmpty(skinName)){
            Resources r =  mResourcesMap.get(skinName);
            if(r != null){
                ret = r ;
            }
        }
        return ret;
    }

    private String getSkinablePkgName(String skinName){
        String ret = mSkinPkgName;
        if(!TextUtils.isEmpty(skinName)){
            String r = mSkinPackageName.get(skinName);
            if(!TextUtils.isEmpty(r)){
                ret = r ;
            }
        }
        return ret;
    }

    private SkinLoaderStrategy getSkinableStrategy(String skinName){
        SkinLoaderStrategy ret = mStrategy;
        if(!TextUtils.isEmpty(skinName)){
            SkinLoaderStrategy r = mSkinStrategy.get(skinName);
            if(r != null ){
                ret = r ;
            }
        }
        return ret;
    }


    public SkinCompatManager.SkinLoaderStrategy getStrategy() {
        return mStrategy;
    }

    public SkinCompatManager.SkinLoaderStrategy getStrategy(String skinName) {
        return mSkinStrategy.get(skinName);
    }

    public boolean isDefaultSkin() {
        return isDefaultSkin;
    }

    @Deprecated
    public int getColor(int resId) {
        return getColor(SkinCompatManager.getInstance().getContext(), resId);
    }

    @Deprecated
    public Drawable getDrawable(int resId) {
        return getDrawable(SkinCompatManager.getInstance().getContext(), resId);
    }

    @Deprecated
    public ColorStateList getColorStateList(int resId) {
        return getColorStateList(SkinCompatManager.getInstance().getContext(), resId);
    }

    public int getTargetResId(Context context, int resId) {
        try {
            String resName = null;
            if (mStrategy != null) {
                resName = mStrategy.getTargetResourceEntryName(context, mSkinName, resId);
            }
            if (TextUtils.isEmpty(resName)) {
                resName = context.getResources().getResourceEntryName(resId);
            }
            String type = context.getResources().getResourceTypeName(resId);
            return mResources.getIdentifier(resName, type, mSkinPkgName);
        } catch (Exception e) {
            // 换肤失败不至于应用崩溃.
            return 0;
        }
    }


    public int getTargetResId(Context context,
                              Resources targetRes,
                              SkinCompatManager.SkinLoaderStrategy targetStrategy,
                              String targetSkinName,
                              String targetPkgName,
                              int resId) {

        if(targetRes == null){
            return getTargetResId(context,resId);
        }
        try {
            String resName = null;
            if (targetStrategy != null) {
                resName = targetStrategy.getTargetResourceEntryName(context, targetSkinName, resId);
            }
            if (TextUtils.isEmpty(resName)) {
                resName = context.getResources().getResourceEntryName(resId);
            }
            String type = context.getResources().getResourceTypeName(resId);
            return targetRes.getIdentifier(resName, type, targetPkgName);
        } catch (Exception e) {
            // 换肤失败不至于应用崩溃.
            return 0;
        }
    }

    private int getSkinColor(Context context, int resId,String skinName) {
        String pName = getSkinName(context);
        if(!TextUtils.isEmpty(skinName) || !TextUtils.isEmpty(pName)) {
            if(!TextUtils.isEmpty(pName)){
                skinName = pName;
            }
            boolean dSkin = false;
            SkinCompatManager.SkinLoaderStrategy  strategy = getSkinableStrategy(skinName);;
            Resources resources = getSkinableResource(skinName);
            String  pkgName = getSkinablePkgName(skinName);


            if (!SkinCompatUserThemeManager.get().isColorEmpty()) {
                ColorStateList colorStateList = SkinCompatUserThemeManager.get().getColorStateList(resId);
                if (colorStateList != null) {
                    return colorStateList.getDefaultColor();
                }
            }

            if (strategy != null) {
                ColorStateList colorStateList = strategy.getColor(context, skinName, resId);
                if (colorStateList != null) {
                    return colorStateList.getDefaultColor();
                }
            }
            if (!dSkin) {
                int targetResId = getTargetResId(context,resources,strategy,skinName,pkgName, resId);
                if (targetResId != 0) {
                   return resources.getColor(targetResId);
                }
            }
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return context.getResources().getColor(resId, context.getTheme());
        }
        return context.getResources().getColor(resId);
    }

    private ColorStateList getSkinColorStateList(Context context, int resId,String skinName) {
        String pName = getSkinName(context) ;
        ColorStateList colorStateList = null ;
        if(!TextUtils.isEmpty(skinName)  || !TextUtils.isEmpty(pName)) {
            if(!TextUtils.isEmpty(pName)){
                skinName = pName;
            }

            SkinCompatManager.SkinLoaderStrategy  strategy = getSkinableStrategy(skinName);;
            Resources resources = getSkinableResource(skinName);
            String  pkgName = getSkinablePkgName(skinName);
            if (!SkinCompatUserThemeManager.get().isColorEmpty()) {
                 colorStateList = SkinCompatUserThemeManager.get().getColorStateList(resId);
                if (colorStateList != null) {
                    return colorStateList;
                }
            }

            if (strategy != null) {
                 colorStateList = strategy.getColorStateList(context, skinName, resId);
                if (colorStateList != null) {
                    return colorStateList;
                }
            }

            int targetResId = getTargetResId(context,resources,strategy,skinName,pkgName, resId);
            if (targetResId != 0) {
                try {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        colorStateList =  resources.getColorStateList(targetResId,null);
                    }else{
                        colorStateList =  resources.getColorStateList(targetResId);
                    }
                }catch (Exception e){
                    logger.warn("getSkinColorStateList :skinName:{} resId:{},error:{}",skinName,resId,e.toString());
                }
                if(colorStateList != null){
                    return colorStateList;
                }
            }
        }
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                colorStateList =  context.getResources().getColorStateList(resId, context.getTheme());
            }else{
                colorStateList = context.getResources().getColorStateList(resId);
            }
        }catch (Exception e){
            logger.warn("getSkinColorStateList 2 :skinName:{} resId:{},error:{}",skinName,resId,e.toString());
        }
        return colorStateList;
    }

    /**
     * 获取页面当前专用的皮肤包资源
     * @param context
     * @return
     */
    private String getSkinName(Context context){
        return SkinActivityLifecycle.getSkinName(context);
    }

    private Drawable getSkinDrawable(Context context, int resId,String skinName) {
        String pName = getSkinName(context);
        Drawable drawable = null;
        if(!TextUtils.isEmpty(skinName) || !TextUtils.isEmpty(pName)){
            if(!TextUtils.isEmpty(pName)){
                skinName = pName;
            }
            SkinCompatManager.SkinLoaderStrategy  strategy = getSkinableStrategy(skinName);;
            Resources resources = getSkinableResource(skinName);
            String  pkgName = getSkinablePkgName(skinName);

            if (!SkinCompatUserThemeManager.get().isColorEmpty()) {
                ColorStateList colorStateList = SkinCompatUserThemeManager.get().getColorStateList(resId);
                if (colorStateList != null) {
                    return new ColorDrawable(colorStateList.getDefaultColor());
                }
            }
            if (!SkinCompatUserThemeManager.get().isDrawableEmpty()) {
                drawable = SkinCompatUserThemeManager.get().getDrawable(resId);
                if (drawable != null) {
                    return drawable;
                }
            }
            if (strategy != null) {
                 drawable = strategy.getDrawable(context, skinName, resId);
                if (drawable != null) {
                    return drawable;
                }
            }

            int targetResId = getTargetResId(context,resources,strategy,skinName,pkgName, resId);
            if (targetResId != 0) {
                try {
                    drawable =  resources.getDrawable(targetResId,null);
                }catch (Exception e){
                    logger.warn("getSkinDrawable:skinName = {},resId = {},error:{}",skinName,resId,e.toString());
                }
                if(drawable != null){
                    return  drawable;
                }
            }
        }
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                drawable =  context.getResources().getDrawable(resId, context.getTheme());
            }else{
                drawable =  context.getResources().getDrawable(resId);
            }
        }catch (Exception e){
            logger.warn("getSkinDrawable 2:skinName = {},resId = {},error:{}",skinName,resId,e.toString());
        }
        return drawable;
    }

    private Typeface getSkinFont(Context context,int resId,String skinName){
        String pName = getSkinName(context);
        if(!TextUtils.isEmpty(skinName) || !TextUtils.isEmpty(pName)){
            if(!TextUtils.isEmpty(pName)){
                skinName = pName;
            }
            boolean dSkin = false;
            SkinCompatManager.SkinLoaderStrategy  strategy = getSkinableStrategy(skinName);;
            Resources resources = getSkinableResource(skinName);
            String  pkgName = getSkinablePkgName(skinName);

            if (strategy != null) {
                Typeface font = strategy.getFont(context, skinName, resId);
                if (font != null) {
                    return font;
                }
            }
            if (!dSkin) {
                int targetResId = getTargetResId(context,resources,strategy,skinName,pkgName, resId);
                if (targetResId != 0) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        return resources.getFont(targetResId);
                    }
                }
            }
        }
        return ResourcesCompat.getFont(context,resId);
    }

    private int getSkinDimensionSize(Context context,int resId,String skinName){
        String pName = getSkinName(context);
        if(!TextUtils.isEmpty(skinName) || !TextUtils.isEmpty(pName)){
            if(!TextUtils.isEmpty(pName)){
                skinName = pName;
            }
            boolean dSkin = false;
            SkinCompatManager.SkinLoaderStrategy  strategy = getSkinableStrategy(skinName);;
            Resources resources = getSkinableResource(skinName);
            String  pkgName = getSkinablePkgName(skinName);

            if (strategy != null) {
                int size = strategy.getSize(context, skinName, resId);
                if (size != 0) {
                    return size;
                }
            }
            if (!dSkin) {
                int targetResId = getTargetResId(context,resources,strategy,skinName,pkgName, resId);
                if (targetResId != 0) {
                    return resources.getDimensionPixelSize(targetResId);
                }
            }
        }
        return context.getResources().getDimensionPixelSize(resId);
    }

    private float getSkinDimension(Context context,int resId,String skinName){
        String pName = getSkinName(context);
        if(!TextUtils.isEmpty(skinName) || !TextUtils.isEmpty(pName) ){
            if(!TextUtils.isEmpty(pName)){
                skinName = pName;
            }
            boolean dSkin = false;
            SkinCompatManager.SkinLoaderStrategy  strategy = getSkinableStrategy(skinName);;
            Resources resources = getSkinableResource(skinName);
            String  pkgName = getSkinablePkgName(skinName);

            if (strategy != null) {
                float size = strategy.getDimension(context, skinName, resId);
                if (size != 0f) {
                    return size;
                }
            }
            if (!dSkin) {
                int targetResId = getTargetResId(context,resources,strategy,skinName,pkgName, resId);
                if (targetResId != 0) {
                    return resources.getDimension(targetResId);
                }
            }
        }
        return context.getResources().getDimension(resId);
    }

    Drawable getStrategyDrawable(Context context, int resId) {
        if (mStrategy != null) {
            return mStrategy.getDrawable(context, mSkinName, resId);
        }
        return null;
    }

    private XmlResourceParser getSkinXml(Context context, int resId,String skinName) {
        String pName = getSkinName(context);
        if(!TextUtils.isEmpty(skinName) || !TextUtils.isEmpty(pName)) {
            if(!TextUtils.isEmpty(pName)){
                skinName = pName;
            }
            boolean dSkin = false;
            SkinCompatManager.SkinLoaderStrategy  strategy = getSkinableStrategy(skinName);;
            Resources resources = getSkinableResource(skinName);
            String  pkgName = getSkinablePkgName(skinName);

            if (!dSkin) {
                int targetResId = getTargetResId(context,resources,strategy,skinName,pkgName, resId);
                if (targetResId != 0) {
                    return resources.getXml(targetResId);
                }
            }
        }
        return context.getResources().getXml(resId);
    }

    private void getSkinValue(Context context, @AnyRes int resId, TypedValue outValue, boolean resolveRefs,String skinName) {
        String pName = getSkinName(context);
        if(!TextUtils.isEmpty(skinName) || !TextUtils.isEmpty(pName) ){
            if(!TextUtils.isEmpty(pName)){
                skinName = pName;
            }
            boolean dSkin = false;
            SkinCompatManager.SkinLoaderStrategy  strategy = getSkinableStrategy(skinName);;
            Resources resources = getSkinableResource(skinName);
            String  pkgName = getSkinablePkgName(skinName);

            if (!dSkin) {
                int targetResId = getTargetResId(context,resources,strategy,skinName,pkgName,resId);
                if (targetResId != 0) {
                    resources.getValue(targetResId, outValue, resolveRefs);
                    return;
                }
            }
        }
        context.getResources().getValue(resId, outValue, resolveRefs);
    }

    public static int getColor(Context context, @ColorRes int resId) {
        return getInstance().getSkinColor(context, resId, getInstance().getSkinName(context));
    }

    public static ColorStateList getColorStateList(Context context,@ColorRes int resId) {
        return getInstance().getSkinColorStateList(context, resId,getInstance().getSkinName(context));
    }

    public static Drawable getDrawable(Context context, @DrawableRes  int resId) {
        return getInstance().getSkinDrawable(context, resId,getInstance().getSkinName(context));
    }

    public static XmlResourceParser getXml(Context context, @XmlRes  int resId) {
        return getInstance().getSkinXml(context, resId,getInstance().getSkinName(context));
    }

    public static void getValue(Context context, @AnyRes int resId, TypedValue outValue, boolean resolveRefs) {
        getInstance().getSkinValue(context, resId, outValue, resolveRefs,getInstance().getSkinName(context));
    }

    public static Typeface getFont(Context context,@FontRes  int resId){
        return getInstance().getSkinFont(context,resId,getInstance().getSkinName(context));
    }

    public static int getSize(Context context,@DimenRes int resId){
        return getInstance().getSkinDimensionSize(context,resId,getInstance().getSkinName(context));
    }

    public static float getDimension(Context context,@DimenRes int resId){
        return getInstance().getSkinDimension(context,resId,getInstance().getSkinName(context));
    }


    public static int getColor(Context context, @ColorRes int resId,String skinName) {
        return getInstance().getSkinColor(context, resId, skinName);
    }

    public static ColorStateList getColorStateList(Context context,@ColorRes int resId,String skinName) {
        return getInstance().getSkinColorStateList(context, resId,skinName);
    }

    public static Drawable getDrawable(Context context, @DrawableRes  int resId,String skinName) {
        return getInstance().getSkinDrawable(context, resId,skinName);
    }

    public static XmlResourceParser getXml(Context context, @XmlRes  int resId,String skinName) {
        return getInstance().getSkinXml(context, resId,skinName);
    }

    public static void getValue(Context context, @AnyRes int resId, TypedValue outValue, boolean resolveRefs,String skinName) {
        getInstance().getSkinValue(context, resId, outValue, resolveRefs,skinName);
    }

    public static Typeface getFont(Context context,@FontRes  int resId,String skinName){
        return getInstance().getSkinFont(context,resId,skinName);
    }

    public static int getSize(Context context,@DimenRes int resId,String skinName){
        return getInstance().getSkinDimensionSize(context,resId,skinName);
    }

    public static float getDimension(Context context,@DimenRes int resId,String skinName){
        return getInstance().getSkinDimension(context,resId,skinName);
    }

}
