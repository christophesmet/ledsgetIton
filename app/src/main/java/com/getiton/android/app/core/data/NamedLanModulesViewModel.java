package com.getiton.android.app.core.data;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.getiton.android.app.core.data.db.NamedLanModule;
import com.getiton.android.app.core.data.db.NamedLanModuleRepo;

/**
 * Created by christophesmet on 11/10/15.
 */

public class NamedLanModulesViewModel {

    private NamedLanModuleRepo mRepo;

    public NamedLanModulesViewModel() {
        mRepo = new NamedLanModuleRepo();
    }

    @NonNull
    public NamedLanModule addNewModule(@NonNull String name, @NonNull String macId) {
        NamedLanModule module = new NamedLanModule(name, macId);
        mRepo.saveModule(module);
        return module;
    }

    @Nullable
    public NamedLanModule findModuleForId(@NonNull String macId) {
        return mRepo.findModule(macId);
    }

    @Nullable
    public String getNameForModuleId(@NonNull String macId) {
        NamedLanModule module = findModuleForId(macId);
        if (module != null) {
            return module.getName();
        }
        return null;
    }
}