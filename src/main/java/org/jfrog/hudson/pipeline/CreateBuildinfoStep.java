package org.jfrog.hudson.pipeline;

import com.google.inject.Inject;
import hudson.Extension;
import org.jenkinsci.plugins.workflow.steps.AbstractStepDescriptorImpl;
import org.jenkinsci.plugins.workflow.steps.AbstractStepImpl;
import org.jenkinsci.plugins.workflow.steps.AbstractSynchronousStepExecution;
import org.kohsuke.stapler.DataBoundConstructor;

import java.util.Date;

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

    public static class Execution extends AbstractSynchronousStepExecution<PipelineBuildInfo> {
        private static final long serialVersionUID = 1L;

        @Inject(optional = true)
        private transient CreateBuildinfoStep step;

        @Override
        protected PipelineBuildInfo run() throws Exception {
            PipelineBuildInfo buildinfo = new PipelineBuildInfo();
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
            return "rtCreateBuildInfo";
        }

        @Override
        public String getDisplayName() {
            return "Create buildInfo";
        }
    }

}
