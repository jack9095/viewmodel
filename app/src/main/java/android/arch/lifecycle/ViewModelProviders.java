package android.arch.lifecycle;

import android.app.Activity;
import android.app.Application;
import android.arch.lifecycle.ViewModelProvider.Factory;
import android.support.annotation.MainThread;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;

/**
 * ViewModel的创建不可直接new，需要使用这个 ViewModelProviders 才能与Activity或者
 * Fragment的生命周期关联起来！
 * ViewModel 的存在是依赖 Activity 或者 Fragment的，不管你在什么地方获取ViewModel，
 * 只要你用的是相同的Activity或者 Fragment，那么获取到的ViewModel将是同一个 (前提是key值是一样的)，所以ViewModel 也具有数据共享的作用！
 */
public class ViewModelProviders {

    /**
     * @deprecated 不应直接实例化此类
     */
    @Deprecated
    public ViewModelProviders() {
    }

    /**
     * 通过Activity获取可用的Application或者检测Activity是否可用
     */
    private static Application checkApplication(Activity activity) {
        Application application = activity.getApplication();
        if (application == null) {
            throw new IllegalStateException("Your activity/fragment is not yet attached to "
                    + "Application. You can't request ViewModel before onCreate call.");
        }
        return application;
    }

    /**
     * 通过Fragment获取Activity或者检测Fragment是否可用
     */
    private static Activity checkActivity(Fragment fragment) {
        Activity activity = fragment.getActivity();
        if (activity == null) {
            throw new IllegalStateException("Can't create ViewModelProvider for detached fragment");
        }
        return activity;
    }

    /**
     * 通过Fragment获得ViewModelProvider
     * ViewModelProvider.AndroidViewModelFactory 来实例化新的 ViewModels
     */
    @NonNull
    @MainThread
    public static ViewModelProvider of(@NonNull Fragment fragment) {
        return of(fragment, null);
    }

    /**
     * 使用 ViewModelProvider.AndroidViewModelFactory 来实例化新的 ViewModels.
     */
    @NonNull
    @MainThread
    public static ViewModelProvider of(@NonNull FragmentActivity activity) {
        return of(activity, null);
    }

    /**
     * 通过给定的工厂来实例化一个新的 ViewModels.
     */
    @NonNull
    @MainThread
    public static ViewModelProvider of(@NonNull Fragment fragment, @Nullable Factory factory) {
        Application application = checkApplication(checkActivity(fragment));
        if (factory == null) {
            // 获取默认的单例 AndroidViewModelFactory，它内部是通过反射来创建具体的 ViewModel
            factory = ViewModelProvider.AndroidViewModelFactory.getInstance(application);
        }

        // 利用 HolderFragment 来关联生命周期并使用 HolderFragment 中的 ViewModelStore 的 HashMap 存储 ViewModel
        // AndroidViewModelFactory 创建 ViewModel
        // 为本次的ViewModel获取创建一个 ViewModelProvider
        return new ViewModelProvider(ViewModelStores.of(fragment), factory);
    }

    /**
     * 通过给定的工厂来实例化一个新的 ViewModels.
     */
    @NonNull
    @MainThread
    public static ViewModelProvider of(@NonNull FragmentActivity activity, @Nullable Factory factory) {
        Application application = checkApplication(activity);
        if (factory == null) {
            factory = ViewModelProvider.AndroidViewModelFactory.getInstance(application);
        }
        // HolderFragment 在这一步中会被创建添加到对应的 Activity  或者 Fragment 中
        return new ViewModelProvider(ViewModelStores.of(activity), factory);
    }

    /**
     * 工厂可创建 AndroidViewModel 和 ViewModel，具有空构造函数的.
     * <p>
     * 不推荐使用 ViewModelProvider.AndroidViewModelFactory
     */
    @SuppressWarnings("WeakerAccess")
    @Deprecated
    public static class DefaultFactory extends ViewModelProvider.AndroidViewModelFactory {
        /**
         * 不推荐使用 ViewModelProvider.AndroidViewModelFactory 和
         * ViewModelProvider.AndroidViewModelFactory.getInstance(Application)的方式创建 AndroidViewModelFactory
         */
        @Deprecated
        public DefaultFactory(@NonNull Application application) {
            super(application);
        }
    }
}
