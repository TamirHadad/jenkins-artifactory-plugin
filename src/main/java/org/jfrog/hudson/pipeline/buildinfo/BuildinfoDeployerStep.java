package org.jfrog.hudson.pipeline.buildinfo;

import com.google.inject.Inject;
import hudson.Extension;
import hudson.model.Run;
import hudson.model.TaskListener;
import hudson.util.ListBoxModel;
import net.sf.json.JSONObject;
import org.apache.commons.cli.MissingArgumentException;
import org.apache.commons.lang.StringUtils;
import org.jenkinsci.plugins.workflow.steps.*;
import org.jfrog.hudson.ArtifactoryServer;
import org.jfrog.hudson.pipeline.ArtifactoryPipelineServer;
import org.jfrog.hudson.pipeline.PipelineUtils;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.StaplerRequest;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by romang on 4/25/16.
 */
public class BuildinfoDeployerStep extends AbstractStepImpl {

    private final ArtifactoryServer server;
    private PipelineBuildinfo buildinfo;

    public ArtifactoryServer getServer() {
        return server;
    }

    @DataBoundConstructor
    public BuildinfoDeployerStep(ArtifactoryPipelineServer artifactoryServer, String artifactoryServerID, PipelineBuildinfo buildinfo) throws MissingArgumentException {
        this.server = PipelineUtils.prepareArtifactoryServer(artifactoryServerID, artifactoryServer);
        this.buildinfo = buildinfo;
    }

    public PipelineBuildinfo getBuildInfo() {
        return buildinfo;
    }

    public static class Execution extends AbstractSynchronousStepExecution<Integer> {
        private static final long serialVersionUID = 1L;

        @Inject(optional = true)
        private transient BuildinfoDeployerStep step;

        @StepContextParameter
        private transient Run build;

        @StepContextParameter
        private transient TaskListener listener;

        @Override
        protected Integer run() throws Exception {
            PipelineBuildinfo buildinfo = step.getBuildInfo();
            if (buildinfo == null) {
                buildinfo = PipelineUtils.getRunBuildInfo(build);
            }

            buildinfo.createDeployer(build, listener, step.getServer()).deploy();
            return 0;
        }
    }

    @Extension
    public static final class DescriptorImpl extends AbstractStepDescriptorImpl {

        public DescriptorImpl() {
            super(BuildinfoDeployerStep.Execution.class);
        }

        @Override
        public String getFunctionName() {
            return "publishBuildinfo";
        }

        @Override
        public String getDisplayName() {
            return "Artifactory - Publish build info";
        }

        public ListBoxModel doFillArtifactoryServerIDItems() {
            return PipelineUtils.getServerListBox();
        }

        @Override
        public Map<String, Object> defineArguments(Step step) throws UnsupportedOperationException {
            BuildinfoDeployerStep buildinfoStep = (BuildinfoDeployerStep) step;
            Map<String, Object> args = new HashMap<String, Object>();
            if (buildinfoStep.getServer() == null) {
                args.put("artifactoryServer", "<provide Artifactory server instance>");
            } else {
                args.put("artifactoryServerID", buildinfoStep.getServer().getName());
            }

            args.put("buildinfo", "<optional: provide Artifactory buildinfo instance here>");
            return args;

        }

        @Override
        public Step newInstance(StaplerRequest req, JSONObject formData) throws FormException {
            String artifactoryServerId = formData.getString("artifactoryServerID");
            try {
                return new BuildinfoDeployerStep(null, artifactoryServerId, new PipelineBuildinfo());
            } catch (MissingArgumentException e) {
                e.printStackTrace();
            }
            return null;
        }
    }
}
