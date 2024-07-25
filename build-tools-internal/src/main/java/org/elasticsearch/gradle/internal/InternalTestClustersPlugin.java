/*
 * Copyright Elasticsearch B.V. and/or licensed to Elasticsearch B.V. under one
 * or more contributor license agreements. Licensed under the Elastic License
 * 2.0 and the Server Side Public License, v 1; you may not use this file except
 * in compliance with, at your election, the Elastic License 2.0 or the Server
 * Side Public License, v 1.
 */

package org.elasticsearch.gradle.internal;

import org.elasticsearch.gradle.VersionProperties;
import org.elasticsearch.gradle.internal.info.BuildParams;
import org.elasticsearch.gradle.testclusters.TestClustersPlugin;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.provider.ProviderFactory;

import javax.inject.Inject;

public class InternalTestClustersPlugin implements Plugin<Project> {

    private ProviderFactory providerFactory;

    @Inject
    public InternalTestClustersPlugin(ProviderFactory providerFactory) {
        this.providerFactory = providerFactory;
    }

    @Override
    public void apply(Project project) {
        //TODO lb run 03a： jdk下载
        project.getPlugins().apply(InternalDistributionDownloadPlugin.class);
        //TODO lb run 03b： 分片服务
        project.getRootProject().getPluginManager().apply(InternalReaperPlugin.class);
        //TODO lb run 03c： 测试集群
        TestClustersPlugin testClustersPlugin = project.getPlugins().apply(TestClustersPlugin.class);
        //TODO lb run 03d： java运行环境
        testClustersPlugin.setRuntimeJava(providerFactory.provider(() -> BuildParams.getRuntimeJavaHome()));
        //TODO lb run 03e： 是否是发布版本
        testClustersPlugin.setIsReleasedVersion(
            version -> (version.equals(VersionProperties.getElasticsearchVersion()) && BuildParams.isSnapshotBuild() == false)
                || BuildParams.getBwcVersions().unreleasedInfo(version) == null
        );
    }

}
