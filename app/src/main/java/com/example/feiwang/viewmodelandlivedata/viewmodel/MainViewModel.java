package com.example.feiwang.viewmodelandlivedata.viewmodel;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

/**
 * Created by fei.wang on 2019/3/24.
 */
public class MainViewModel extends ViewModel {
    MutableLiveData<String> mainLiveData = new MutableLiveData<>();

}
