package com.heaton.baselib.app.language;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class Language {
    //默认支持的语言集合
    private static final List<Locale> DEFALUT_LOCALES = Arrays.asList(Locale.CHINA, Locale.ENGLISH);
    //自定义模式下,默认语言
    private static final Locale DEFALUT_LOCALE = Locale.ENGLISH;
    private MODE mode;
    //是否开启多语言(如果设置为false则代表用户自定义自己的多语言)
    private boolean enable = true;
    private Locale defalutLocale;
    private List<Locale> locales;

    public Language(MODE mode, Locale defalutLocale, List<Locale> locales) {
        this.mode = mode;
        if (defalutLocale == null){
            defalutLocale = DEFALUT_LOCALE;
        }
        if (locales == null){
            locales = DEFALUT_LOCALES;
        }
        this.defalutLocale = defalutLocale;
        this.locales = locales;
    }

    public Language(MODE mode, Locale defalutLocale) {
        this(mode, defalutLocale,null);
    }

    public Language(MODE mode) {
        this(mode, null,null);
    }

    public List<Locale> getLocales() {
        return locales;
    }

    public void setLocales(List<Locale> locales) {
        this.locales = locales;
    }

    public MODE getMode() {
        return mode;
    }

    public void setMode(MODE mode) {
        this.mode = mode;
    }

    public Locale getDefalutLocale() {
        return defalutLocale;
    }

    public void setDefalutLocale(Locale defalutLocale) {
        this.defalutLocale = defalutLocale;
    }

    public boolean isEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    public enum MODE{
        AUTO,
        CUSTOM
    }
}
