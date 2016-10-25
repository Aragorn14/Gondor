package com.scube.hoverboard.src.main.java.com.hoverboard.util;

import android.content.Context;
import android.graphics.Typeface;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.Hashtable;

/**
 * Created by jschroeder on 11/8/13.
 */
public class FontUtil
{
    public enum IGNFont
    {
        ARS_MAQUETTE,
        ARS_MAQUETTE_BOLD,
        DIN_ROUND_PRO,
        DIN_ROUND_PRO_MEDIUM,
        DIN_ROUND_PRO_BOLD
    }

    private static Context sContext;
    public static final String ARS_MAQUETTE = "ARSMaquette-Regular.otf";
    public static final String ARS_MAQUETTE_BOLD = "ARSMaquette-Bold.otf";
    public static final String DIN_ROUND_PRO = "DINRoundCompPro.ttf";
    public static final String DIN_ROUND_PRO_MEDIUM = "DINRoundCompPro-Medium.ttf";
    public static final String DIN_ROUND_PRO_BOLD = "DINRoundCompPro-Bold.ttf";

    public static Context getContext()
    {
        return sContext;
    }

    public static void setContext(Context context)
    {
        sContext = context;
    }

    private static final Hashtable<String, Typeface> mCache = new Hashtable<String, Typeface>();

    public static Typeface loadFont(String fontName)
    {
        // Make sure we load each font only once to not run out of memory
        synchronized (mCache) {
            if (!mCache.containsKey(fontName)) {
                // TODO: Load typeface
                if (sContext != null) {
                    Typeface typeface = Typeface.createFromAsset(sContext.getAssets(), fontName);
                    if (typeface != null) {
                        mCache.put(fontName, typeface);
                    }
                }
            }

            return mCache.get(fontName);
        }
    }

    public static Typeface getARSMaquetteFont()
    {
        return loadFont(ARS_MAQUETTE);
    }

    public static Typeface getARSMaquetteBold()
    {
        return loadFont(ARS_MAQUETTE_BOLD);
    }

    public static Typeface getDinRoundPro()
    {
        return loadFont(DIN_ROUND_PRO);
    }

    public static Typeface getDinRoundProBold()
    {
        return loadFont(DIN_ROUND_PRO_BOLD);
    }

    public static Typeface getDinRoundProMedium()
    {
        return loadFont(DIN_ROUND_PRO_MEDIUM);
    }

    public static void setFontForIDFromView(ViewGroup parentView, int id, IGNFont font)
    {
        TextView textView = (TextView) parentView.findViewById(id);
        textView.setTypeface(fontForIGNFont(font));
    }

    public static Typeface fontForIGNFont(IGNFont font)
    {
        Typeface typeface = null;
        if (font == IGNFont.ARS_MAQUETTE) {
            typeface = getARSMaquetteFont();
        } else if (font == IGNFont.ARS_MAQUETTE_BOLD) {
            typeface = getARSMaquetteBold();
        } else if (font == IGNFont.DIN_ROUND_PRO) {
            typeface = getDinRoundPro();
        } else if (font == IGNFont.DIN_ROUND_PRO_MEDIUM) {
            typeface = getDinRoundProMedium();
        } else if (font == IGNFont.DIN_ROUND_PRO_BOLD) {
            typeface = getDinRoundProBold();
        }

        return typeface;
    }

    public static Typeface arsMaquette()
    {
        return fontForIGNFont(IGNFont.ARS_MAQUETTE);
    }

    public static Typeface arsMaquetteBold()
    {
        return fontForIGNFont(IGNFont.ARS_MAQUETTE_BOLD);
    }

    public static Typeface dinRoundPro()
    {
        return fontForIGNFont(IGNFont.DIN_ROUND_PRO);
    }

    public static Typeface dinRoundProMedium()
    {
        return fontForIGNFont(IGNFont.DIN_ROUND_PRO_MEDIUM);
    }

    public static Typeface dinRoundProBold()
    {
        return fontForIGNFont(IGNFont.DIN_ROUND_PRO_BOLD);
    }
}
