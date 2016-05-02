package org.jfrog.hudson.pipeline;

import org.kohsuke.stapler.DataBoundConstructor;

import java.io.Serializable;

/**
 * Created by romang on 4/21/16.
 */
public class ArtifactoryPipelineServer implements Serializable {
    private String serverName;
    private String url;
    private String username;
    private String password;
    private boolean bypassProxy;

    public ArtifactoryPipelineServer(String artifactoryServerName, String url, String username, String password) {
        serverName = artifactoryServerName;
        this.url = url;
        this.username = username;
        this.password = password;
    }

    public ArtifactoryPipelineServer(String url, String username, String password) {
        this.url = url;
        this.username = username;
        this.password = password;
    }

    public String getServerName() {
        return serverName;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setBypassProxy(boolean bypassProxy) {
        this.bypassProxy = bypassProxy;
    }

    public boolean isBypassProxy() {
        return bypassProxy;
    }
}
