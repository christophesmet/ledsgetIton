package com.getiton.android.app.core.data.db;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.activeandroid.query.Select;

/**
 * Created by christophesmet on 11/10/15.
 */

public class NamedLanModuleRepo {

    @Nullable
    public NamedLanModule findModule(@NonNull String mac) {
        return new Select().from(NamedLanModule.class).where(NamedLanModule.KEY_COLUMN_MAC_ID + " = ?", mac).executeSingle();
    }

    public void saveModule(@NonNull NamedLanModule model) {
        NamedLanModule savedModule = findModule(model.getMacId());
        if (savedModule == null) {
            savedModule = new NamedLanModule(model.getName(), model.getMacId());
            savedModule.save();
        } else {
            savedModule.setName(model.getName());
            savedModule.setMacId(model.getMacId());
            savedModule.save();
        }
    }
}