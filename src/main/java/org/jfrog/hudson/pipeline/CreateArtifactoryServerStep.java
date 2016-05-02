package org.jfrog.hudson.pipeline;

import com.google.inject.Inject;
import hudson.Extension;
import org.apache.commons.cli.MissingArgumentException;
import org.jenkinsci.plugins.workflow.steps.AbstractStepDescriptorImpl;
import org.jenkinsci.plugins.workflow.steps.AbstractStepImpl;
import org.jenkinsci.plugins.workflow.steps.AbstractSynchronousStepExecution;
import org.kohsuke.stapler.DataBoundConstructor;

/**
 * Created by romang on 4/21/16.
 */
public class CreateArtifactoryServerStep extends AbstractStepImpl {
    private String url;
    private String username;
    private String password;

    @DataBoundConstructor
    public CreateArtifactoryServerStep(String url, String username, String password) {
        this.url = url;
        this.username = username;
        this.password = password;
    }

    public String getUrl() {
        return url;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public static class Execution extends AbstractSynchronousStepExecution<ArtifactoryPipelineServer> {
        private static final long serialVersionUID = 1L;

        @Inject(optional = true)
        private transient CreateArtifactoryServerStep step;

        @Override
        protected ArtifactoryPipelineServer run() throws Exception {
            String artifactoryUrl = step.getUrl();
            if (artifactoryUrl == null || artifactoryUrl == "") {
                getContext().onFailure(new MissingArgumentException("Artifactory server URL is mandatory"));
            }
            return new ArtifactoryPipelineServer(artifactoryUrl, step.getUsername(), step.getPassword());
        }
    }

    @Extension
    public static final class DescriptorImpl extends AbstractStepDescriptorImpl {

        public DescriptorImpl() {
            super(CreateArtifactoryServerStep.Execution.class);
        }

        @Override
        public String getFunctionName() {
            return "createArtifactoryServer";
        }

        @Override
        public String getDisplayName() {
            return "Artifactory: Creates server object";
        }
    }
}
