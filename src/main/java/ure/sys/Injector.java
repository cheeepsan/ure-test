package ure.sys;

import ure.sys.dagger.AppComponent;
import ure.sys.dagger.AppModule;
import ure.sys.dagger.DaggerAppComponent;

/**
 * This is a static class that provides easy access to our AppModule singleton for dependency
 * injection wherever it's needed.  See the docs for dagger.AppComponent for more details.
 *
 */
public class Injector {

    public static AppComponent appComponent;

    public static AppComponent getAppComponent() {
        if (appComponent == null) {
            appComponent = DaggerAppComponent.builder()
                    .appModule(new AppModule())
                    .build();
        }
        return appComponent;
    }
}
