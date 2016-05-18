package org.jfrog.hudson.pipeline.steps;

import com.google.inject.Inject;
import hudson.Extension;
import hudson.model.Run;
import hudson.model.TaskListener;
import org.apache.commons.cli.MissingArgumentException;
import org.apache.commons.lang.StringUtils;
import org.jenkinsci.plugins.workflow.steps.*;
import org.jfrog.hudson.pipeline.types.ArtifactoryServer;
import org.kohsuke.stapler.DataBoundConstructor;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by romang on 4/21/16.
 */
public class CreateArtifactoryServerStep extends AbstractStepImpl {
    private String url;
    private String userName;
    private String password;

    @DataBoundConstructor
    public CreateArtifactoryServerStep(String url, String userName, String password) {
        this.url = url;
        this.userName = userName;
        this.password = password;
    }

    public String getUrl() {
        return url;
    }

    public String getUserName() {
        return userName;
    }

    public String getPassword() {
        return password;
    }

    public static class Execution extends AbstractSynchronousStepExecution<ArtifactoryServer> {
        private static final long serialVersionUID = 1L;

        @StepContextParameter
        private transient Run build;

        @StepContextParameter
        private transient TaskListener listener;

        @Inject(optional = true)
        private transient CreateArtifactoryServerStep step;

        @Override
        protected ArtifactoryServer run() throws Exception {
            String artifactoryUrl = step.getUrl();
            if (artifactoryUrl == null || artifactoryUrl == "") {
                getContext().onFailure(new MissingArgumentException("Artifactory server URL is mandatory"));
            }
            return new ArtifactoryServer(artifactoryUrl, step.getUserName(), step.getPassword(), build, listener/*, step.getCredentialsId()*/);
        }
    }

    @Extension
    public static final class DescriptorImpl extends AbstractStepDescriptorImpl {

        public DescriptorImpl() {
            super(CreateArtifactoryServerStep.Execution.class);
        }

        @Override
        public String getFunctionName() {
            return "newArtifactoryServer";
        }

        @Override
        public String getDisplayName() {
            return "Returns new Artifactory server";
        }

        @Override
        public Map<String, Object> defineArguments(Step step) throws UnsupportedOperationException {
            Map<String, Object> args = new HashMap<String, Object>();
            CreateArtifactoryServerStep cStep = (CreateArtifactoryServerStep) step;

            if (StringUtils.isNotEmpty(cStep.getUrl())) {
                args.put("url", cStep.getUrl());
            }
            if (StringUtils.isNotEmpty(cStep.getUserName())) {
                args.put("username", cStep.getUserName());
            }
            if (StringUtils.isNotEmpty(cStep.getPassword())) {
                args.put("password", cStep.getPassword());
            }
            return args;
        }
    }
}
