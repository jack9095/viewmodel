package android.arch.lifecycle;

import android.annotation.SuppressLint;
import android.app.Application;
import android.support.annotation.NonNull;

/**
 * 感知应用程序的上下文
 * <p>
 * 子类必须有一个接受 application 作为唯一参数的构造函数。
 * <p>
 */
public class AndroidViewModel extends ViewModel {
    @SuppressLint("StaticFieldLeak")
    private Application mApplication;

    public AndroidViewModel(@NonNull Application application) {
        mApplication = application;
    }

    /**
     * 返回对应的 Application
     */
    @SuppressWarnings("TypeParameterUnusedInFormals")
    @NonNull
    public <T extends Application> T getApplication() {
        //noinspection unchecked
        return (T) mApplication;
    }
}
