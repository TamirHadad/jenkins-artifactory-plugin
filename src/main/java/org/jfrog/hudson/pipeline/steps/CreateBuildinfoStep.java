package org.jfrog.hudson.pipeline.steps;

import com.google.inject.Inject;
import hudson.Extension;
import org.jenkinsci.plugins.workflow.steps.AbstractStepDescriptorImpl;
import org.jenkinsci.plugins.workflow.steps.AbstractStepImpl;
import org.jenkinsci.plugins.workflow.steps.AbstractSynchronousStepExecution;
import org.jfrog.hudson.pipeline.types.BuildInfo;
import org.kohsuke.stapler.DataBoundConstructor;

/**
 * Created by romang on 5/2/16.
 */
public class CreateBuildinfoStep extends AbstractStepImpl {

    private final String name;
    private final String number;

    @DataBoundConstructor
    public CreateBuildinfoStep(String name, String number) {
        this.name = name;
        this.number = number;
    }

    public String getName() {
        return name;
    }

    public String getNumber() {
        return number;
    }

    public static class Execution extends AbstractSynchronousStepExecution<BuildInfo> {
        private static final long serialVersionUID = 1L;

        @Inject(optional = true)
        private transient CreateBuildinfoStep step;

        @Override
        protected BuildInfo run() throws Exception {
            BuildInfo buildinfo = new BuildInfo();
            buildinfo.setName(step.getName());
            buildinfo.setNumber(step.getNumber());
            return buildinfo;
        }
    }

    @Extension
    public static final class DescriptorImpl extends AbstractStepDescriptorImpl {

        public DescriptorImpl() {
            super(CreateBuildinfoStep.Execution.class);
        }

        @Override
        public String getFunctionName() {
            return "newArtifactoryBuildInfo";
        }

        @Override
        public String getDisplayName() {
            return "Create buildInfo";
        }
    }

}
