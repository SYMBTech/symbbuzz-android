package symbbuzz.com.symbbuzzlib.app

class Application: android.app.Application() {
    companion object{
        lateinit var instance: Application
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
    }
}
