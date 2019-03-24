package android.arch.lifecycle;

import java.util.HashMap;

/**
 * 用来存储 ViewModel
 * <p>
 * 必须通过配置更改保留 ViewModelStore 的实例：
 * <p>
 * 如果此 ViewModelStore 的所有者由于配置而被销毁并重新创建
 * 更改，所有者的新实例仍应具有相同的旧实例
 * <p>
 * 如果此 ViewModelStore 的所有者被销毁，并且不会被重新创建，
 * 然后它应该在这个 viewModelStore 上调用 clear（），因此 ViewModels 通知他们不再使用。
 * 当Activity或者Fragment销毁的时候就会调用clear方法
 *
 * TODO 问题点：
 * 1.ViewModelStore被HolderFragment创建和持有
 *
 * TODO ViewModelStore 是每一个 Activity 或者 Fragment 都有一个
 */
public class ViewModelStore {

    private final HashMap<String, ViewModel> mMap = new HashMap<>();

    final void put(String key, ViewModel viewModel) {
        ViewModel oldViewModel = mMap.put(key, viewModel);
        if (oldViewModel != null) {
            oldViewModel.onCleared();
        }
    }

    final ViewModel get(String key) {
        return mMap.get(key);
    }

    /**
     * 清除内部存储并通知 ViewModel 它们不再使用
     */
    public final void clear() {
        for (ViewModel vm : mMap.values()) {
            vm.onCleared();
        }
        mMap.clear();
    }
}
