package org.gitlab4j.api;

import java.time.Duration;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ExtensionContext.Namespace;
import org.junit.jupiter.api.extension.ExtensionContext.Store;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.utility.DockerImageName;

public class GitLabTestContainersExtension implements BeforeAllCallback {

	private static final List<String> GITLAB_TESTED_VERSIONS = Arrays.asList("12.9.2-ce.0", "13.0.14-ce.0");
	private static final Map<String, GitLabContainer> gitLabContainers = new HashMap<>();

	@Override
	public void beforeAll(ExtensionContext context) throws Exception {
		GITLAB_TESTED_VERSIONS.forEach(version -> {
			Version parsedVersion = new Version(version);
			int port = parsedVersion.getPort();

			GitLabContainer container = new GitLabContainer(version)
				.withGitLabPort(port)
				.waitingFor(Wait.forHealthcheck().withStartupTimeout(Duration.ofSeconds(300)));

			System.out.println("Starting GitLab container version " + version + " on http://localhost:" + port);
			container.start();
			System.out.println("GitLab container version " + version + " started on http://localhost:" + port);

			gitLabContainers.put(version, container);
		});

		getStore(context).put("gitLabContainers", gitLabContainers);
	}

	class Version {
		private Integer major;
		private Integer minor;
		private String fix;
		private Integer build;

		public Version(String version) {
			String[] splittedVersion = version.split("\\.");
			this.major = Integer.valueOf(splittedVersion[0]);
			this.minor = Integer.valueOf(splittedVersion[1]);
			this.fix = splittedVersion[2];
			this.build = Integer.valueOf(splittedVersion[3]);
		}

		public int getMajor() {
			return major;
		}

		public int getMinor() {
			return minor;
		}

		public String getFix() {
			return fix;
		}

		public int getBuild() {
			return build;
		}

		int getPort() {
			String port = "8" + major.toString() + minor.toString();
			return Integer.valueOf(port);
		}
	}

	private Store getStore(ExtensionContext context) {
		return context.getStore(Namespace.create(getClass(), context.getRequiredTestMethod()));
	}


}
