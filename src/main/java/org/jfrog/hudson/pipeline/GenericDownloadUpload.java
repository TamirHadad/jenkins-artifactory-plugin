package org.jfrog.hudson.pipeline;

import org.apache.commons.cli.MissingArgumentException;
import org.apache.commons.lang.StringUtils;
import org.jfrog.hudson.ArtifactoryServer;
import org.jfrog.hudson.pipeline.buildinfo.PipelineBuildinfo;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by romang on 4/26/16.
 */
public class GenericDownloadUpload {

    private final ArtifactoryServer server;
    private final String json;
    private PipelineBuildinfo buildinfo;

    public GenericDownloadUpload(ArtifactoryPipelineServer artifactoryServer, String artifactoryServerID, String json, PipelineBuildinfo buildinfo) throws MissingArgumentException {
        this.server = PipelineUtils.prepareArtifactoryServer(artifactoryServerID, artifactoryServer);
        this.json = json;
        this.buildinfo = buildinfo;

        if (this.buildinfo == null) {
            this.buildinfo = new PipelineBuildinfo();
        }
    }

    public ArtifactoryServer getServer() {
        return server;
    }

    public String getJson() {
        return json;
    }

    public PipelineBuildinfo getBuildinfo() {
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