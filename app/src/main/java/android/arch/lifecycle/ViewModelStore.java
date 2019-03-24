package android.arch.lifecycle;

import java.util.HashMap;

/**
 * Class to store {@code ViewModels}.
 * <p>
 * An instance of {@code ViewModelStore} must be retained through configuration changes:
 * if an owner of this {@code ViewModelStore} is destroyed and recreated due to configuration
 * changes, new instance of an owner should still have the same old instance of
 * {@code ViewModelStore}.
 * <p>
 * If an owner of this {@code ViewModelStore} is destroyed and is not going to be recreated,
 * then it should call {@link #clear()} on this {@code ViewModelStore}, so {@code ViewModels} would
 * be notified that they are no longer used.
 * <p>
 * {@link android.arch.lifecycle.ViewModelStores} provides a {@code ViewModelStore} for
 * activities and fragments.
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
     *  Clears internal storage and notifies ViewModels that they are no longer used.
     */
    public final void clear() {
        for (ViewModel vm : mMap.values()) {
            vm.onCleared();
        }
        mMap.clear();
    }
}
