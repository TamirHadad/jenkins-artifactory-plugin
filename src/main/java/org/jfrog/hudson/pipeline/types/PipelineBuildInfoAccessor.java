package org.jfrog.hudson.pipeline.types;

import org.jenkinsci.plugins.workflow.steps.StepContext;
import org.jfrog.build.api.Artifact;
import org.jfrog.build.api.Dependency;
import org.jfrog.build.api.dependency.BuildDependency;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by Tamirh on 16/05/2016.
 */
public class PipelineBuildInfoAccessor {
    BuildInfo buildInfo;
    public PipelineBuildInfoAccessor (BuildInfo buildInfo) {
        this.buildInfo = buildInfo;
    }

    public void appendPublishedDependencies(List<Dependency> resolvedDependencies) {
        this.buildInfo.appendPublishedDependencies(resolvedDependencies);
    }

    public Map<String, String> getEnvVars() {
        return this.buildInfo.getEnvVars();
    }

    public Map<String, String> getSysVars() {
        return this.buildInfo.getSysVars();
    }

    public List<BuildDependency> getBuildDependencies() {
        return this.buildInfo.getBuildDependencies();
    }

    public Date getStartDate() {
        return this.buildInfo.getStartDate();
    }

    public Map<Artifact, Artifact> getDeployedArtifacts() {
        return this.buildInfo.getDeployedArtifacts();
    }

    public String getBuildName() {
        return this.buildInfo.getBuildName();
    }

    public String getBuildNumber() {
        return this.buildInfo.getBuildNumber();
    }

    public Map<Dependency, Dependency> getPublishedDependencies() {
        return this.buildInfo.getPublishedDependencies();
    }

    public void captureVariables(StepContext context) throws Exception {
        this.buildInfo.captureVariables(context);
    }

    public void appendDeployedArtifacts(List<Artifact> artifacts) {
        this.buildInfo.appendDeployedArtifacts(artifacts);
    }
}
