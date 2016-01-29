package com.getiton.android.app.core.data.db;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

/**
 * Created by christophesmet on 11/10/15.
 */

@Table(name = "namedLanModule")
public class NamedLanModule extends Model {

    public static final String KEY_COLUMN_NAME = "name";
    public static final String KEY_COLUMN_MAC_ID = "macId";


    @Column(name = "name")
    private String mName;
    @Column(name = "macId")
    private String mMacId;

    public NamedLanModule() {
    }

    public NamedLanModule(String name, String macId) {
        mName = name;
        mMacId = macId;
    }

    public void setName(String name) {
        mName = name;
    }

    public void setMacId(String macId) {
        mMacId = macId;
    }

    public String getName() {
        return mName;
    }

    public String getMacId() {
        return mMacId;
    }
}