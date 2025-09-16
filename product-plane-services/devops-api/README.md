# DevOps API Usage

## Executor Secrets

The DevOps API supports passing secrets to executors through HTTP headers when creating or starting activities and tasks. This allows executors to authenticate with external services and access protected resources during task execution.

### Header Format

Secrets are passed using HTTP headers following this naming convention:

```
x-odm-<ExecutorName>-executor-secret-<SecretType>
```

Where:
- `<ExecutorName>` is the name of the executor that will receive the secret
- `<SecretType>` is the type of secret being passed (e.g., `gitlab-token`, `azure-token`, `aws-key`)

### Examples

#### GitLab Token for Dummy Executor
```http
x-odm-dummy-executor-secret-gitlab-token: glpat-xxxxxxxxxxxxxxxxxxxx
```

#### Azure Token for Azure Executor
```http
x-odm-azure-executor-secret-azure-token: eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiJ9...
```

#### Multiple Secrets for Same Executor
```http
x-odm-dummy-executor-secret-gitlab-token: glpat-xxxxxxxxxxxxxxxxxxxx
x-odm-dummy-executor-secret-azure-token: eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiJ9...
```

### Usage

#### Activity Creation with Secrets

When creating an activity that you want to start immediately (`startAfterCreation=true`), you can include executor secrets in the request headers:

```http
POST /api/v1/pp/devops/activities?startAfterCreation=true
Content-Type: application/json
x-odm-dummy-executor-secret-gitlab-token: glpat-xxxxxxxxxxxxxxxxxxxx
x-odm-dummy-executor-secret-azure-token: eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiJ9...

{
  "dataProductId": "f350cab5-992b-32f7-9c90-79bca1bf10be",
  "dataProductVersion": "1.0.0",
  "stage": "test"
}
```

**Note**: If you're not starting the activity immediately (`startAfterCreation=false` or omitted), there's no need to pass secrets during creation. Secrets should only be provided when the activity will actually start, either during creation with `startAfterCreation=true` or when calling the start endpoint separately.

#### Activity Start with Secrets

When starting an existing activity, you can provide secrets:

```http
POST /api/v1/pp/devops/activities/{id}/start
x-odm-dummy-executor-secret-gitlab-token: glpat-xxxxxxxxxxxxxxxxxxxx
```

#### Individual Task Start with Secrets

When starting an individual task, you can provide secrets specific to that task's executor:

```http
POST /api/v1/pp/devops/tasks/{id}/start
x-odm-dummy-executor-secret-gitlab-token: glpat-xxxxxxxxxxxxxxxxxxxx
```

### Secret Processing

The DevOps API processes secrets as follows:

1. **Extraction**: Headers matching the `x-odm-<ExecutorName>-executor-secret-<SecretType>` pattern are extracted
2. **Transformation**: Secret headers are transformed to `x-odm-<SecretType>` format for executor consumption
3. **Storage**: Secrets are stored in a thread-safe cache keyed by executor name and activity ID
4. **Forwarding**: When tasks are executed, secrets are automatically forwarded to the appropriate executors
5. **Cleanup**: Secrets are automatically cleaned up when activities complete or expire

### Security Considerations

- Secrets are stored in memory only and are not persisted to the database
- Secrets automatically expire after 1 hour to prevent memory leaks
- Each activity's secrets are isolated and cannot access secrets from other activities
- Invalid or malformed secret headers are ignored without error

### Error Handling

- Missing or null headers are handled gracefully
- Invalid header formats are ignored
- Secret processing failures do not prevent activity or task execution
- Cache cleanup failures are logged but do not affect functionality

### Multiple Executors

When an activity contains tasks from multiple executors, you can provide secrets for each executor:

```http
POST /api/v1/pp/devops/activities
x-odm-gitlab-executor-secret-gitlab-token: glpat-xxxxxxxxxxxxxxxxxxxx
x-odm-azure-executor-secret-azure-token: eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiJ9...
x-odm-aws-executor-secret-aws-key: AKIAIOSFODNN7EXAMPLE
```

Each executor will receive only the secrets intended for it, ensuring proper isolation and security.
