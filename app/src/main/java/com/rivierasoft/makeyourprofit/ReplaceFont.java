package com.rivierasoft.makeyourprofit;

import android.content.Context;
import android.graphics.Typeface;

import java.lang.reflect.Field;

public class ReplaceFont {
    public static void replaceDefaultFont(Context context,
                                          String nameOfFontBeingReplaced,
                                          String nameOfFontInAsset) {
        Typeface customFonttypeface = Typeface.createFromAsset( context.getAssets(),nameOfFontInAsset);
        replauceDfont(nameOfFontBeingReplaced,customFonttypeface);
    }

    private static void replauceDfont(String nameOfFontBeingReplaced, Typeface customFontTypeface) {
        try{
            Field myfield = Typeface.class.getDeclaredField(nameOfFontBeingReplaced);
            myfield.setAccessible(true);
            myfield.set(null,customFontTypeface);
        }catch (NoSuchFieldException e){
            e.printStackTrace();
        }catch (IllegalAccessException e){
            e.printStackTrace();
        }
    }
}
