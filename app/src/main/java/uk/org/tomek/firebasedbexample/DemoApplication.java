package uk.org.tomek.firebasedbexample;

import android.app.Application;

import timber.log.Timber;

/**
 * Main application class.
 */
public class DemoApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Timber.plant(new Timber.DebugTree());
    }
}
