package app.shouoff.app;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Build;

import java.lang.reflect.Field;

public final class FontsOverride
{
    public static void setDefaultFont(Context context, String staticTypefaceFieldName, String fontAssetName) {
        final Typeface regular = Typeface.createFromAsset(context.getAssets(), fontAssetName);
        replaceFont(staticTypefaceFieldName, regular);
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    private static void replaceFont(String staticTypefaceFieldName, final Typeface newTypeface) {
        try {
            final Field staticField = Typeface.class.getDeclaredField(staticTypefaceFieldName);
            staticField.setAccessible(true);
            staticField.set(null, newTypeface);
        } catch (NoSuchFieldException | IllegalAccessException e)
        {
            e.printStackTrace();
        }
    }
}
