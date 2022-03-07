package org.gitlab4j.api;

import static java.util.Arrays.asList;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.Extension;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolver;
import org.junit.jupiter.api.extension.TestTemplateInvocationContext;
import org.junit.jupiter.api.extension.TestTemplateInvocationContextProvider;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.utility.DockerImageName;

public class GitLabInvocationContextProvider implements TestTemplateInvocationContextProvider {

	private static final List<String> GITLAB_TESTED_VERSIONS = Arrays.asList("12.9.2-ce.0");

	private final Map<String, GitLabContainer> gitLabContainers;

	public GitLabInvocationContextProvider() {
		gitLabContainers = new HashMap<>();
		GITLAB_TESTED_VERSIONS.forEach(version -> {

			GitLabContainer container = new GitLabContainer(version)
				.withGitLabPort(8090)
				.waitingFor(Wait.forHttp("/")
					.forStatusCode(200));

			gitLabContainers.put(version, container);
		});
	}

	@Override
	public boolean supportsTestTemplate(ExtensionContext context) {
		return true;
	}

	@Override
	public Stream<TestTemplateInvocationContext> provideTestTemplateInvocationContexts(ExtensionContext context) {
		return gitLabContainers.keySet().stream().map(this::invocationContext);
	}

	private TestTemplateInvocationContext invocationContext(final String gitLabVersion) {
        return new TestTemplateInvocationContext() {

            @Override
            public String getDisplayName(int invocationIndex) {
              return gitLabVersion;
             }

            @Override
            public List<Extension> getAdditionalExtensions() {
              final GenericContainer<?> gitLabContainer = gitLabContainers.get(gitLabVersion);
              return asList(
                  (BeforeEachCallback) context -> {
                	  System.out.println("Start container: " + System.currentTimeMillis() );
                	  gitLabContainer.start();
                	  System.out.println("Container started: " + System.currentTimeMillis() );
                  },
                  (AfterAllCallback)   context -> gitLabContainer.stop(),
                  new ParameterResolver() {
                      @Override
                      public boolean supportsParameter(ParameterContext parameterCtx, ExtensionContext extensionCtx) {
                          return parameterCtx.getParameter().getType().equals(GenericContainer.class);
                      }

                      @Override
                      public Object resolveParameter(ParameterContext parameterCtx, ExtensionContext extensionCtx) {
                          return gitLabContainer;
                      }
                  });
            }
        };
    }

}
