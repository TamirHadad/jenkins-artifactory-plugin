package org.jfrog.hudson.pipeline;

import hudson.Extension;
import hudson.util.ListBoxModel;
import net.sf.json.JSONObject;
import org.apache.commons.cli.MissingArgumentException;
import org.jenkinsci.plugins.workflow.steps.AbstractStepDescriptorImpl;
import org.jenkinsci.plugins.workflow.steps.AbstractStepImpl;
import org.jenkinsci.plugins.workflow.steps.Step;
import org.jfrog.hudson.ArtifactoryServer;
import org.jfrog.hudson.pipeline.buildinfo.PipelineBuildinfo;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.StaplerRequest;

import java.util.Map;

/**
 * Created by romang on 4/19/16.
 */
public class GenericUploadStep extends AbstractStepImpl {

    private final ArtifactoryPipelineServer artifactoryServer;
    private final String artifactoryServerID;
    private GenericDownloadUpload upload;


    @DataBoundConstructor
    public GenericUploadStep(ArtifactoryPipelineServer artifactoryServer, String artifactoryServerID, String json, PipelineBuildinfo buildinfo) throws MissingArgumentException {
        this.upload = new GenericDownloadUpload(artifactoryServer, artifactoryServerID, json, buildinfo);
        this.artifactoryServer = artifactoryServer;
        this.artifactoryServerID = artifactoryServerID;
    }

    public String getArtifactoryServerID() {
        return artifactoryServerID;
    }

    public ArtifactoryServer getServer() {
        return upload.getServer();
    }

    public String getJson() {
        return upload.getJson();
    }

    public PipelineBuildinfo getBuildinfo() {
        return upload.getBuildinfo();
    }

    @Extension
    public static class DescriptorImpl extends AbstractStepDescriptorImpl {

        public DescriptorImpl() {
            super(GenericUploadExecution.class);
        }

        @Override
        public String getFunctionName() {
            return "rtUpload";
        }

        @Override
        public String getDisplayName() {
            return "Upload artifacts";
        }

        public ListBoxModel doFillArtifactoryServerIDItems() {
            return PipelineUtils.getServerListBox();
        }

        @Override
        public Map<String, Object> defineArguments(Step step) throws UnsupportedOperationException {
            GenericUploadStep uploadStep = (GenericUploadStep) step;
            return GenericDownloadUpload.getDefineArguments(uploadStep.getArtifactoryServerID(), uploadStep.getJson());
        }


        @Override
        public Step newInstance(StaplerRequest req, JSONObject formData) throws FormException {
            String artifactoryServerId = formData.getString("artifactoryServerID");
            String json = formData.getString("json");

            try {
                return new GenericUploadStep(new ArtifactoryPipelineServer(null, null, null, null), artifactoryServerId, json, null);
            } catch (MissingArgumentException e) {
                e.printStackTrace();
            }
            return null;
        }
    }
}