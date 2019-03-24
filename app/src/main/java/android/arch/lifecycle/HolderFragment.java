package android.arch.lifecycle;

import android.app.Activity;
import android.app.Application.ActivityLifecycleCallbacks;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RestrictTo;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentManager.FragmentLifecycleCallbacks;
import android.util.Log;

import java.util.HashMap;
import java.util.Map;


/**
 * HolderFragment跟我们的Activity或者Fragment有什么关系？
    答：当我们要给Activity或者Fragment创建ViewModel的时候，系统就会为Activity或者Fragment添加一个HolderFragment，HolderFragment中会创建持有一个ViewModelStore
 */
@RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
public class HolderFragment extends Fragment implements ViewModelStoreOwner {
    private static final String LOG_TAG = "ViewModelStores";

    private static final HolderFragmentManager sHolderFragmentManager = new HolderFragmentManager();

    /**
     * @hide
     */
    @RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
    public static final String HOLDER_TAG = "android.arch.lifecycle.state.StateProviderHolderFragment";

    // TODO 一句话总结`ViewModel`是怎么被存储的:
    // 答： `ViewModel`是存储在当前`Activity / Fragment` 的 `HolderFragment` 中的`ViewModelStore`的HashMap中，
    // 我们可以`get`,`put`或者在`Activity / Fragment `销毁的时候`HolderFragment`会跟随销毁，
    // 在`HolderFragment`的`onDestroy`方法中调用`mViewModelStore`的`clear`方法。
    //  HolderFragment伴随销毁时调用自己所有ViewModel的onCleared方法.
    private ViewModelStore mViewModelStore = new ViewModelStore();

    /**
     * 实现原理就是巧妙滴借用了Fragment的setRetainInstance(true)属性。
     * 关于setRetainInstance更多介绍可以参考：https://blog.csdn.net/airk000/article/details/38557605
     */
    public HolderFragment() {
        /* TODO
            setRetainInstance(boolean) 是Fragment中的一个方法。将这个方法设置为true就可以使当前Fragment在Activity重建时存活下来,
            如果不设置或者设置为 false, 当前 Fragment 会在 Activity 重建时同样发生重建, 以至于被新建的对象所替代。 
            在setRetainInstance(boolean)为true的 Fragment （就是HolderFragment）中放一个专门用于存储ViewModel的Map,
            这样Map中所有的ViewModel都会幸免于Activity的配置改变导致的重建，让需要创建ViewModel的Activity,
            Fragment都绑定一个这样的Fragment（就是HolderFragment）, 将ViewModel存放到这个 Fragment 的 Map 中, ViewModel 组件就这样实现了。
         */
        setRetainInstance(true);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sHolderFragmentManager.holderFragmentCreated(this);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mViewModelStore.clear(); // 清空 Activity 或者 Fragment 中的 ViewModel
    }

    @NonNull
    @Override
    public ViewModelStore getViewModelStore() {
        return mViewModelStore;
    }

    /**
     * @hide
     */
    @RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
    public static HolderFragment holderFragmentFor(FragmentActivity activity) {
        return sHolderFragmentManager.holderFragmentFor(activity);
    }

    /**
     * @hide
     */
    @RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
    public static HolderFragment holderFragmentFor(Fragment fragment) {
        return sHolderFragmentManager.holderFragmentFor(fragment);
    }

    // 全局单例
    @SuppressWarnings("WeakerAccess")
    static class HolderFragmentManager {
        private Map<Activity, HolderFragment> mNotCommittedActivityHolders = new HashMap<>();
        private Map<Fragment, HolderFragment> mNotCommittedFragmentHolders = new HashMap<>();

        private ActivityLifecycleCallbacks mActivityCallbacks =
                new EmptyActivityLifecycleCallbacks() {
                    @Override
                    public void onActivityDestroyed(Activity activity) {
                        HolderFragment fragment = mNotCommittedActivityHolders.remove(activity);
                        if (fragment != null) {
                            Log.e(LOG_TAG, "Failed to save a ViewModel for " + activity);
                        }
                    }
                };

        private boolean mActivityCallbacksIsAdded = false;

        private FragmentLifecycleCallbacks mParentDestroyedCallback =
                new FragmentLifecycleCallbacks() {
                    @Override
                    public void onFragmentDestroyed(FragmentManager fm, Fragment parentFragment) {
                        super.onFragmentDestroyed(fm, parentFragment);
                        HolderFragment fragment = mNotCommittedFragmentHolders.remove(
                                parentFragment);
                        if (fragment != null) {
                            Log.e(LOG_TAG, "Failed to save a ViewModel for " + parentFragment);
                        }
                    }
                };

        void holderFragmentCreated(Fragment holderFragment) {
            Fragment parentFragment = holderFragment.getParentFragment();
            if (parentFragment != null) {
                mNotCommittedFragmentHolders.remove(parentFragment);
                parentFragment.getFragmentManager().unregisterFragmentLifecycleCallbacks(
                        mParentDestroyedCallback);
            } else {
                mNotCommittedActivityHolders.remove(holderFragment.getActivity());
            }
        }

        private static HolderFragment findHolderFragment(FragmentManager manager) {
            if (manager.isDestroyed()) {
                throw new IllegalStateException("Can't access ViewModels from onDestroy");
            }

            Fragment fragmentByTag = manager.findFragmentByTag(HOLDER_TAG);
            if (fragmentByTag != null && !(fragmentByTag instanceof HolderFragment)) {
                throw new IllegalStateException("Unexpected "
                        + "fragment instance was returned by HOLDER_TAG");
            }
            return (HolderFragment) fragmentByTag;
        }

        private static HolderFragment createHolderFragment(FragmentManager fragmentManager) {
            HolderFragment holder = new HolderFragment();
            fragmentManager.beginTransaction().add(holder, HOLDER_TAG).commitAllowingStateLoss();
            return holder;
        }

        HolderFragment holderFragmentFor(FragmentActivity activity) {
            // 获取 Activity 的 FragmentManager
            FragmentManager fm = activity.getSupportFragmentManager();
            // 通过 HOLDER_TAG 在 FragmentManager 中需要 HolderFragment
            HolderFragment holder = findHolderFragment(fm);
            // 获得的 HolderFragment 不为空就返回
            if (holder != null) {
                return holder;
            }

            // 在 Map<Activity, HolderFragment> 缓存中获取 HolderFragment
            // Activity 为 key，所以每一个 Activity 或者 Fragment 只会有一个 HolderFragment
            holder = mNotCommittedActivityHolders.get(activity);
            // 不为空就返回
            if (holder != null) {
                return holder;
            }

            // 在 Application 中注册一个所有 Activity 生命周期回调监听，这里只会注册一次
            // 这里注册 Activity 生命周期监听的目的是在 Activity 销毁的时候好移除 Map<Activity, HolderFragment> 中的对应数据
            if (!mActivityCallbacksIsAdded) {
                mActivityCallbacksIsAdded = true;
                activity.getApplication().registerActivityLifecycleCallbacks(mActivityCallbacks);
            }

            // new HolderFragment() 并通过fm添加到 Activity 并返回
            holder = createHolderFragment(fm);
            // 添加到 Map<Activity, HolderFragment> 缓存
            mNotCommittedActivityHolders.put(activity, holder);
            // 返回
            return holder;
        }

        HolderFragment holderFragmentFor(Fragment parentFragment) {
            FragmentManager fm = parentFragment.getChildFragmentManager();
            HolderFragment holder = findHolderFragment(fm);
            if (holder != null) {
                return holder;
            }
            holder = mNotCommittedFragmentHolders.get(parentFragment);
            if (holder != null) {
                return holder;
            }

            parentFragment.getFragmentManager().registerFragmentLifecycleCallbacks(mParentDestroyedCallback, false);
            holder = createHolderFragment(fm);
            mNotCommittedFragmentHolders.put(parentFragment, holder);
            return holder;
        }
    }
}
