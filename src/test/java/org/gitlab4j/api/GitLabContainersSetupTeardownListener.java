package org.gitlab4j.api;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.stream.Collectors;

import org.junit.platform.launcher.LauncherSession;
import org.junit.platform.launcher.LauncherSessionListener;
import org.testcontainers.containers.wait.strategy.Wait;

public class GitLabContainersSetupTeardownListener implements LauncherSessionListener {

	private static final List<String> GITLAB_TESTED_VERSIONS = Arrays.asList("12.9.2-ce.0", "13.0.14-ce.0",
		"13.12.15-ce.0");
	private static final Map<String, GitLabContainer> gitLabContainers = new HashMap<>();

	@Override
	public void launcherSessionOpened(LauncherSession session) {
		if (gitLabContainers.isEmpty()) {

			List<String> gitLabVersions = new ArrayList<>();

			String gitLabVersionsFromProperties = System.getenv("GITLAB4JAPI_GITLAB_VERSIONS");

			if (gitLabVersionsFromProperties != null && !gitLabVersionsFromProperties.isEmpty()) {
				gitLabVersions.addAll(Arrays.stream(gitLabVersionsFromProperties.split(","))
					.filter(s -> s != null && !s.isEmpty())
					.collect(Collectors.toList()));
			} else {
				gitLabVersions.addAll(GITLAB_TESTED_VERSIONS);
			}

			gitLabVersions.forEach(version -> {
				Version parsedVersion = new Version(version);
				int port = parsedVersion.getPort();

				GitLabContainer container = new GitLabContainer(version)
					.withGitLabPort(port)
					.waitingFor(Wait.forHealthcheck().withStartupTimeout(Duration.ofSeconds(300)));

				System.out.println("GitLab container version " + container.getVersion()
					+ " started on http://localhost:" + container.getPort() + "/");

				gitLabContainers.put(version, container);
			});
		}
	}

	@Override
	public void launcherSessionClosed(LauncherSession session) {
		gitLabContainers.values().forEach(container -> {
			container.stop();
			System.out.println("GitLab container version " + container.getVersion() + " stopped");
		});

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
			String port = "8" + major.toString() + String.format("%02d", minor);
			return Integer.valueOf(port);
		}
	}
}
