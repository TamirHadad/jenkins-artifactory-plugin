package org.jfrog.hudson.pipeline;

import hudson.FilePath;
import hudson.Launcher;
import hudson.model.Run;
import hudson.model.TaskListener;
import hudson.util.StreamTaskListener;
import jenkins.model.Jenkins;
import org.jenkinsci.plugins.workflow.steps.StepContext;
import org.jfrog.hudson.pipeline.buildinfo.PipelineBuildInfo;
import org.jfrog.hudson.pipeline.buildinfo.PipelineBuildinfoDeployer;

import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.logging.Logger;

import java.io.Serializable;

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
    private transient PrintStream logger = null;

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

    public PipelineBuildInfo download(String json) throws Exception{
        this.listener = new hudson.util.StreamTaskListener(new PrintStream(new FileOutputStream(build.getLogFile())));
        PipelineBuildInfo buildInfo = new GenericDownloadExecution(PipelineUtils.prepareArtifactoryServer(null, this)).execution(this.listener, this.launcher, this.build, this.ws, null, json);
        return buildInfo;
    }

    public PipelineBuildInfo upload(String json) throws Exception{
        PipelineBuildInfo buildInfo = new GenericUploadExecution(PipelineUtils.prepareArtifactoryServer(null, this)).execution(this.listener, this.launcher, this.build, this.ws, null, json, context);
        return buildInfo;
    }

    public void publishBuildInfo(PipelineBuildInfo buildInfo) throws Exception{
        PipelineBuildinfoDeployer deployer = buildInfo.createDeployer(build, listener, PipelineUtils.prepareArtifactoryServer(null, this));
        deployer.deploy();
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

    public void setLogger(PrintStream out) {
        this.logger = out;
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
