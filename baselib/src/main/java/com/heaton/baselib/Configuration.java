package com.heaton.baselib;

import com.heaton.baselib.api.ApiConfig;
import com.heaton.baselib.app.language.Language;

//配置类
public class Configuration {

    public boolean loggable;
    public String logTag;
    public Language language;
    public ApiConfig apiConfig;

    public static Configuration defalut(){
        return new Builder().build();
    }

    private Configuration(Builder builder) {
        loggable = builder.loggable;
        logTag = builder.logTag;
        language = builder.language;
        apiConfig = builder.apiConfig;
    }

    public static final class Builder {
        private boolean loggable = true;
        private String logTag = "Heaton_LOGGER";
        private Language language = new Language(Language.MODE.AUTO);
        private ApiConfig apiConfig;

        public Builder() {
        }

        public Builder loggable(boolean val) {
            loggable = val;
            return this;
        }

        public Builder logTag(String logTag) {
            this.logTag = logTag;
            return this;
        }

        public Builder language(Language val) {
            language = val;
            return this;
        }

        public Builder apiConfig(ApiConfig val){
            apiConfig = val;
            return this;
        }

        public Configuration build() {
            return new Configuration(this);
        }
    }
}
