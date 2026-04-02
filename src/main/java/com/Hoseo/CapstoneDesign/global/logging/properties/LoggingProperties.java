package com.Hoseo.CapstoneDesign.global.logging.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.ArrayList;
import java.util.List;

@ConfigurationProperties(prefix = "app.logging")
public class LoggingProperties {

    private String service = "capstone-main";
    private String serverType = "spring";
    private String directory = "./.logs";
    private List<String> excludePaths = new ArrayList<>();
    private DebugAspect debugAspect = new DebugAspect();

    public String getService() {
        return service;
    }

    public void setService(String service) {
        this.service = service;
    }

    public String getServerType() {
        return serverType;
    }

    public void setServerType(String serverType) {
        this.serverType = serverType;
    }

    public String getDirectory() {
        return directory;
    }

    public void setDirectory(String directory) {
        this.directory = directory;
    }

    public List<String> getExcludePaths() {
        return excludePaths;
    }

    public void setExcludePaths(List<String> excludePaths) {
        this.excludePaths = excludePaths;
    }

    public DebugAspect getDebugAspect() {
        return debugAspect;
    }

    public void setDebugAspect(DebugAspect debugAspect) {
        this.debugAspect = debugAspect;
    }

    public static class DebugAspect {

        private boolean enabled = false;
        private int maxStringLength = 120;
        private int maxCollectionSize = 10;
        private boolean logReturnValue = true;

        public boolean isEnabled() {
            return enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }

        public int getMaxStringLength() {
            return maxStringLength;
        }

        public void setMaxStringLength(int maxStringLength) {
            this.maxStringLength = maxStringLength;
        }

        public int getMaxCollectionSize() {
            return maxCollectionSize;
        }

        public void setMaxCollectionSize(int maxCollectionSize) {
            this.maxCollectionSize = maxCollectionSize;
        }

        public boolean isLogReturnValue() {
            return logReturnValue;
        }

        public void setLogReturnValue(boolean logReturnValue) {
            this.logReturnValue = logReturnValue;
        }
    }
}

