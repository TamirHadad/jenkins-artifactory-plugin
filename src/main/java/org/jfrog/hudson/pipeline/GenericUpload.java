package org.jfrog.hudson.pipeline;

import hudson.Extension;
import org.jenkinsci.plugins.workflow.steps.AbstractStepDescriptorImpl;
import org.jenkinsci.plugins.workflow.steps.AbstractStepImpl;
import org.kohsuke.stapler.DataBoundConstructor;

/**
 * Created by romang on 4/19/16.
 */
public class GenericUpload extends AbstractStepImpl implements GenericDownloadUpload {

    private ArtifactoryPipelineServer artifactoryServer;
    private String artifactoryServerID;
    private String json;

    @DataBoundConstructor
    public GenericUpload(ArtifactoryPipelineServer artifactoryServer, String artifactoryServerID, String json) {
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
            super(GenericUploadExecution.class);
        }

        @Override
        public String getFunctionName() {
            return "artifactoryUpload";
        }

        @Override
        public String getDisplayName() {
            return "Artifactory upload pipeline step";
        }
    }
}
