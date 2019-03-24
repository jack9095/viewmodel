package android.arch.lifecycle;

import android.app.Application;
import android.support.annotation.MainThread;
import android.support.annotation.NonNull;

import java.lang.reflect.InvocationTargetException;

/**
 * ViewModelProvider 是每次获取创建 ViewModel 的时候都会创建一个新的
 */
@SuppressWarnings("WeakerAccess")
public class ViewModelProvider {

    private static final String DEFAULT_KEY = "android.arch.lifecycle.ViewModelProvider.DefaultKey";

    /**
     * Factory 接口的实现负责实例化 ViewModels。
     */
    public interface Factory {
        /**
         * 创建给定 @code Class 类的新实例
         *
         * @param modelClass 新实例的 class
         * @param <T>        ViewModel 类型的泛型参数.
         * @return 返回新创建的 ViewModel
         */
        @NonNull
        <T extends ViewModel> T create(@NonNull Class<T> modelClass);
    }

    private final Factory mFactory;
    private final ViewModelStore mViewModelStore;

    /**
     * @param owner   ViewModelStore 用于保存 ViewModels
     * @param factory Factory 用于创建新的 ViewModels
     */
    public ViewModelProvider(@NonNull ViewModelStoreOwner owner, @NonNull Factory factory) {
        this(owner.getViewModelStore(), factory);
    }

    public ViewModelProvider(@NonNull ViewModelStore store, @NonNull Factory factory) {
        mFactory = factory;
        this.mViewModelStore = store;
    }

    /**
     * 创建一个ViewModelProvider，使用 ViewModelProvider 内部的全局单例 AndroidViewModelFactory 来反射创建 ViewModel,并把创建的ViewModel存入传入的ViewModelStore中！
     * @param modelClass ViewModel 的子类的 class
     */
    @NonNull
    @MainThread
    public <T extends ViewModel> T get(@NonNull Class<T> modelClass) {
        String canonicalName = modelClass.getCanonicalName();
        if (canonicalName == null) {
            throw new IllegalArgumentException("Local and anonymous classes can not be ViewModels");
        }
        // ViewModelStore 存储 ViewModel key 的获取：  DEFAULT_KEY 和 类名组成一个key值
        return get(DEFAULT_KEY + ":" + canonicalName, modelClass);
    }


    @NonNull
    @MainThread
    public <T extends ViewModel> T get(@NonNull String key, @NonNull Class<T> modelClass) {
        ViewModel viewModel = mViewModelStore.get(key); // 先从缓存中获取

        if (modelClass.isInstance(viewModel)) { // 缓存有就直接返回，否则就用 Factory 从新创建
            //noinspection unchecked
            return (T) viewModel;
        } else {
            //noinspection StatementWithEmptyBody
            if (viewModel != null) {
                // TODO: log a warning.
            }
        }

        viewModel = mFactory.create(modelClass); // 创建对应的 viewModel
        mViewModelStore.put(key, viewModel); // 把创建好的 viewModel 存储到 ViewModelStore 的 HashMap 的 put 方法中
        //noinspection unchecked
        return (T) viewModel;
    }

    /**
     * Simple factory, which calls empty constructor on the give class.
     * 简单工厂，它在给定类上调用空构造函数
     */
    public static class NewInstanceFactory implements Factory {

        @SuppressWarnings("ClassNewInstance")
        @NonNull
        @Override
        public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
            // noinspection TryWithIdenticalCatches
            try {
                return modelClass.newInstance();
            } catch (InstantiationException e) {
                throw new RuntimeException("Cannot create an instance of " + modelClass, e);
            } catch (IllegalAccessException e) {
                throw new RuntimeException("Cannot create an instance of " + modelClass, e);
            }
        }
    }

    /**
     * AndroidViewModelFactory 在正常情况下是全局单例只有一个，只是一个反射创建对象的工具类
     */
    public static class AndroidViewModelFactory extends ViewModelProvider.NewInstanceFactory {

        // 单例实现
        private static AndroidViewModelFactory sInstance;

        // 获得 AndroidViewModelFactory 单例
        @NonNull
        public static AndroidViewModelFactory getInstance(@NonNull Application application) {
            if (sInstance == null) {
                sInstance = new AndroidViewModelFactory(application);
            }
            return sInstance;
        }

        private Application mApplication;

        // 构造方式是 public 的，是可以 new 一个 AndroidViewModelFactory 出来的
        public AndroidViewModelFactory(@NonNull Application application) {
            mApplication = application;
        }

        @NonNull
        @Override
        public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
            if (AndroidViewModel.class.isAssignableFrom(modelClass)) {
                // noinspection TryWithIdenticalCatches  不带识别标记的检查尝试
                try {
                    // 创建ViewModel的关键地方，根据给出的Class反射创建需要的ViewModel
                    return modelClass.getConstructor(Application.class).newInstance(mApplication);
                } catch (NoSuchMethodException e) {
                    throw new RuntimeException("Cannot create an instance of " + modelClass, e);
                } catch (IllegalAccessException e) {
                    throw new RuntimeException("Cannot create an instance of " + modelClass, e);
                } catch (InstantiationException e) {
                    throw new RuntimeException("Cannot create an instance of " + modelClass, e);
                } catch (InvocationTargetException e) {
                    throw new RuntimeException("Cannot create an instance of " + modelClass, e);
                }
            }
            return super.create(modelClass);
        }
    }
}
