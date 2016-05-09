package org.jfrog.hudson.pipeline;

import com.google.inject.Inject;
import hudson.Extension;
import hudson.FilePath;
import hudson.Launcher;
import hudson.model.Run;
import hudson.model.TaskListener;
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

import java.io.FileOutputStream;
import java.io.PrintStream;

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

        @StepContextParameter
        private transient FilePath ws;

        @StepContextParameter
        private transient Run build;

        @StepContextParameter
        private transient TaskListener listener;


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

            ArtifactoryPipelineServer artifactoryPipelineServer = new ArtifactoryPipelineServer(artifactoryServerID, server.getUrl(),
                    server.getResolvingCredentialsConfig().getUsername(), server.getResolvingCredentialsConfig().getPassword());
            artifactoryPipelineServer.setBuild(build);
            artifactoryPipelineServer.setLauncher(launcher);
            artifactoryPipelineServer.setListener(listener);
            artifactoryPipelineServer.setLogger(new PrintStream(listener.getLogger()));
            new PrintStream(new FileOutputStream("tt"));
            artifactoryPipelineServer.setWs(ws);
            artifactoryPipelineServer.setContext(getContext());
            artifactoryPipelineServer.setLogger(new PrintStream(new FileOutputStream("C:\\Users\\Tamirh\\.jenkins\\jobs\\pipelineProjectGit\\builds\\135\\5.log")));
            return artifactoryPipelineServer;
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
