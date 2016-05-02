package org.jfrog.hudson.pipeline;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.inject.Inject;
import hudson.FilePath;
import hudson.Launcher;
import hudson.model.Run;
import hudson.model.TaskListener;
import jenkins.model.Jenkins;
import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.jenkinsci.plugins.workflow.steps.AbstractStepExecutionImpl;
import org.jenkinsci.plugins.workflow.steps.StepContextParameter;
import org.jfrog.build.api.Artifact;
import org.jfrog.build.api.util.Log;
import org.jfrog.hudson.ArtifactoryServer;
import org.jfrog.hudson.generic.GenericArtifactsDeployer;
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

    public boolean start() throws Exception {
        log = new JenkinsBuildInfoLog(listener);
        ObjectMapper mapper = new ObjectMapper();
        ArtifactoryDownloadUploadJson uploadJson = mapper.readValue(step.getJson(), ArtifactoryDownloadUploadJson.class);

        uploadArtifacts(uploadJson);

        // return buildinfo
        getContext().onSuccess(step.getBuildinfo());
        return false;
    }

    private void uploadArtifacts(ArtifactoryDownloadUploadJson uploadJson) {
        ArrayListMultimap<String, String> propertiesToAdd = ArrayListMultimap.create();
        try {
            ArtifactoryServer server = step.getServer();
            for (ArtifactoryFileJson file : uploadJson.getFiles()) {
                Multimap<String, String> pairs = HashMultimap.create();

                String repoKey = getRepositoryKey(file.getTarget());
                pairs.put(file.getPattern(), getLocalPath(file.getTarget()));

                List<Artifact> artifactsToDeploy = ws.act(new GenericArtifactsDeployer.FilesDeployerCallable(listener, pairs, server,
                        server.getDeployerCredentialsConfig().getCredentials(), repoKey, propertiesToAdd,
                        server.createProxyConfiguration(Jenkins.getInstance().proxy)));

                step.getBuildinfo().appendDeployedArtifacts(artifactsToDeploy);
                PipelineUtils.getRunBuildInfo(build).appendDeployedArtifacts(artifactsToDeploy);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    private String getRepositoryKey(String path) {
        return StringUtils.substringBefore(path, "/");
    }

    private String getLocalPath(String path) {
        return StringUtils.substringAfter(path, "/");
    }

    private Multimap<String, String> prepareUploadPairs(ArtifactoryDownloadUploadJson uploadJson) {

        Multimap<String, String> pairs = HashMultimap.create();
        for (ArtifactoryFileJson file : uploadJson.getFiles()) {

            pairs.put(file.getPattern(), file.getTarget());
        }
        return pairs;
    }

    public void stop(@Nonnull Throwable throwable) throws Exception {

    }

}
