package com.bytedesk.ui.util;

//import java.lang.reflect.Field;

import android.content.Context;
import android.content.res.Resources;

import com.orhanobut.logger.Logger;

public class KFResUtil {

    private static KFResUtil instance;
    private Context context;
    private Resources mResource;
    private String mStrPkgName;

    private KFResUtil(Context var1) {
        this.context = var1.getApplicationContext();
        this.mResource = var1.getResources();
        this.mStrPkgName = var1.getPackageName();
    }

    public static KFResUtil getResofR(Context var0) {
        if(instance == null) {
            instance = new KFResUtil(var0);
        }
        return instance;
    }

    public int getAnim(String var1) {
        return this.getResofR("anim", var1);
    }

    public int getId(String var1) {
        return this.getResofR("id", var1);
    }

    public int getDimen(String var1) {
        return this.getResofR("dimen", var1);
    }

    public int getDrawable(String var1) {
        return this.getResofR("drawable", var1);
    }

    public int getLayout(String var1) {
        return this.getResofR("layout", var1);
    }

    public int getStyle(String var1) {
        return this.getResofR("style", var1);
    }

    public int getStyleable(String var1) {
        return this.getResofR("styleable", var1);
    }

    public int getString(String var1) {
        return this.getResofR("string", var1);
    }

    public int getArray(String var1) {
        return this.getResofR("array", var1);
    }

    public int getRaw(String var1) {
        return this.getResofR("raw", var1);
    }

    public int getColor(String var1) {
        return this.getResofR("color", var1);
    }

    private int getResofR(String type, String resName) {
        int res = mResource.getIdentifier(resName, type, mStrPkgName);
        if(res <= 0) {
            Logger.d("resource " + type + "." + resName + " is not defined!");
        }
        return res;
    }
}












