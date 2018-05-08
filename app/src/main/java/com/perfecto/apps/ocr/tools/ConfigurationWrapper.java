package com.perfecto.apps.ocr.tools;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.support.annotation.NonNull;

import java.util.Locale;

/**
 * Created by hosam azzam on 04/05/2017.
 */

public class ConfigurationWrapper {
    private ConfigurationWrapper() {
    }

    //Creates a Context with updated Configuration.
    public static Context wrapConfiguration(@NonNull final Context context,
                                            @NonNull final Configuration config) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            return context.createConfigurationContext(config);
        }
        return context;
    }

    // Creates a Context with updated Locale.
    public static Context wrapLocale(@NonNull final Context context,
                                     @NonNull final Locale locale) {
        final Resources res = context.getResources();
        final Configuration config = res.getConfiguration();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            config.setLocale(locale);
        }
        return wrapConfiguration(context, config);
    }
}
