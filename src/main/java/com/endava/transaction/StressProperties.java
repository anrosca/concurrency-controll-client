package com.endava.transaction;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

@ConfigurationProperties("stress")
public class StressProperties {
    private int corePoolSize;
    private int maxPoolSize;
    private String threadNamePrefix;
    private List<String> endpointUrls;

    public StressProperties() {
    }

    public int getCorePoolSize() {
        return corePoolSize;
    }

    public void setCorePoolSize(int corePoolSize) {
        this.corePoolSize = corePoolSize;
    }

    public int getMaxPoolSize() {
        return maxPoolSize;
    }

    public void setMaxPoolSize(int maxPoolSize) {
        this.maxPoolSize = maxPoolSize;
    }

    public String getThreadNamePrefix() {
        return threadNamePrefix;
    }

    public void setThreadNamePrefix(String threadNamePrefix) {
        this.threadNamePrefix = threadNamePrefix;
    }

    public List<String> getEndpointUrls() {
        return endpointUrls;
    }

    public void setEndpointUrls(List<String> endpointUrls) {
        this.endpointUrls = endpointUrls;
    }
}
