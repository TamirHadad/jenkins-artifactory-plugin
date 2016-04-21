package org.jfrog.hudson.pipeline;

/**
 * Created by romang on 4/20/16.
 */
public class ArtifactoryDownloadJson {
    private String url;
    private String username;
    private String password;
    private boolean bypassProxy;
    private ArtifactoryJsonFile[] files;

    public String getUrl() {
        return url;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public ArtifactoryJsonFile[] getFiles() {
        return files;
    }

    public boolean getBypassProxy() {
        return bypassProxy;
    }
}
