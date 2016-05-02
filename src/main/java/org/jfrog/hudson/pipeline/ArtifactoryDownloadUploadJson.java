package org.jfrog.hudson.pipeline;

/**
 * Created by romang on 4/20/16.
 */
public class ArtifactoryDownloadUploadJson {

    private ArtifactoryFileJson[] files;

    public ArtifactoryFileJson[] getFiles() {
        return files;
    }

    public void setFiles(ArtifactoryFileJson[] files) {
        this.files = files;
    }
}
