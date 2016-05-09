package org.jfrog.hudson.pipeline;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.inject.Inject;
import hudson.EnvVars;
import hudson.FilePath;
import hudson.Launcher;
import hudson.model.Cause;
import hudson.model.Run;
import hudson.model.TaskListener;
import jenkins.model.Jenkins;
import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.jenkinsci.plugins.workflow.steps.AbstractStepExecutionImpl;
import org.jenkinsci.plugins.workflow.steps.StepContextParameter;
import org.jfrog.build.api.Artifact;
import org.jfrog.build.api.BuildInfoFields;
import org.jfrog.build.api.util.Log;
import org.jfrog.hudson.ArtifactoryServer;
import org.jfrog.hudson.action.ActionableHelper;
import org.jfrog.hudson.generic.GenericArtifactsDeployer;
import org.jfrog.hudson.pipeline.buildinfo.PipelineBuildinfo;
import org.jfrog.hudson.util.BuildUniqueIdentifierHelper;
import org.jfrog.hudson.util.ExtractorUtils;
import org.jfrog.hudson.util.JenkinsBuildInfoLog;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.util.List;

/**
 * Created by romang on 4/24/16.
 */
public class GenericUploadExecution extends AbstractStepExecutionImpl {
    @StepContextParameter
    private transient FilePath ws;

    @StepContextParameter
    private transient Run build;

    @StepContextParameter
    private transient Launcher launcher;

    @StepContextParameter
    private transient TaskListener listener;

    @Inject(optional = true)
    private transient GenericUploadStep step;

    private static final long serialVersionUID = 1L;

    private Log log;
    private PipelineBuildinfo buildinfo;

    public boolean start() throws Exception {
        log = new JenkinsBuildInfoLog(listener);
        buildinfo = PipelineUtils.prepareBuildinfo(build, step.getBuildinfo());

        ObjectMapper mapper = new ObjectMapper();
        ArtifactoryDownloadUploadJson uploadJson = mapper.readValue(step.getJson(), ArtifactoryDownloadUploadJson.class);

        uploadArtifacts(uploadJson);

        // return buildinfo
        getContext().onSuccess(buildinfo);
        return false;
    }

    private void uploadArtifacts(ArtifactoryDownloadUploadJson uploadJson) {
        try {
            ArtifactoryServer server = step.getServer();
            for (ArtifactoryFileJson file : uploadJson.getFiles()) {
                ArrayListMultimap<String, String> propertiesToAdd = getPropertiesMap(file.getProps());
                Multimap<String, String> pairs = HashMultimap.create();

                String repoKey = getRepositoryKey(file.getTarget());
                pairs.put(file.getPattern(), getLocalPath(file.getTarget()));

                boolean isFlat = file.getFlat() != null ? org.eclipse.jgit.util.StringUtils.toBoolean(file.getFlat()) : true;
                boolean isRecursive = file.getRecursive() != null ? org.eclipse.jgit.util.StringUtils.toBoolean(file.getRecursive()) : true;

                GenericArtifactsDeployer.FilesDeployerCallable deployer = new GenericArtifactsDeployer.FilesDeployerCallable(listener, pairs, server,
                        server.getDeployerCredentialsConfig().getCredentials(), repoKey, propertiesToAdd,
                        server.createProxyConfiguration(Jenkins.getInstance().proxy));
                deployer.setPatternType(GenericArtifactsDeployer.FilesDeployerCallable.PatternType.WILDCARD);
                deployer.setRecursive(isRecursive);
                deployer.setFlat(isFlat);

                List<Artifact> artifactsToDeploy = ws.act(deployer);

                buildinfo.appendDeployedArtifacts(artifactsToDeploy);
                PipelineUtils.getRunBuildInfo(build).appendDeployedArtifacts(artifactsToDeploy);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private ArrayListMultimap<String, String> getPropertiesMap(String props) throws IOException, InterruptedException {
        ArrayListMultimap<String, String> properties = ArrayListMultimap.create();

        if (buildinfo.getName() != null) {
            properties.put("build.name", buildinfo.getName());
        } else {
            properties.put("build.name", BuildUniqueIdentifierHelper.getBuildName(build));
        }

        if (buildinfo.getNumber() != null) {
            properties.put("build.number", buildinfo.getNumber());
        } else {
            properties.put("build.number", BuildUniqueIdentifierHelper.getBuildNumber(build));
        }

        properties.put("build.timestamp", build.getTimestamp().getTime().getTime() + "");
        Cause.UpstreamCause parent = ActionableHelper.getUpstreamCause(build);
        if (parent != null) {
            properties.put("build.parentName", ExtractorUtils.sanitizeBuildName(parent.getUpstreamProject()));
            properties.put("build.parentNumber", parent.getUpstreamBuild() + "");
        }
        EnvVars env = getContext().get(EnvVars.class);
        String revision = ExtractorUtils.getVcsRevision(env);
        if (StringUtils.isNotBlank(revision)) {
            properties.put(BuildInfoFields.VCS_REVISION, revision);
        }

        if (props == null) {
            return properties;
        }

        for (String prop : props.trim().split(";")) {
            String key = StringUtils.substringBefore(prop, "=");
            String values = StringUtils.substringAfter(prop, "=");
            for (String value : values.split(",")) {
                properties.put(key, value);
            }
        }
        return properties;
    }

    private String getRepositoryKey(String path) {
        return StringUtils.substringBefore(path, "/");
    }

    private String getLocalPath(String path) {
        return StringUtils.substringAfter(path, "/");
    }

    public void stop(@Nonnull Throwable throwable) throws Exception {

    }

}
