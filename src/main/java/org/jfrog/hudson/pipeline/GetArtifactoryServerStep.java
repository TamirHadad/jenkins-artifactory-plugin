package org.jfrog.hudson.pipeline;

import com.google.inject.Inject;
import hudson.Extension;
import hudson.Launcher;
import hudson.util.ListBoxModel;
import org.acegisecurity.acls.NotFoundException;
import org.apache.commons.cli.MissingArgumentException;
import org.jenkinsci.plugins.workflow.steps.AbstractStepDescriptorImpl;
import org.jenkinsci.plugins.workflow.steps.AbstractStepImpl;
import org.jenkinsci.plugins.workflow.steps.AbstractSynchronousStepExecution;
import org.jenkinsci.plugins.workflow.steps.StepContextParameter;
import org.jfrog.hudson.ArtifactoryServer;
import org.jfrog.hudson.util.RepositoriesUtils;
import org.kohsuke.stapler.DataBoundConstructor;

/**
 * Created by romang on 4/21/16.
 */
public class GetArtifactoryServerStep extends AbstractStepImpl {
    private String artifactoryServerID;

    @DataBoundConstructor
    public GetArtifactoryServerStep(String artifactoryServerID) {
        this.artifactoryServerID = artifactoryServerID;
    }

    public String getArtifactoryServerID() {
        return artifactoryServerID;
    }

    public static class Execution extends AbstractSynchronousStepExecution<ArtifactoryPipelineServer> {

        @StepContextParameter
        private transient Launcher launcher;

        @Inject(optional = true)
        private transient GetArtifactoryServerStep step;

        @Override
        protected ArtifactoryPipelineServer run() throws Exception {
            String artifactoryServerID = step.getArtifactoryServerID();
            if (artifactoryServerID == null || artifactoryServerID == "") {
                getContext().onFailure(new MissingArgumentException("Artifactory server name is mandatory"));
            }

            ArtifactoryServer server = RepositoriesUtils.getArtifactoryServer(step.getArtifactoryServerID(), RepositoriesUtils.getArtifactoryServers());
            if (server == null) {
                getContext().onFailure(new NotFoundException("Couldn't find Artifactory named: " + artifactoryServerID));
            }

            return new ArtifactoryPipelineServer(artifactoryServerID, server.getUrl(),
                    server.getResolvingCredentialsConfig().getUsername(), server.getResolvingCredentialsConfig().getPassword(), "");
        }

        private static final long serialVersionUID = 1L;

    }

    @Extension
    public static final class DescriptorImpl extends AbstractStepDescriptorImpl {

        public DescriptorImpl() {
            super(Execution.class);
        }

        @Override
        public String getFunctionName() {
            return "rtGetServer";
        }

        @Override
        public String getDisplayName() {
            return "Get Artifactory server from Jenkins config";
        }

        public ListBoxModel doFillArtifactoryServerIDItems() {
            return PipelineUtils.getServerListBox();
        }
    }
}
