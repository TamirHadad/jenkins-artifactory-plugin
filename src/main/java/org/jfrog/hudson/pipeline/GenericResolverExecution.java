package org.jfrog.hudson.pipeline;

import com.google.gson.Gson;
import com.google.inject.Inject;
import hudson.FilePath;
import hudson.Launcher;
import hudson.model.Run;
import hudson.model.TaskListener;
import jenkins.model.Jenkins;
import org.acegisecurity.acls.NotFoundException;
import org.apache.commons.cli.MissingArgumentException;
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
import org.jfrog.hudson.util.RepositoriesUtils;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.util.List;

/**
 * Created by romang on 4/19/16.
 */
public class GenericResolverExecution extends AbstractStepExecutionImpl {
    @StepContextParameter
    private transient FilePath ws;

    @StepContextParameter
    private transient Run build;

    @StepContextParameter
    private transient Launcher launcher;

    @StepContextParameter
    private transient TaskListener listener;

    @Inject(optional = true)
    private transient GenericResolver step;

    private transient List<Dependency> publishedDependencies;

    private static final long serialVersionUID = 1L;

    public boolean start() throws Exception {

        Gson gson = new Gson();
        ArtifactoryDownloadJson downloadJson = gson.fromJson(step.getJson(), ArtifactoryDownloadJson.class);
        ArtifactoryServer server = prepareArtifactoryServer(downloadJson);
        downloadArtifacts(server, downloadJson);

        getContext().onSuccess(null);
        return false;
    }

    private ArtifactoryServer prepareArtifactoryServer(ArtifactoryDownloadJson downloadJson) {
        if (downloadJson.getUrl() != null && downloadJson.getUrl() != "") {
            CredentialsConfig credentials = new CredentialsConfig(downloadJson.getUsername(), downloadJson.getPassword(), null, null);

            return new ArtifactoryServer(null, downloadJson.getUrl(), credentials,
                    credentials, 0, downloadJson.getBypassProxy());
        }

        String artifactoryServerID = step.getArtifactoryName();
        if(artifactoryServerID == null || artifactoryServerID == ""){
            getContext().onFailure(new MissingArgumentException("Artifactory server name is mandatory"));
        }

        ArtifactoryServer server = RepositoriesUtils.getArtifactoryServer(step.getArtifactoryName(), RepositoriesUtils.getArtifactoryServers());
        if(server == null){
            getContext().onFailure(new NotFoundException("Couldn't find Artifactory named: " + artifactoryServerID));
        }
        return server;
    }


    private void downloadArtifacts(ArtifactoryServer server, ArtifactoryDownloadJson downloadJson) {
        hudson.ProxyConfiguration proxy = Jenkins.getInstance().proxy;
        ProxyConfiguration proxyConfiguration = null;
        if (proxy != null) {
            proxyConfiguration = new ProxyConfiguration();
            proxyConfiguration.host = proxy.name;
            proxyConfiguration.port = proxy.port;
            proxyConfiguration.username = proxy.getUserName();
            proxyConfiguration.password = proxy.getPassword();
        }

        CredentialsConfig preferredResolver = server.getDeployerCredentialsConfig();
        ArtifactoryDependenciesClient dependenciesClient = server.createArtifactoryDependenciesClient(
                preferredResolver.provideUsername(), preferredResolver.providePassword(), proxyConfiguration,
                null);

        Log log = new JenkinsBuildInfoLog(listener);
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
