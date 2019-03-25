package com.example.feiwang.viewmodelandlivedata.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.support.annotation.NonNull;

/**
 * Created by fei.wang on 2019/3/24.
 */
public class MainViewModel extends AndroidViewModel {
    private final MutableLiveData<String> mainLiveData = new MutableLiveData<>();

    public MainViewModel(@NonNull Application application) {
        super(application);
    }

    public LiveData<String> getLiveData() {
        return mainLiveData;
    }
}
