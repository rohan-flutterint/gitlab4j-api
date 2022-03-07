package org.gitlab4j.api;

import java.util.Arrays;

import org.testcontainers.containers.GenericContainer;
import org.testcontainers.utility.DockerImageName;

/**
 * Container definition for testing purpose.
 * @author jabberwock
 */
public class GitLabContainer extends GenericContainer<GitLabContainer> {

	private static final String IMAGE_NAME = "gitlab/gitlab-ce";
	private static final DockerImageName DEFAULT_IMAGE_NAME = DockerImageName.parse(IMAGE_NAME);

	private int port = 80;
	private String version;

	public GitLabContainer(final String version) {
		this(DockerImageName.parse(IMAGE_NAME + ":" + version));
		this.version = version;
	}

	private GitLabContainer(final DockerImageName dockerImageName) {
        super(dockerImageName);
        dockerImageName.assertCompatibleWith(DEFAULT_IMAGE_NAME);
        withEnv("GITLAB_OMNIBUS_CONFIG", "gitlab_rails['initial_root_password']=\"password\";gitlab_rails['lfs_enabled']=false;");
    }

	public GitLabContainer withGitLabPort(int port) {
		setPortBindings(Arrays.asList(String.valueOf(port) + ":80"));
		withExposedPorts(port);
		this.port = port;
		return this;
	}

	public int getPort() {
		return port;
	}

	public String getVersion() {
		return version;
	}

}
