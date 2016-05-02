package org.jfrog.hudson.pipeline;

import hudson.Util;
import hudson.model.Run;
import hudson.tools.ToolDescriptor;
import hudson.tools.ToolInstallation;
import hudson.util.ListBoxModel;
import org.apache.commons.cli.MissingArgumentException;
import org.jfrog.hudson.ArtifactoryServer;
import org.jfrog.hudson.CredentialsConfig;
import org.jfrog.hudson.pipeline.buildinfo.PipelineBuildinfo;
import org.jfrog.hudson.util.RepositoriesUtils;
import org.kohsuke.stapler.QueryParameter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by romang on 4/24/16.
 */
public class PipelineUtils {

    private static int buildNumber = 0;
    private static int subBuildNumber = 0;
    private static Map<String, PipelineBuildinfo> jobBuildInfo = new HashMap<String, PipelineBuildinfo>();

    /**
     * Prepares Artifactory server either from serverID or from ArtifactoryPipelineServer.
     *
     * @param artifactoryServerID
     * @param pipelineServer
     * @return
     */
    public static ArtifactoryServer prepareArtifactoryServer(String artifactoryServerID,
                                                             ArtifactoryPipelineServer pipelineServer) throws MissingArgumentException {

        if (artifactoryServerID == null && pipelineServer == null) {
            return null;
//            throw new MissingArgumentException("Artifactory server ID or Artifactory server are mandatory");
        }

        if (artifactoryServerID != null && pipelineServer != null) {
            return null;
//            throw new IllegalArgumentException("Both Artifactory server ID and Artifactory server cannot be declared together");
        }

        if (pipelineServer != null) {
            CredentialsConfig credentials = new CredentialsConfig(pipelineServer.getUsername(),
                    pipelineServer.getPassword(), null, null);

            return new ArtifactoryServer(null, pipelineServer.getUrl(), credentials,
                    credentials, 0, pipelineServer.isBypassProxy());
        }

        ArtifactoryServer server = RepositoriesUtils.getArtifactoryServer(artifactoryServerID, RepositoriesUtils.getArtifactoryServers());
        if (server == null) {
            return null;
//            throw new NotFoundException("Couldn't find Artifactory named: " + artifactoryServerID);
        }
        return server;
    }


    public static ListBoxModel getServerListBox() {
        ListBoxModel r = new ListBoxModel();
        List<ArtifactoryServer> servers = RepositoriesUtils.getArtifactoryServers();
        r.add("", "");
        for (ArtifactoryServer server : servers) {
            r.add(server.getName() + ":" + server.getUrl(), server.getName());
        }
        return r;
    }

    public ListBoxModel doFillNameItems(@QueryParameter String type) {
        type = Util.fixEmpty(type);
        ListBoxModel r = new ListBoxModel();
        for (ToolDescriptor<?> desc : ToolInstallation.all()) {
            if (type != null && !desc.getId().equals(type)) {
                continue;
            }
            for (ToolInstallation tool : desc.getInstallations()) {
                r.add(tool.getName());
            }
        }
        return r;
    }

    public static String getBuildName(String buildNumberStr) {
        int buildNumber = Integer.parseInt(buildNumberStr);
        if (buildNumber != PipelineUtils.buildNumber) {
            PipelineUtils.buildNumber = buildNumber;
            subBuildNumber = 0;
        }
        return PipelineUtils.buildNumber + ":" + String.valueOf(subBuildNumber++);
    }

    public static PipelineBuildinfo getRunBuildInfo(Run run) {
        if (!jobBuildInfo.containsKey(run.getId())) {
            jobBuildInfo.put(run.getId(), new PipelineBuildinfo());
        }

        return jobBuildInfo.get(run.getId());
    }

    public static void removePipelineBuildInfo(Run run) {
        jobBuildInfo.remove(run.getId());
    }

}
