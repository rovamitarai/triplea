package org.triplea.http.client.github.issues;

import org.triplea.http.client.HttpClient;
import org.triplea.http.client.ServiceClient;
import org.triplea.http.client.error.report.create.ErrorReport;
import org.triplea.http.client.github.issues.create.CreateIssueResponse;

/** Creates an http client that can be used to interact with Github 'issues'. */
public final class GithubIssueClientFactory {
  private GithubIssueClientFactory() {}

  /** Creates an http client that can post a new github issue. */
  public static ServiceClient<ErrorReport, CreateIssueResponse> newGithubIssueCreator(
      final IssueClientParams issueClientParams) {
    return new ServiceClient<>(
        new HttpClient<>(
            GithubIssueClient.class,
            (client, request) -> client.newIssue(issueClientParams, request),
            issueClientParams.getUri()));
  }
}
