package org.jfrog.hudson.pipeline;

import com.google.inject.Inject;
import hudson.FilePath;
import hudson.Launcher;
import hudson.model.Run;
import hudson.model.TaskListener;
import jenkins.model.Jenkins;
import org.codehaus.jackson.map.ObjectMapper;
import org.jenkinsci.plugins.workflow.steps.AbstractStepExecutionImpl;
import org.jenkinsci.plugins.workflow.steps.StepContextParameter;
import org.jfrog.build.api.Dependency;
import org.jfrog.build.api.util.Log;
import org.jfrog.build.client.ProxyConfiguration;
import org.jfrog.build.extractor.clientConfiguration.client.ArtifactoryDependenciesClient;
import org.jfrog.build.extractor.clientConfiguration.util.DependenciesHelper;
import org.jfrog.hudson.ArtifactoryServer;
import org.jfrog.hudson.CredentialsConfig;
import org.jfrog.hudson.generic.DependenciesDownloaderImpl;
import org.jfrog.hudson.util.JenkinsBuildInfoLog;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.util.List;

/**
 * Created by romang on 4/19/16.
 */
public class GenericDownloadExecution extends AbstractStepExecutionImpl {
    @StepContextParameter
    private transient FilePath ws;

    @StepContextParameter
    private transient Run build;

    @StepContextParameter
    private transient Launcher launcher;

    @StepContextParameter
    private transient TaskListener listener;

    @Inject(optional = true)
    private transient GenericDownload step;

    private transient List<Dependency> publishedDependencies;

    private static final long serialVersionUID = 1L;

    private Log log;

    public boolean start() throws Exception {
        log = new JenkinsBuildInfoLog(listener);

        ObjectMapper mapper = new ObjectMapper();
        ArtifactoryDownloadUploadJson downloadJson = mapper.readValue(step.getJson(), ArtifactoryDownloadUploadJson.class);
        downloadArtifacts(downloadJson);

        listener.getLogger().println(downloadJson.getFiles()[1].getAql());

        getContext().onSuccess(null);
        return false;
    }

    private void downloadArtifacts(ArtifactoryDownloadUploadJson downloadJson) {
        hudson.ProxyConfiguration proxy = Jenkins.getInstance().proxy;
        ProxyConfiguration proxyConfiguration = null;
        if (proxy != null) {
            proxyConfiguration = new ProxyConfiguration();
            proxyConfiguration.host = proxy.name;
            proxyConfiguration.port = proxy.port;
            proxyConfiguration.username = proxy.getUserName();
            proxyConfiguration.password = proxy.getPassword();
        }

        ArtifactoryServer server = PipelineUtils.prepareArtifactoryServer(getContext(), step);
        CredentialsConfig preferredResolver = server.getDeployerCredentialsConfig();
        ArtifactoryDependenciesClient dependenciesClient = server.createArtifactoryDependenciesClient(
                preferredResolver.provideUsername(), preferredResolver.providePassword(), proxyConfiguration,
                null);

        DependenciesDownloaderImpl dependancyDownloader = new DependenciesDownloaderImpl(dependenciesClient, ws, log);
        DependenciesHelper helper = new DependenciesHelper(dependancyDownloader, log);
        try {
            publishedDependencies = helper.retrievePublishedDependencies(downloadJson.getFiles()[0].getPattern());
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void stop(@Nonnull Throwable throwable) throws Exception {

    }
}
