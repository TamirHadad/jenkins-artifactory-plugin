package org.jfrog.hudson.pipeline;

import org.apache.commons.cli.MissingArgumentException;
import org.apache.commons.lang.StringUtils;
import org.jfrog.hudson.ArtifactoryServer;
import org.jfrog.hudson.pipeline.buildinfo.PipelineBuildInfo;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by romang on 4/26/16.
 */
public class GenericDownloadUpload {

    private final ArtifactoryServer server;
    private final String json;
    private PipelineBuildInfo buildinfo;

    public GenericDownloadUpload(ArtifactoryPipelineServer artifactoryServer, String artifactoryServerID, String json, PipelineBuildInfo buildinfo) throws MissingArgumentException {
        this.server = PipelineUtils.prepareArtifactoryServer(artifactoryServerID, artifactoryServer);
        this.json = json;
        this.buildinfo = buildinfo;
    }

    public ArtifactoryServer getServer() {
        return server;
    }

    public String getJson() {
        return json;
    }

    public PipelineBuildInfo getBuildinfo() {
        return buildinfo;
    }

    public static Map<String, Object> getDefineArguments(String artifactoryServerID, String json) {
        Map<String, Object> args = new HashMap<String, Object>();
        if (StringUtils.isEmpty(artifactoryServerID)) {
            args.put("artifactoryServer", "<provide Artifactory server instance>");
        } else {
            args.put("artifactoryServerID", artifactoryServerID);
        }

        args.put("buildinfo", "<optional: provide Artifactory buildinfo instance here>");
        args.put("json", json);

        return args;
    }
}