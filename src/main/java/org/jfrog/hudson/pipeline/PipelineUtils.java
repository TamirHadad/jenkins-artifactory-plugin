package org.jfrog.hudson.pipeline;

import org.acegisecurity.acls.NotFoundException;
import org.apache.commons.cli.MissingArgumentException;
import org.jenkinsci.plugins.workflow.steps.StepContext;
import org.jfrog.hudson.ArtifactoryServer;
import org.jfrog.hudson.CredentialsConfig;
import org.jfrog.hudson.util.RepositoriesUtils;

/**
 * Created by romang on 4/24/16.
 */
public class PipelineUtils {

    /**
     * Prepares Artifactory server either from serverID or from ArtifactoryPipelineServer.
     * @param context Step context for fail conditions
     * @param step The step
     * @return ArtifactoryServer
     */
    public static ArtifactoryServer prepareArtifactoryServer(StepContext context, GenericDownloadUpload step) {
        String artifactoryServerID = step.getArtifactoryServerID();
        ArtifactoryPipelineServer pipelineServer = step.getArtifactoryServer();
        if (artifactoryServerID == null && pipelineServer == null) {
            context.onFailure(new MissingArgumentException("Artifactory server ID or Artifactory server are mandatory"));
        }

        if (artifactoryServerID != null && pipelineServer != null) {
            context.onFailure(new IllegalArgumentException("Both Artifactory server ID and Artifactory server cannot be declared together"));
        }

        if (pipelineServer != null) {
            CredentialsConfig credentials = new CredentialsConfig(pipelineServer.getUsername(),
                    pipelineServer.getPassword(), null, null);

            return new ArtifactoryServer(null, pipelineServer.getUrl(), credentials,
                    credentials, 0, pipelineServer.isBypassProxy());
        }

        ArtifactoryServer server = RepositoriesUtils.getArtifactoryServer(step.getArtifactoryServerID(), RepositoriesUtils.getArtifactoryServers());
        if (server == null) {
            context.onFailure(new NotFoundException("Couldn't find Artifactory named: " + artifactoryServerID));
        }
        return server;
    }
}
