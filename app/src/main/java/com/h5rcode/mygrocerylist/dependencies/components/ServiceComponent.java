package com.h5rcode.mygrocerylist.dependencies.components;

import com.h5rcode.mygrocerylist.dependencies.modules.ServiceModule;
import com.h5rcode.mygrocerylist.fragments.MainFragment;

import javax.inject.Singleton;

import dagger.Component;

@Component(modules = ServiceModule.class)
@Singleton
public interface ServiceComponent {
    void inject(MainFragment mainFragment);
}
