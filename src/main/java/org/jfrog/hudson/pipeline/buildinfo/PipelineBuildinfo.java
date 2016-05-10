package org.jfrog.hudson.pipeline.buildinfo;

import hudson.EnvVars;
import hudson.model.Run;
import hudson.model.TaskListener;
import jenkins.model.Jenkins;
import org.jenkinsci.plugins.workflow.steps.StepContext;
import org.jfrog.build.api.Artifact;
import org.jfrog.build.api.Dependency;
import org.jfrog.build.api.dependency.BuildDependency;
import org.jfrog.build.extractor.clientConfiguration.IncludeExcludePatterns;
import org.jfrog.build.extractor.clientConfiguration.PatternMatcher;
import org.jfrog.build.extractor.clientConfiguration.client.ArtifactoryBuildInfoClient;
import org.jfrog.hudson.ArtifactoryServer;
import org.jfrog.hudson.CredentialsConfig;
import org.jfrog.hudson.util.CredentialManager;

import java.io.IOException;
import java.io.Serializable;
import java.security.NoSuchAlgorithmException;
import java.util.*;

/**
 * Created by romang on 4/26/16.
 */
public class PipelineBuildInfo implements Serializable {

    private String name;
    private String number;

    private Map<Artifact, Artifact> deployedArtifacts = new HashMap<Artifact, Artifact>();
    private List<BuildDependency> buildDependencies = new ArrayList<BuildDependency>();
    private Map<Dependency, Dependency> publishedDependencies = new HashMap<Dependency, Dependency>();
    private Map<String, String> envVars = new HashMap<String, String>();
    private Map<String, String> sysVars = new HashMap<String, String>();

    public PipelineBuildInfo() {
    }

    public void appendDeployedArtifacts(List<Artifact> artifacts) {
        if (artifacts == null) {
            return;
        }
        for (Artifact artifact : artifacts) {
            deployedArtifacts.put(artifact, artifact);
        }
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public void appendBuildDependencies(List<BuildDependency> dependencies) {
        if (dependencies == null) {
            return;
        }

        buildDependencies.addAll(dependencies);
    }

    public void appendPublishedDependencies(List<Dependency> dependencies) {
        if (dependencies == null) {
            return;
        }
        for (Dependency dependency : dependencies) {
            publishedDependencies.put(dependency, dependency);
        }
    }

    public void appendEnvVariables(Map<String, String> vars, IncludeExcludePatterns patterns) {
        appendVariables(envVars, vars, patterns);
    }

    public void appendSysVariables(Map<String, String> vars, IncludeExcludePatterns patterns) {
        appendVariables(sysVars, vars, patterns);
    }

    private void appendVariables(Map<String, String> vars, Map<String, String> toAdd, IncludeExcludePatterns patterns) {
        for (Map.Entry<String, String> entry : toAdd.entrySet()) {
            String varKey = entry.getKey();
            if (PatternMatcher.pathConflicts(varKey, patterns)) {
                continue;
            }
            if (vars.containsKey(varKey)) {
                continue;
            }
            vars.put(varKey, entry.getValue());
        }
    }

    public String getName() {
        return name;
    }

    public String getNumber() {
        return number;
    }

    public Map<Artifact, Artifact> getDeployedArtifacts() {
        return deployedArtifacts;
    }

    public List<BuildDependency> getBuildDependencies() {
        return buildDependencies;
    }

    public Map<Dependency, Dependency> getPublishedDependencies() {
        return publishedDependencies;
    }

    public Map<String, String> getEnvVars() {
        return envVars;
    }

    public Map<String, String> getSysVars() {
        return sysVars;
    }

    public void merge(PipelineBuildInfo other) {
        this.deployedArtifacts.putAll(other.deployedArtifacts);
        this.publishedDependencies.putAll(other.publishedDependencies);
        this.buildDependencies.addAll(other.buildDependencies);
        this.envVars.putAll(other.getEnvVars());
        this.sysVars.putAll(other.getSysVars());
    }

    public void captureVariables(StepContext context) throws Exception {
        EnvVars env = context.get(EnvVars.class);
        envVars.putAll(env);
        Map<String, String> sysEnv = new HashMap<String, String>();
        Properties systemProperties = System.getProperties();
        Enumeration<?> enumeration = systemProperties.propertyNames();
        while (enumeration.hasMoreElements()) {
            String propertyKey = (String) enumeration.nextElement();
            sysEnv.put(propertyKey, systemProperties.getProperty(propertyKey));
        }
        sysVars.putAll(sysEnv);
    }

    public PipelineBuildinfoDeployer createDeployer(Run build, TaskListener listener, ArtifactoryServer server) throws InterruptedException, NoSuchAlgorithmException, IOException {
        ArtifactoryPipelineConfigurator config = new ArtifactoryPipelineConfigurator(server);
        CredentialsConfig preferredDeployer = CredentialManager.getPreferredDeployer(config, server);
        ArtifactoryBuildInfoClient client = server.createArtifactoryClient(preferredDeployer.provideUsername(),
                preferredDeployer.providePassword(), server.createProxyConfiguration(Jenkins.getInstance().proxy));

        return new PipelineBuildinfoDeployer(config, client, build, listener, this);
    }
}
