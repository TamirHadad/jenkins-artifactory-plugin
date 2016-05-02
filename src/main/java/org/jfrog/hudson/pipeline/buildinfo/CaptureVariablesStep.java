package org.jfrog.hudson.pipeline.buildinfo;

import com.google.inject.Inject;
import hudson.EnvVars;
import hudson.Extension;
import hudson.model.Run;
import hudson.model.TaskListener;
import org.apache.commons.cli.MissingArgumentException;
import org.jenkinsci.plugins.workflow.steps.AbstractStepDescriptorImpl;
import org.jenkinsci.plugins.workflow.steps.AbstractStepImpl;
import org.jenkinsci.plugins.workflow.steps.AbstractSynchronousStepExecution;
import org.jenkinsci.plugins.workflow.steps.StepContextParameter;
import org.jfrog.build.extractor.clientConfiguration.IncludeExcludePatterns;
import org.jfrog.hudson.pipeline.PipelineUtils;
import org.kohsuke.stapler.DataBoundConstructor;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * Created by romang on 4/27/16.
 */
public class CaptureVariablesStep /*extends AbstractStepImpl*/ {

    private final IncludeExcludePatterns patterns;
    private PipelineBuildinfo buildinfo;

    @DataBoundConstructor
    public CaptureVariablesStep(PipelineBuildinfo buildinfo, String includePatterns, String excludePatterns) throws MissingArgumentException {
        this.buildinfo = buildinfo;
        this.patterns = new IncludeExcludePatterns(includePatterns, excludePatterns);
    }

    public PipelineBuildinfo getBuildInfo() {
        return buildinfo;
    }

    public IncludeExcludePatterns getPatterns() {
        return patterns;
    }

    public static class Execution extends AbstractSynchronousStepExecution<Integer> {
        private static final long serialVersionUID = 1L;

        @Inject(optional = true)
        private transient CaptureVariablesStep step;

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
            EnvVars env = getContext().get(EnvVars.class);
            buildinfo.appendEnvVariables(env, step.getPatterns());
            Map<String, String> sysEnv = new HashMap<String, String>();

            Properties systemProperties = System.getProperties();
            Enumeration<?> enumeration = systemProperties.propertyNames();
            while (enumeration.hasMoreElements()) {
                String propertyKey = (String) enumeration.nextElement();
                sysEnv.put(propertyKey, systemProperties.getProperty(propertyKey));
            }
            buildinfo.appendSysVariables(sysEnv, step.getPatterns());
            return 0;
        }
    }

    @Extension
    public static final class DescriptorImpl extends AbstractStepDescriptorImpl {

        public DescriptorImpl() {
            super(CaptureVariablesStep.Execution.class);
        }

        @Override
        public String getFunctionName() {
            return "captureVariables";
        }

        @Override
        public String getDisplayName() {
            return "Capture environment and system variables, add them to build info";
        }
    }
}
