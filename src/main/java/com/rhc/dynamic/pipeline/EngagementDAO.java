package com.rhc.dynamic.pipeline;

import com.rhc.automation.model.Application;
import com.rhc.automation.model.Engagement;
import com.rhc.automation.model.OpenShiftCluster;
import com.rhc.automation.model.Project;
import com.rhc.automation.model.Project.EnvironmentTypeEnum;

public final class EngagementDAO {

	public static Project getBuildProject(Engagement engagement) {
		for (OpenShiftCluster cluster : engagement.getOpenshiftClusters()) {
			for (Project project : cluster.getOpenshiftResources().getProjects()) {
				if (project.getEnvironmentType().equals( EnvironmentTypeEnum.BUILD )) {
					return project;
				}
			}
		}
		return null;
	}

	public static Application getApplicationFromBuildProject(Engagement engagement, String applicationName) {
		Project project = getBuildProject(engagement);
		for (Application app : project.getApps()) {
			if (app.getName() != null && app.getName().equals( applicationName )) {
				return app;
			}
		}
		return null;
	}

	/**
	 * There should only be a single build project declared across all clusters. It is invalid to have more than one.
	 */
	public static OpenShiftCluster getClusterWithBuildProject( Engagement engagement ){
		for (OpenShiftCluster cluster : engagement.getOpenshiftClusters() ){
			for ( Project project : cluster.getOpenshiftResources().getProjects() ){
				if ( project.getEnvironmentType().equals( EnvironmentTypeEnum.BUILD ) ){
					return cluster;
				}
			}
		}
		return null;
	}

	public static OpenShiftCluster getBuildCluster(Engagement engagement){
		return engagement.getOpenshiftClusters().get(0);
	}
}
