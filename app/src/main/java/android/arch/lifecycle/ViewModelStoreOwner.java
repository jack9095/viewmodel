package android.arch.lifecycle;

import android.support.annotation.NonNull;

/**
 * 此接口实现的职责是在配置更改期间保留所拥有的 ViewModelStore，并在要销毁此范围时调用 viewModelStore clear（）。
 * 这个接口使用者一般不会去实现，都是系统创建 HolderFragment 的时候去实现的
 */
@SuppressWarnings("WeakerAccess")
public interface ViewModelStoreOwner {

    @NonNull
    ViewModelStore getViewModelStore();
}
