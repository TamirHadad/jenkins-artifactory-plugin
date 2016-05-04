package org.jfrog.hudson.pipeline.buildinfo;

import com.google.common.collect.Lists;
import hudson.model.Run;
import hudson.model.TaskListener;
import org.apache.commons.lang.StringUtils;
import org.jfrog.build.api.*;
import org.jfrog.build.api.builder.BuildInfoBuilder;
import org.jfrog.build.api.builder.ModuleBuilder;
import org.jfrog.build.extractor.clientConfiguration.client.ArtifactoryBuildInfoClient;
import org.jfrog.hudson.AbstractBuildInfoDeployer;
import org.jfrog.hudson.pipeline.PipelineUtils;
import org.jfrog.hudson.util.ExtractorUtils;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Map;

/**
 * Created by romang on 4/25/16.
 */
public class PipelineBuildinfoDeployer extends AbstractBuildInfoDeployer {

    private final Run build;
    private final Map<String, String> sysVars;
    private final Map<String, String> envVars;
    private ArtifactoryPipelineConfigurator configurator;
    private Build buildInfo;

    public PipelineBuildinfoDeployer(ArtifactoryPipelineConfigurator configurator, ArtifactoryBuildInfoClient client
            , Run build, TaskListener listener, PipelineBuildinfo pipelineBuildinfo) throws IOException, InterruptedException, NoSuchAlgorithmException {
        super(configurator, build, listener, client);
        this.configurator = configurator;
        this.build = build;
        this.envVars = pipelineBuildinfo.getEnvVars();
        this.sysVars = pipelineBuildinfo.getSysVars();
        this.buildInfo = createBuildInfo("Pipeline", "Pipeline", BuildType.GENERIC);

        createDeployDetailsAndAddToBuildInfo(pipelineBuildinfo.getDeployedArtifacts(), pipelineBuildinfo.getPublishedDependencies());
        buildInfo.setBuildDependencies(pipelineBuildinfo.getBuildDependencies());

        if (StringUtils.isNotEmpty(pipelineBuildinfo.getName())) {
            buildInfo.setName(pipelineBuildinfo.getName());
        }

        if (StringUtils.isNotEmpty(pipelineBuildinfo.getNumber())) {
            buildInfo.setNumber(pipelineBuildinfo.getNumber());
        } else {
            buildInfo.setNumber(PipelineUtils.getBuildNumber(buildInfo.getNumber()));
        }
    }

    public void deploy() throws IOException {
        String url = configurator.getArtifactoryServer().getUrl() + "/api/build";
        listener.getLogger().println("Deploying build info to: " + url);
        client.sendBuildInfo(buildInfo);
    }

    private void createDeployDetailsAndAddToBuildInfo(List<Artifact> deployedArtifacts,
                                                      List<Dependency> publishedDependencies) throws IOException, NoSuchAlgorithmException {
        ModuleBuilder moduleBuilder = new ModuleBuilder()
                .id(ExtractorUtils.sanitizeBuildName(build.getParent().getDisplayName()) + ":" + build.getNumber())
                .artifacts(deployedArtifacts);
        moduleBuilder.dependencies(publishedDependencies);
        buildInfo.setModules(Lists.newArrayList(moduleBuilder.build()));
    }

    /**
     * Adding environment and system variables to build info.
     *
     * @param builder
     */
    @Override
    protected void addBuildInfoProperties(BuildInfoBuilder builder) {
        if (envVars != null) {
            for (Map.Entry<String, String> entry : envVars.entrySet()) {
                builder.addProperty(BuildInfoProperties.BUILD_INFO_ENVIRONMENT_PREFIX + entry.getKey(), entry.getValue());
            }
        }

        if (sysVars != null) {
            for (Map.Entry<String, String> entry : sysVars.entrySet()) {
                builder.addProperty(entry.getKey(), entry.getValue());
            }
        }
    }
}
