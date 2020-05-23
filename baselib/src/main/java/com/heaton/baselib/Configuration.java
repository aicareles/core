package com.heaton.baselib;

import com.heaton.baselib.api.ApiConfig;
import com.heaton.baselib.app.language.Language;

//配置类
public class Configuration {

    public boolean loggable;
    public Language language;
    public ApiConfig apiConfig;

    public static Configuration defalut(){
        Builder builder = new Builder();
        builder.language = new Language(Language.MODE.AUTO);
        return builder.build();
    }

    private Configuration(Builder builder) {
        loggable = builder.loggable;
        language = builder.language;
        apiConfig = builder.apiConfig;
    }

    public static final class Builder {
        private boolean loggable;
        private Language language;
        private ApiConfig apiConfig;

        public Builder() {
        }

        public Builder loggable(boolean val) {
            loggable = val;
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
