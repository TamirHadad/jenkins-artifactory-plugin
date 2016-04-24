package org.jfrog.hudson.pipeline;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.google.inject.Inject;
import hudson.FilePath;
import hudson.Launcher;
import hudson.model.Run;
import hudson.model.TaskListener;
import jenkins.model.Jenkins;
import org.codehaus.jackson.map.ObjectMapper;
import org.jenkinsci.plugins.workflow.steps.AbstractStepExecutionImpl;
import org.jenkinsci.plugins.workflow.steps.StepContextParameter;
import org.jfrog.build.api.Artifact;
import org.jfrog.build.api.Dependency;
import org.jfrog.build.api.util.Log;
import org.jfrog.hudson.ArtifactoryServer;
import org.jfrog.hudson.generic.GenericArtifactsDeployer;
import org.jfrog.hudson.util.JenkinsBuildInfoLog;

import javax.annotation.Nonnull;
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
    private transient GenericUpload step;

    private transient List<Dependency> publishedDependencies;

    private static final long serialVersionUID = 1L;

    private Log log;

    public boolean start() throws Exception {
        log = new JenkinsBuildInfoLog(listener);
        ObjectMapper mapper = new ObjectMapper();
        ArtifactoryDownloadUploadJson uploadJson = mapper.readValue(step.getJson(), ArtifactoryDownloadUploadJson.class);
        ArtifactoryServer server = PipelineUtils.prepareArtifactoryServer(getContext(), step);
        uploadArtifacts(server, uploadJson);

        getContext().onSuccess(null);
        return false;
    }


    private void uploadArtifacts(ArtifactoryServer server, ArtifactoryDownloadUploadJson uploadJson) {


    }


    public void stop(@Nonnull Throwable throwable) throws Exception {

    }

}
