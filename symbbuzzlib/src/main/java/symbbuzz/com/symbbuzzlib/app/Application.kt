package symbbuzz.com.symbbuzzlib.app

import com.crashlytics.android.Crashlytics
import io.fabric.sdk.android.Fabric

class Application: android.app.Application() {
    companion object{
        lateinit var instance: Application
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
        Fabric.with(this, Crashlytics())
    }
}
