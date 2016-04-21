package org.jfrog.hudson.pipeline;

import hudson.Extension;
import org.jenkinsci.plugins.workflow.steps.AbstractStepDescriptorImpl;
import org.jenkinsci.plugins.workflow.steps.AbstractStepImpl;
import org.kohsuke.stapler.DataBoundConstructor;

/**
 * Created by romang on 4/19/16.
 */
public class GenericResolver extends AbstractStepImpl {

    private String artifactoryName;
    private String json;

    @DataBoundConstructor
    public GenericResolver(String artifactoryName, String json) {
        this.artifactoryName = artifactoryName;
        this.json = json;
    }

    public String getArtifactoryName() {
        return artifactoryName;
    }

    public String getJson() {
        return json;
    }

    @Extension
    public static class DescriptorImpl extends AbstractStepDescriptorImpl {

        public DescriptorImpl() {
            super(GenericResolverExecution.class);
        }

        @Override
        public String getFunctionName() {
            return "artifactoryDownload";
        }

        @Override
        public String getDisplayName() {
            return "Artifactory pipeline step";
        }
    }
}
