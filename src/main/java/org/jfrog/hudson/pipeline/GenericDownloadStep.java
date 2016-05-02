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
public class GenericDownloadStep extends AbstractStepImpl {

    private final ArtifactoryPipelineServer artifactoryServer;
    private GenericDownloadUpload download;
    private String artifactoryServerID;

    @DataBoundConstructor
    public GenericDownloadStep(ArtifactoryPipelineServer artifactoryServer, String artifactoryServerID, String json, PipelineBuildinfo buildinfo) throws MissingArgumentException {
        this.download = new GenericDownloadUpload(artifactoryServer, artifactoryServerID, json, buildinfo);
        this.artifactoryServer = artifactoryServer;
        this.artifactoryServerID = artifactoryServerID;
    }


    public ArtifactoryPipelineServer getArtifactoryServer() {
        return artifactoryServer;
    }

    public String getArtifactoryServerID() {
        return artifactoryServerID;
    }

    public ArtifactoryServer getServer() {
        return download.getServer();
    }

    public String getJson() {
        return download.getJson();
    }

    public PipelineBuildinfo getBuildinfo() {
        return download.getBuildinfo();
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
            return "Artifactory: Download artifacts";
        }

        public ListBoxModel doFillArtifactoryServerIDItems() {
            return PipelineUtils.getServerListBox();
        }

        @Override
        public Map<String, Object> defineArguments(Step step) throws UnsupportedOperationException {
            GenericDownloadStep downloadStep = (GenericDownloadStep) step;
            return GenericDownloadUpload.getDefineArguments(downloadStep.getArtifactoryServerID(), downloadStep.getJson());
        }


        @Override
        public Step newInstance(StaplerRequest req, JSONObject formData) throws FormException {
            String artifactoryServerId = formData.getString("artifactoryServerID");
            String json = formData.getString("json");

            try {
                return new GenericDownloadStep(new ArtifactoryPipelineServer(null, null, null), artifactoryServerId, json, null);
            } catch (MissingArgumentException e) {
                e.printStackTrace();
            }
            return null;
        }

    }
}