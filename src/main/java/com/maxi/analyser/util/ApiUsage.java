package com.maxi.analyser.util;


    public class ApiUsage {
        private String apiPath;
        private String componentName;

        public ApiUsage(String apiPath, String componentName) {
            this.apiPath = apiPath;
            this.componentName = componentName;
        }

        public String getApiPath() {
            return apiPath;
        }

        public String getComponentName() {
            return componentName;
        }
    }


