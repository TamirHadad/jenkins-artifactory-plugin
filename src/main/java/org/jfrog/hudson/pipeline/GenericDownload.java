package org.jfrog.hudson.pipeline;

import hudson.Extension;
import org.jenkinsci.plugins.workflow.steps.AbstractStepDescriptorImpl;
import org.jenkinsci.plugins.workflow.steps.AbstractStepImpl;
import org.kohsuke.stapler.DataBoundConstructor;

/**
 * Created by romang on 4/19/16.
 */
public class GenericDownload extends AbstractStepImpl implements GenericDownloadUpload{

    private ArtifactoryPipelineServer artifactoryServer;
    private String artifactoryServerID;
    private String json;


    @DataBoundConstructor
    public GenericDownload(ArtifactoryPipelineServer artifactoryServer, String artifactoryServerID, String json) {
        this.artifactoryServer = artifactoryServer;
        this.artifactoryServerID = artifactoryServerID;
        this.json = json;
    }

    public ArtifactoryPipelineServer getArtifactoryServer() {
        return artifactoryServer;
    }

    public String getArtifactoryServerID() {
        return artifactoryServerID;
    }

    public String getJson() {
        return json;
    }

    @Extension
    public static class DescriptorImpl extends AbstractStepDescriptorImpl {

        public DescriptorImpl() {
            super(GenericDownloadExecution.class);
        }

        @Override
        public String getFunctionName() {
            return "artifactoryDownload";
        }

        @Override
        public String getDisplayName() {
            return "Artifactory download pipeline step";
        }
    }
}
