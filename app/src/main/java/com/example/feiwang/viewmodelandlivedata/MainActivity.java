package com.example.feiwang.viewmodelandlivedata;

import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import com.example.feiwang.viewmodelandlivedata.viewmodel.MainViewModel;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ViewModelProvider.AndroidViewModelFactory factory = ViewModelProvider.AndroidViewModelFactory.getInstance(this.getApplication());

        // Activity 中创建 ViewModel
//        MainViewModel mainViewModel = ViewModelProviders.of(this).get(MainViewModel.class);

        // fragment 中创建 ViewModel
//        MainViewModel fragmentViewModel = ViewModelProviders.of(fragment).get(MainViewModel.class);

        /**
         * 在任何地方创建使用 ViewModel：
         * 将 context 强转成 FragmentActivity 就行
         */
//        MainViewModel mMainViewModel = ViewModelProviders.of((FragmentActivity) context).get(MainViewModel.class);

        // 第一步:根据 Activity 或者 Fragment 获得 ViewModelProvider
        ViewModelProvider viewModelProvider = ViewModelProviders.of(MainActivity.this);

        // 第二步:使用 ViewModelProvider 反射创建需要的 ViewModel
        MainViewModel mainViewModel = viewModelProvider.get(MainViewModel.class);

    }
}
