package cn.trainservice.trainservice.service.movie;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Environment;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import cn.trainservice.trainservice.R;
import dalvik.system.DexClassLoader;

public class ProxyActivity extends Activity {
    private Resources mBundleResources;

    public void replaceContextResources(Context context, String apk) {
        try {

            if (null == mBundleResources) {

                AssetManager assetManager = AssetManager.class.newInstance();
                Method addAssetPath = assetManager.getClass().getMethod("addAssetPath", String.class);
                addAssetPath.invoke(assetManager, apk);
                Resources superRes = context.getResources();
                mBundleResources = new Resources(assetManager, superRes.getDisplayMetrics(), superRes.getConfiguration());
            }
        }catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }  catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // ProxyActivity要加载的插件Activity名字
        try {
            File storageDirectory = Environment.getExternalStorageDirectory();
            String path = storageDirectory.getPath() + "/trainserver/";
            String fileName = "superplayer.apk";
            Intent intent = getIntent();
            String title = intent.getStringExtra("title");
            String sourceUrl = intent.getStringExtra("sourceUrl");
            File dexOutputDir = getBaseContext().getDir("dex", 0);

            ClassLoader localClassLoader = ClassLoader.getSystemClassLoader();
            DexClassLoader localDexClassLoader = new DexClassLoader(path + fileName, dexOutputDir.getAbsolutePath(), null, localClassLoader.getParent());
            PackageInfo localPackageInfo = this.getPackageManager().getPackageArchiveInfo(path + fileName, PackageManager.GET_ACTIVITIES);
            replaceContextResources(this, path + fileName);

            if ((localPackageInfo.activities != null) && (localPackageInfo.activities.length > 0)) {
                String activityName = localPackageInfo.activities[1].name;
                Class<?> localClass = localDexClassLoader.loadClass(activityName);
                Constructor<?> localConstructor = localClass.getConstructor(new Class[]{});
                Object localInstance = localConstructor.newInstance(new Object[]{});
                // 把当前的傀儡Activity注入到插件中
                Method setProxy = localClass.getMethod("setProxy", new Class[]{Activity.class});
                setProxy.setAccessible(true);
                setProxy.invoke(localInstance, new Object[]{this});
                Field tag_title = localClass.getDeclaredField("title");
                Field tag_sourceUrl = localClass.getDeclaredField("sourceUrl");
                Field tag_mResourcesl = localClass.getDeclaredField("mResources");
                tag_title.setAccessible(true);
                tag_sourceUrl.setAccessible(true);
                tag_mResourcesl.setAccessible(true);
                tag_title.set(localInstance, title);
                tag_sourceUrl.set(localInstance, sourceUrl);
               // tag_mResourcesl.set(localInstance, sourceUrl);
                // 调用插件的onCreate()
                Method onCreate = localClass.getDeclaredMethod("onCreate", new Class[]{Bundle.class});
                onCreate.setAccessible(true);
                onCreate.invoke(localInstance,new Bundle());
            }
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
    }
}