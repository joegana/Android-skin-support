package skin.support;


import android.app.Application;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.SparseArray;
import androidx.annotation.DimenRes;
import androidx.annotation.FontRes;
import com.moorgen.sdk.common.CUtilKt;
import java.util.ArrayList;
import java.util.List;
import skin.support.annotation.NonNull;
import skin.support.annotation.Nullable;
import skin.support.app.SkinActivityLifecycle;
import skin.support.app.SkinLayoutInflater;
import skin.support.app.SkinWrapper;
import skin.support.load.SkinAssetsLoader;
import skin.support.load.SkinBuildInLoader;
import skin.support.load.SkinNoneLoader;
import skin.support.load.SkinPrefixBuildInLoader;
import skin.support.observe.SkinObservable;
import skin.support.observe.SkinObserver;
import skin.support.utils.SkinPreference;
import skin.support.content.res.SkinCompatResources;

public class SkinCompatManager extends SkinObservable {
    public static final String  DEFAULT_SKIN_NAME = SkinPreference.DEFAULT_SKIN_NAME;
    public static final int SKIN_LOADER_STRATEGY_NONE = -1;
    public static final int SKIN_LOADER_STRATEGY_ASSETS = 0;
    public static final int SKIN_LOADER_STRATEGY_BUILD_IN = 1;
    public static final int SKIN_LOADER_STRATEGY_PREFIX_BUILD_IN = 2;
    private static volatile SkinCompatManager sInstance;
    private final Object mLock = new Object();
    private final Context mAppContext;
    private  SkinActivityLifecycle skinActivityLifecycle;
    private boolean mLoading = false;
    private List<SkinWrapper> mWrappers = new ArrayList<>();
    private List<SkinLayoutInflater> mInflaters = new ArrayList<>();
    private List<SkinLayoutInflater> mHookInflaters = new ArrayList<>();
    private SparseArray<SkinLoaderStrategy> mStrategyMap = new SparseArray<>();
    private boolean mSkinAllActivityEnable = true;
    private boolean mSkinStatusBarColorEnable = false;
    private boolean mSkinWindowBackgroundColorEnable = true;

    /**
     * 皮肤包加载监听.
     */
    public interface SkinLoaderListener {
        /**
         * 开始加载.
         */
        void onStart();

        /**
         * 加载成功.
         */
        void onSuccess();

        /**
         * 加载失败.
         *
         * @param errMsg 错误信息.
         */
        void onFailed(String errMsg);
    }

    /**
     * 皮肤包加载策略.
     */
    public interface SkinLoaderStrategy {
        /**
         * 加载皮肤包.
         *
         * @param context  {@link Context}
         * @param skinName 皮肤包名称.
         * @return 加载成功，返回皮肤包名称；失败，则返回空。
         */
        String loadSkinInBackground(Context context, String skinName);

        /**
         * 根据应用中的资源ID，获取皮肤包相应资源的资源名.
         *
         * @param context  {@link Context}
         * @param skinName 皮肤包名称.
         * @param resId    应用中需要换肤的资源ID.
         * @return 皮肤包中相应的资源名.
         */
        String getTargetResourceEntryName(Context context, String skinName, int resId);

        /**
         * 开发者可以拦截应用中的资源ID，返回对应color值。
         *
         * @param context  {@link Context}
         * @param skinName 皮肤包名称.
         * @param resId    应用中需要换肤的资源ID.
         * @return 获得拦截后的颜色值，添加到ColorStateList的defaultColor中。不需要拦截，则返回空
         */
        ColorStateList getColor(Context context, String skinName, int resId);

        /**
         * 开发者可以拦截应用中的资源ID，返回对应ColorStateList。
         *
         * @param context  {@link Context}
         * @param skinName 皮肤包名称.
         * @param resId    应用中需要换肤的资源ID.
         * @return 返回对应ColorStateList。不需要拦截，则返回空
         */
        ColorStateList getColorStateList(Context context, String skinName, int resId);

        /**
         * 开发者可以拦截应用中的资源ID，返回对应Drawable。
         *
         * @param context  {@link Context}
         * @param skinName 皮肤包名称.
         * @param resId    应用中需要换肤的资源ID.
         * @return 返回对应Drawable。不需要拦截，则返回空
         */
        Drawable getDrawable(Context context, String skinName, int resId);


        /**
         * 开发者可以拦截应用中的资源ID，返回对应TypeFace。
         * @param context
         * @param skinName
         * @param resId
         * @return
         */
        Typeface getFont(Context context,String skinName,@FontRes  int resId);

        /**
         * 开发者可以拦截应用中的资源ID，返回对应 dimension size。
         * @param context
         * @param skinName
         * @param resId
         * @return
         */
        int getSize(Context context,String skinName,@DimenRes  int resId);

        /**
         * 开发者可以拦截应用中的资源ID，返回对应 dimension size。
         * @param context
         * @param skinName
         * @param resId
         * @return
         */
        float getDimension(Context context,String skinName,@DimenRes  int resId);


        /**
         * {@link #SKIN_LOADER_STRATEGY_NONE}
         * {@link #SKIN_LOADER_STRATEGY_ASSETS}
         * {@link #SKIN_LOADER_STRATEGY_BUILD_IN}
         * {@link #SKIN_LOADER_STRATEGY_PREFIX_BUILD_IN}
         *
         * @return 皮肤包加载策略类型.
         */
        int getType();
    }

    /**
     * @param context
     * @return
     */
    public static SkinCompatManager init(Context context) {
        if (sInstance == null) {
            synchronized (SkinCompatManager.class) {
                if (sInstance == null) {
                    sInstance = new SkinCompatManager(context);
                    SkinPreference.init(context);
                    SkinCompatResources.getInstance().reset();//重置皮肤资源到默认
                }
            }
        }
        return sInstance;
    }

    public static SkinCompatManager getInstance() {
        return sInstance;
    }

    /**
     * 初始化换肤框架，监听Activity生命周期. 通过该方法初始化.
     *
     * @param application 应用Application.
     * @return
     */
    public static SkinCompatManager withoutActivity(Application application) {
        init(application);
        SkinActivityLifecycle.init(application);
        return sInstance;
    }

    /**
     * 设置 Context 当前使用的皮肤包名称
     * @param context
     * @param skinName
     */
    public static void setSkinName(Context context,String skinName){
        SkinActivityLifecycle.setSkinName(context,skinName);
    }

    /**
     * 界定一个Context当前是否是换肤的Context
     * @param context
     * @return
     */
    public static boolean isSupportSkin(Context context){
        return SkinActivityLifecycle.skinableContext(context);
    }

    /**
     * 获取一个Context当前的皮肤资源名称
     * @param context
     * @return
     */
    public static String getSkinName(Context context){
        return SkinActivityLifecycle.getSkinName(context);
    }

    public SkinObserver getSkinObserver(Context mContext){
        SkinActivityLifecycle instance = SkinActivityLifecycle.init(null);
        if(instance != null){
            return instance.acquireObserver(mContext);
        }
        return null ;
    }

    public  ArrayList<Context> whoHasObservers(){
        SkinActivityLifecycle instance = SkinActivityLifecycle.init(null);
        if(instance != null){
            return instance.whoHasObservers();
        }
        return null ;
    }


    private SkinCompatManager(Context context) {
        mAppContext = context.getApplicationContext();
        initLoaderStrategy();
    }

    private void initLoaderStrategy() {
        mStrategyMap.put(SKIN_LOADER_STRATEGY_NONE, new SkinNoneLoader());
        mStrategyMap.put(SKIN_LOADER_STRATEGY_ASSETS, new SkinAssetsLoader());
        mStrategyMap.put(SKIN_LOADER_STRATEGY_BUILD_IN, new SkinBuildInLoader());
        mStrategyMap.put(SKIN_LOADER_STRATEGY_PREFIX_BUILD_IN, new SkinPrefixBuildInLoader());
    }

    public Context getContext() {
        return mAppContext;
    }

    /**
     * 添加皮肤包加载策略.
     *
     * @param strategy 自定义加载策略
     * @return
     */
    public SkinCompatManager addStrategy(SkinLoaderStrategy strategy) {
        mStrategyMap.put(strategy.getType(), strategy);
        return this;
    }

    public SparseArray<SkinLoaderStrategy> getStrategies() {
        return mStrategyMap;
    }

    /**
     * 自定义View换肤时，可选择添加一个{@link SkinLayoutInflater}
     * @return
     */
    public SkinCompatManager addInflater(SkinLayoutInflater inflater) {
        if (inflater instanceof SkinWrapper) {
            mWrappers.add((SkinWrapper) inflater);
        }
        mInflaters.add(inflater);
        return this;
    }

    public List<SkinWrapper> getWrappers() {
        return mWrappers;
    }

    public List<SkinLayoutInflater> getInflaters() {
        return mInflaters;
    }


    /**
     * 自定义View换肤时，可选择添加一个{@link SkinLayoutInflater}
     * @return
     */
    @Deprecated
    public SkinCompatManager addHookInflater(SkinLayoutInflater inflater) {
        mHookInflaters.add(inflater);
        return this;
    }

    @Deprecated
    public List<SkinLayoutInflater> getHookInflaters() {
        return mHookInflaters;
    }

    /**
     * 获取当前皮肤包.
     *
     * @return
     */
    @Deprecated
    public String getCurSkinName() {
        return SkinPreference.getInstance().getSkinName();
    }

    /**
     * 恢复默认主题，使用应用自带资源.
     */
    public void restoreDefaultTheme() {
        loadSkin("", SKIN_LOADER_STRATEGY_NONE);
    }

    /**
     * 设置是否所有Activity都换肤.
     *
     * @param enable true: 所有Activity都换肤; false: 添加注解Skinable或实现SkinCompatSupportable的Activity支持换肤.
     * @return
     */
    public SkinCompatManager setSkinAllActivityEnable(boolean enable) {
        mSkinAllActivityEnable = enable;
        return this;
    }

    public boolean isSkinAllActivityEnable() {
        return mSkinAllActivityEnable;
    }

    @Deprecated
    public SkinCompatManager setSkinStatusBarColorEnable(boolean enable) {
        mSkinStatusBarColorEnable = enable;
        return this;
    }

    @Deprecated
    public boolean isSkinStatusBarColorEnable() {
        return mSkinStatusBarColorEnable;
    }

    /**
     * 设置WindowBackground换肤，使用Theme中的{@link android.R.attr#windowBackground}属性.
     *
     * @param enable true: 打开; false: 关闭.
     * @return
     */
    public SkinCompatManager setSkinWindowBackgroundEnable(boolean enable) {
        mSkinWindowBackgroundColorEnable = enable;
        return this;
    }

    public boolean isSkinWindowBackgroundEnable() {
        return mSkinWindowBackgroundColorEnable;
    }

    /**
     * 加载记录的皮肤包，一般在Application中初始化换肤框架后调用.
     *
     * @return
     */
    public AsyncTask loadSkin() {
       return  innerLoadSkin(null,false);
    }

    public AsyncTask syncLoadSkin() {
        return innerLoadSkin(null,true);
    }

    /**
     * 加载记录的皮肤包，一般在Application中初始化换肤框架后调用.
     * @param listener 皮肤包加载监听.
     * @return
     */
    public AsyncTask loadSkin(SkinLoaderListener listener) {
        return innerLoadSkin(listener,false);
    }

    public AsyncTask syncLoadSkin(SkinLoaderListener listener) {
        return innerLoadSkin(listener,true);
    }

    private AsyncTask innerLoadSkin(SkinLoaderListener listener,Boolean main){
        String skin = SkinPreference.getInstance().getSkinName();
        int strategy = SkinPreference.getInstance().getSkinStrategy();
        if (TextUtils.isEmpty(skin) || strategy == SKIN_LOADER_STRATEGY_NONE) {
            return null;
        }
        return loadSkin(skin, listener, strategy,main);
    }

    @Deprecated
    public AsyncTask loadSkin(String skinName) {
        return loadSkin(skinName, null);
    }
    @Deprecated
    public AsyncTask syncLoadSkin(String skinName) {
        return loadSkin(skinName, null,SKIN_LOADER_STRATEGY_ASSETS,true);
    }


    @Deprecated
    public AsyncTask loadSkin(String skinName, final SkinLoaderListener listener) {
        return loadSkin(skinName, listener, SKIN_LOADER_STRATEGY_ASSETS,false);
    }

    @Deprecated
    public AsyncTask syncLoadSkin(String skinName, final SkinLoaderListener listener) {
        return loadSkin(skinName, listener, SKIN_LOADER_STRATEGY_ASSETS,true);
    }

     /**
     * 加载皮肤包.
     *
     * @param skinName 皮肤包名称.
     * @param strategy 皮肤包加载策略.
     * @return
     */
     @androidx.annotation.Nullable
    public AsyncTask loadSkin(String skinName, int strategy) {
        return loadSkin(skinName, null, strategy,false);
    }

    @androidx.annotation.Nullable
    public AsyncTask syncLoadSkin(String skinName, int strategy) {
        return loadSkin(skinName, null, strategy,true);
    }

    public AsyncTask loadSkin(String skinName, SkinLoaderListener listener, int strategy){
        return loadSkin(skinName, listener, strategy,false);
    }

    public AsyncTask syncLoadSkin(String skinName, SkinLoaderListener listener, int strategy){
        return loadSkin(skinName, listener, strategy,true);
    }

     /**
     * 加载皮肤包.
     *
     * @param skinName 皮肤包名称.
     * @param listener 皮肤包加载监听.
     * @param strategy 皮肤包加载策略.
     * @return
     */
     @androidx.annotation.Nullable
    public AsyncTask loadSkin(String skinName, SkinLoaderListener listener, int strategy,boolean sync) {
        SkinLoaderStrategy loaderStrategy = mStrategyMap.get(strategy);
        if (loaderStrategy == null) {
            return null;
        }
        SkinCompatResources skinRes =  SkinCompatResources.getInstance();
         Resources resources = skinRes.getSkinResources(skinName);
         boolean notifySkinChanged = false ;
        if(resources != null && !DEFAULT_SKIN_NAME.equals(skinName)){
            SkinPreference.getInstance().setSkinName(skinName).setSkinStrategy(
                    skinRes.getStrategy(skinName).getType());
            SkinCompatResources.getInstance().setupSkin(
                    resources,
                    skinRes.getSkinPkgName(skinName),
                    skinName,
                    skinRes.getStrategy(skinName));
            notifySkinChanged = true;
        }else if(TextUtils.isEmpty(skinName) || DEFAULT_SKIN_NAME.equals(skinName)){
            SkinPreference.getInstance().setSkinName("").setSkinStrategy(SKIN_LOADER_STRATEGY_NONE);
            SkinCompatResources.getInstance().reset();

            notifySkinChanged = true;
        }
        if(notifySkinChanged){
            CUtilKt.callOnMain(0, null,null, () -> {
                notifyUpdateSkin();
                if(listener != null){
                    listener.onSuccess();
                }
            });
            return null ;
        }
         SkinLoadTask task =   new SkinLoadTask(listener, loaderStrategy);
         if(sync){
             task.onPreExecute();
             task.doInBackground(skinName);
             task.onPostExecute(skinName);
         }else{
             task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, skinName);
         }
         return task ;
    }

    private class SkinLoadTask extends AsyncTask<String, Void, String> {
        private final SkinLoaderListener mListener;
        private final SkinLoaderStrategy mStrategy;
        SkinLoadTask(@Nullable SkinLoaderListener listener,
                     @NonNull SkinLoaderStrategy strategy) {
            mListener = listener;
            mStrategy = strategy;
        }

        @Override
        protected void onPreExecute() {
            if (mListener != null) {
                mListener.onStart();
            }
        }

        @Override
        protected String doInBackground(String... params) {
            synchronized (mLock) {
                while (mLoading) {
                    try {
                        mLock.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                mLoading = true;
            }
            try {
                if (params.length == 1) {
                    String skinName = mStrategy.loadSkinInBackground(mAppContext, params[0]);
                    if (TextUtils.isEmpty(skinName)) {
                        SkinCompatResources.getInstance().reset(mStrategy);
                        return "";
                    }
                    return params[0];
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            SkinCompatResources.getInstance().reset();
            return null;
        }

        private void notify(String skinName){
            if (skinName != null) {
                SkinPreference.getInstance().setSkinName(skinName).setSkinStrategy(mStrategy.getType());
                notifyUpdateSkin();
                if (mListener != null) {
                    mListener.onSuccess();
                }
            } else {
                SkinPreference.getInstance().setSkinName("").setSkinStrategy(SKIN_LOADER_STRATEGY_NONE);
                if (mListener != null) {
                    mListener.onFailed("皮肤资源获取失败");
                }
            }
        }

        @Override
        protected void onPostExecute(String skinName) {
            synchronized (mLock) {
                // skinName 为""时，恢复默认皮肤
                notify(skinName);
                mLock.notifyAll();
            }
            mLoading = false;
        }
    }

    /**
     * 获取皮肤包包名.
     *
     * @param skinPkgPath sdcard中皮肤包路径.
     * @return
     */
    public String getSkinPackageName(String skinPkgPath) {
        PackageManager mPm = mAppContext.getPackageManager();
        PackageInfo info = mPm.getPackageArchiveInfo(skinPkgPath, PackageManager.GET_ACTIVITIES);
        return info.packageName;
    }

    /**
     * 获取皮肤包资源{@link Resources}.
     *
     * @param skinPkgPath sdcard中皮肤包路径.
     * @return
     */
    @Nullable
    public Resources getSkinResources(String skinPkgPath) {
        try {
            PackageInfo packageInfo = mAppContext.getPackageManager().getPackageArchiveInfo(skinPkgPath, 0);
            packageInfo.applicationInfo.sourceDir = skinPkgPath;
            packageInfo.applicationInfo.publicSourceDir = skinPkgPath;
            Resources res = mAppContext.getPackageManager().getResourcesForApplication(packageInfo.applicationInfo);
            Resources superRes = mAppContext.getResources();
            return new Resources(res.getAssets(), superRes.getDisplayMetrics(), superRes.getConfiguration());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}