package facebook.content.app;

import android.app.ActivityManager;
import android.app.Application;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;

import com.facebook.drawee.backends.pipeline.Fresco;

public class FacebookContentProviderApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        //Timber.plant(new Timber.DebugTree());

        Fresco.initialize(this);

    }
}
