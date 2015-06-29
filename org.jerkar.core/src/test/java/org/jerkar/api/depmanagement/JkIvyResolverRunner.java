package org.jerkar.api.depmanagement;



import static org.jerkar.api.depmanagement.JkScopedDependencyTest.COMPILE;

import java.util.HashSet;
import java.util.Set;

public class JkIvyResolverRunner {

	public static void main(String[] args) {
		spring();
		//jogl();
		//joglWithSource();
		//testPublishIvy();
	}

	public static void spring() {
		final JkRepos repos = JkRepos.mavenCentral().andMavenCentral();
		final JkDependencies deps = JkDependencies.builder()
				.on("org.springframework", "spring-jdbc", "3.0.+")
				.mapScope(COMPILE).to("compile", "default")
				.and(JkScopedDependencyTest.PROVIDED).to("provided")
				.build();
		final Set<JkArtifact> artifacts = IvyResolver.of(repos).resolveAnonymous(deps, COMPILE, JkResolutionParameters.of());
		for (final JkArtifact artifact : artifacts) {
			System.out.println(artifact);
		}
		System.out.println(deps.resolvedWithArtifacts(artifacts));
	}

	public static void jogl() {
		final JkRepos repos = JkRepos.mavenCentral().andMavenCentral();
		final JkDependencies deps = JkDependencies.builder()
				.on("org.apache.cocoon.all:cocoon-all:3.0.0-alpha-3").scope(COMPILE).build();
		final Set<JkArtifact> artifacts = IvyResolver.of(repos).resolveAnonymous(deps, COMPILE, JkResolutionParameters.of(defaultMapping()));
		for (final JkArtifact artifact : artifacts) {
			System.out.println(artifact);
		}
		System.out.println("--- " + artifacts.size());
	}

	private static JkScopeMapping defaultMapping() {
		return JkScopeMapping.of(COMPILE).to("compile", "archive(master)")
				.and(JkScopedDependencyTest.RUNTIME).to("runtime", "archive(master)");
	}

	public static void joglWithSource() {
		final JkRepos repos = JkRepos.mavenCentral();
		final JkDependencies deps = JkDependencies.builder()
				.on("org.apache.cocoon.all:cocoon-all:3.0.0-alpha-3").scope(COMPILE).build();
		final InternalDepResolver jkIvyResolver = IvyResolver.of(repos);
		final Set<JkArtifact> artifacts = jkIvyResolver.resolveAnonymous(deps, COMPILE, JkResolutionParameters.of(defaultMapping()));
		final Set<JkVersionedModule> modules = new HashSet<JkVersionedModule>();
		for (final JkArtifact artifact : artifacts) {
			modules.add(artifact.versionedModule());
		}
		final JkAttachedArtifacts result = jkIvyResolver.getArtifacts(modules, JkScope.of("sources"), JkScope.of("javadoc"), JkScope.of("noexist"));
		System.out.println(result);
		final Set<JkArtifact> artifactSet = result.getArtifacts(JkModuleId.of("org.apache.wicket", "wicket-ioc"), JkScope.of("sources"));
		System.out.println(artifactSet);
		final Set<JkArtifact> javadocArtifactSet = result.getArtifacts(JkModuleId.of("org.apache.wicket", "wicket-ioc"), JkScope.of("javadoc"));
		System.out.println(javadocArtifactSet);
		final Set<JkArtifact> noExistArtifactSet = result.getArtifacts(JkModuleId.of("org.apache.wicket", "wicket-ioc"), JkScope.of("noexist"));
		System.out.println(noExistArtifactSet);
	}




}