package org.jfrog.hudson.pipeline;

/**
 * Created by romang on 4/24/16.
 */
public interface GenericDownloadUpload {

    ArtifactoryPipelineServer getArtifactoryServer();

    String getArtifactoryServerID();

    String getJson();
}
