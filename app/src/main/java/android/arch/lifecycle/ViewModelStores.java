package android.arch.lifecycle;

import static android.arch.lifecycle.HolderFragment.holderFragmentFor;

import android.support.annotation.MainThread;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;

/**
 * Factory methods for {@link ViewModelStore} class.
 */
@SuppressWarnings("WeakerAccess")
public class ViewModelStores {

    private ViewModelStores() {
    }

    /**
     * 如果你的 Activity 实现了 ViewModelStoreOwner 接口具备了提供 ViewModelStore 的功能就直接获取返回
     * 通常我们的 Activity 都不会去实现这个功能
     * TODO 为 Activity 创建 ViewModelStore
     */
    @NonNull
    @MainThread
    public static ViewModelStore of(@NonNull FragmentActivity activity) {
        if (activity instanceof ViewModelStoreOwner) {
            return ((ViewModelStoreOwner) activity).getViewModelStore();
        }
        // 系统为你的 Activity 添加一个具有提供 ViewModelStore 的功能的 holderFragment
        return holderFragmentFor(activity).getViewModelStore();
    }

    /**
     * 如果你的 Fragment 实现了 ViewModelStoreOwner 接口具备了提供 ViewModelStore 的功能就直接获取返回
     * 通常我们的 Fragment 都不会去实现这个功能
     * TODO 为 Fragment 创建 ViewModelStore
     */
    @NonNull
    @MainThread
    public static ViewModelStore of(@NonNull Fragment fragment) {
        if (fragment instanceof ViewModelStoreOwner) {
            return ((ViewModelStoreOwner) fragment).getViewModelStore();
        }
        // 系统为你的 Fragment 添加一个具有提供 ViewModelStore 的功能的 holderFragment
        return holderFragmentFor(fragment).getViewModelStore();
    }
}
