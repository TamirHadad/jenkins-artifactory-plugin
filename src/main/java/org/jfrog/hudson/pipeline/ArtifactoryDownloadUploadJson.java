package org.jfrog.hudson.pipeline;

/**
 * Created by romang on 4/20/16.
 */
public class ArtifactoryDownloadUploadJson {

    private ArtifactoryJsonFile[] files;

    public ArtifactoryJsonFile[] getFiles() {
        return files;
    }

    public void setFiles(ArtifactoryJsonFile[] files) {
        this.files = files;
    }
}
