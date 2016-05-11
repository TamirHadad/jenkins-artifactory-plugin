package org.jfrog.hudson.pipeline;

import hudson.FilePath;
import hudson.Launcher;
import hudson.model.Run;
import hudson.model.TaskListener;
import hudson.util.StreamTaskListener;
import org.jenkinsci.plugins.workflow.steps.StepContext;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by romang on 4/21/16.
 */
public class ArtifactoryPipelineServer implements Serializable {
    private String serverName;
    private String url;
    private String username;
    private String password;
    private boolean bypassProxy;

    private transient Launcher launcher;
    private transient FilePath ws;
    private transient Run build;
    private transient StepContext context;
    private transient TaskListener listener;

    public ArtifactoryPipelineServer(String artifactoryServerName, String url, String username, String password) {
        serverName = artifactoryServerName;
        this.url = url;
        this.username = username;
        this.password = password;
    }

    public ArtifactoryPipelineServer(String url, String username, String password) {
        this.url = url;
        this.username = username;
        this.password = password;
    }

    public PipelineBuildInfo download(String json) throws Exception {
        return download(json, null);
    }

    public PipelineBuildInfo download(String json, PipelineBuildInfo providedBuildInfo) throws Exception {
        TaskListener listener = getBuildListener();
        PipelineBuildInfo build = new GenericDownloadExecution(PipelineUtils.prepareArtifactoryServer(null, this), listener, this.build, this.ws, providedBuildInfo).execution(json);
        build.captureVariables(context);
        return build;
    }

    public PipelineBuildInfo upload(String json) throws Exception {
        TaskListener listener = getBuildListener();
        PipelineBuildInfo buildInfo = new GenericUploadExecution(PipelineUtils.prepareArtifactoryServer(null, this), listener, this.build, this.ws, null, context).execution(json);
        buildInfo.captureVariables(context);
        return buildInfo;
    }

    public PipelineBuildInfo upload(String json, PipelineBuildInfo providedBuildInfo) throws Exception {
        TaskListener listener = getBuildListener();
        PipelineBuildInfo buildInfo = new GenericUploadExecution(PipelineUtils.prepareArtifactoryServer(null, this), listener, this.build, this.ws, providedBuildInfo, context).execution(json);
        buildInfo.captureVariables(context);
        return buildInfo;
    }

    public void publishBuildInfo(PipelineBuildInfo buildInfo) throws Exception {
        TaskListener listener = getBuildListener();
        PipelineBuildInfoDeployer deployer = buildInfo.createDeployer(build, listener, PipelineUtils.prepareArtifactoryServer(null, this));
        deployer.deploy();
    }

    private TaskListener getBuildListener() {
        TaskListener listener;
        try {
            Field listenerField = build.getClass().getDeclaredField("listener");
            listenerField.setAccessible(true);
            listener = (StreamTaskListener) listenerField.get(build);
        } catch (NoSuchFieldException e) {
            Logger.getLogger(ArtifactoryPipelineServer.class.getName()).log(Level.FINE, "couldn't create listener");
            listener = this.listener;
        } catch (IllegalAccessException e) {
            Logger.getLogger(ArtifactoryPipelineServer.class.getName()).log(Level.FINE, "couldn't create listener");
            listener = this.listener;
        }
        return listener;
    }

    public String getServerName() {
        return serverName;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setBypassProxy(boolean bypassProxy) {
        this.bypassProxy = bypassProxy;
    }

    public boolean isBypassProxy() {
        return bypassProxy;
    }

    public TaskListener getListener() {
        return listener;
    }

    public void setListener(TaskListener listener) {
        this.listener = listener;
    }

    public Launcher getLauncher() {
        return launcher;
    }

    public void setLauncher(Launcher launcher) {
        this.launcher = launcher;
    }

    public FilePath getWs() {
        return ws;
    }

    public void setWs(FilePath ws) {
        this.ws = ws;
    }

    public Run getBuild() {
        return build;
    }

    public void setBuild(Run build) {
        this.build = build;
    }

    public StepContext getContext() {
        return context;
    }

    public void setContext(StepContext context) {
        this.context = context;
    }

}
