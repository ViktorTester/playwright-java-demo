# Playwright Java Test Automation Framework

Production-oriented test automation framework based on Java 21, Playwright Java, JUnit 5, Maven and Allure.

The framework supports UI and API automated testing with separated layers for configuration, API clients, request builders, assertions, test data, page objects, reporting and CI execution.

## Tech stack

- Java 21
- Maven
- JUnit 5
- Playwright Java
- Jackson
- dotenv-java
- Allure Report
- GitHub Actions

## Project structure

```text
src/test/java
  api
    assertions      - API response validation, JSON path reader and API expectations
    client          - API client, request builder and API logging support
    constants       - API status codes and shared constants
    domains         - Domain-specific API entry points
    dto             - Request and response DTOs
    endpoints       - API endpoint definitions
  base              - Base classes for API and UI tests
  config            - Environment configuration
  extensions        - JUnit extensions, for example UI failure watcher
  helpers           - Reusable test helpers, for example auth token provider
  pages             - UI Page Objects
  testdata          - Centralized test data
  tests
    api             - API tests
    ui              - UI tests
  utils             - JSON and Allure attachment utilities
```

## Architecture overview

### API layer

```text
Test
  -> ApiContainer
    -> Domain API
      -> ApiClient
        -> RequestBuilder
          -> Playwright APIRequestContext
```

This keeps tests readable and hides low-level HTTP implementation details.

Example:

```java
APIResponse response = authenticatedApi.room
        .getRoom()
        .get();
```

### UI layer

```text
Test
  -> Page Object
    -> Playwright Page and Locator
```

Example:

```java
HomePage homePage = new HomePage(page);

homePage.open();
homePage.shouldBeOpened();
```

Tests should describe business intent. Locators and implementation details should live inside Page Objects.

## Environment configuration

Create a local `.env` file from the example:

```bash
cp .env.example .env
```

Example `.env.example`:

```env
BASE_URL=https://automationintesting.online
BASE_API_URL=https://automationintesting.online/api

ADMIN_USERNAME=admin
ADMIN_PASSWORD=password

BROWSER=chromium
HEADLESS=true
DEFAULT_TIMEOUT_MS=10000

API_TIMEOUT_MS=10000

LOG_SENSITIVE_DATA=false
```

Do not commit `.env` to Git.

Configuration priority:

```text
JVM system property
  -> environment variable
    -> .env file
      -> default value, if defined
```

Example overrides:

```bash
mvn clean test -Pui -DHEADLESS=false
mvn clean test -Pui -DBROWSER=firefox
mvn clean test -Papi -DLOG_SENSITIVE_DATA=true
```

## Prerequisites

Install:

- Java 21
- Maven
- Allure CLI

Check versions:

```bash
java -version
mvn -version
allure --version
```

Install Allure CLI on macOS:

```bash
brew install allure
```

## Install dependencies

```bash
mvn -B -DskipTests clean test
```

## Install Playwright browsers

```bash
mvn exec:java -e -Dexec.mainClass=com.microsoft.playwright.CLI -Dexec.args="install"
```

For CI/Linux with dependencies:

```bash
mvn exec:java -e -Dexec.mainClass=com.microsoft.playwright.CLI -Dexec.args="install --with-deps chromium"
```

## Test tags and Maven profiles

JUnit 5 tags are used to separate test execution.

Recommended tags:

```text
api
ui
smoke
regression
```

Recommended usage:

```java
@Tag(API)
class RoomApiTest extends BaseAuthenticatedApiTest {

    @Test
    @Tag(SMOKE)
    void shouldReturnRoomsList() {
        // smoke-level API check
    }

    @Test
    @Tag(REGRESSION)
    void shouldValidateRoomContract() {
        // regression-level API check
    }
}
```

Run all tests:

```bash
mvn clean test
```

Run API tests:

```bash
mvn clean test -Papi
```

Run UI tests:

```bash
mvn clean test -Pui
```

Run smoke tests:

```bash
mvn clean test -Psmoke
```

Run regression tests:

```bash
mvn clean test -Pregression
```

Run API smoke tests:

```bash
mvn clean test -Papi-smoke
```

Run UI smoke tests:

```bash
mvn clean test -Pui-smoke
```

## API assertions

Use `ApiResponseValidator` and `ApiExpect` for readable API checks.

Example:

```java
Map<String, Object> expectedBody = new HashMap<>();

expectedBody.put("rooms", ApiExpect.notEmptyList());
expectedBody.put("rooms[0].roomid", ApiExpect.exists());
expectedBody.put("rooms[0].roomName", ApiExpect.notBlank());
expectedBody.put("rooms[0].roomPrice", ApiExpect.greaterThan(0));

verifyApiResponse(response, 200, expectedBody);
```

Supported expectation helpers:

```java
ApiExpect.equalsTo(value)
ApiExpect.exists()
ApiExpect.notNull()
ApiExpect.notBlank()
ApiExpect.notEmptyList()
ApiExpect.notEmptyMap()
ApiExpect.contains(value)
ApiExpect.matchesRegex(regex)
ApiExpect.greaterThan(number)
ApiExpect.lessThan(number)
```

JSON path examples:

```text
rooms
rooms[0].roomid
transactions.first[0].id
transactions[0].items[0].id
```

If a path does not exist, the assertion fails with a clear message.

## API request bodies and DTOs

Prefer DTOs for stable API contracts.

Example request DTO:

```java
public record AuthRequest(
        String username,
        String password
) {
}
```

Example usage:

```java
APIResponse response = api.auth
        .postAuthLogin()
        .body(adminCredentials())
        .post();
```

Avoid raw `Map.of(...)` for stable request contracts unless the payload is very small or temporary.

## API logging

Request and response logging is handled inside `RequestBuilder`.

Sensitive values are masked by default:

```env
LOG_SENSITIVE_DATA=false
```

For local debugging only:

```bash
mvn clean test -Papi -DLOG_SENSITIVE_DATA=true
```

Production rule:

```text
CI/shared logs:
  sensitive data masked

Local debugging:
  sensitive data can be enabled explicitly
```

## Allure reporting

The project writes Allure results to:

```text
target/allure-results
```

Run tests:

```bash
mvn clean test -Psmoke
```

Open Allure report locally:

```bash
allure serve target/allure-results
```

Generate static report:

```bash
allure generate target/allure-results -o target/allure-report --clean
allure open target/allure-report
```

Do not use `mvn allure:serve` if the Maven Allure plugin has path resolution issues in the local environment.

## Allure annotations

Recommended test-level metadata:

```java
@Epic("API")
@Feature("Rooms")
@Owner("Viktor")
@Tag(API)
class RoomApiSmokeTest extends BaseAuthenticatedApiTest {
}
```

Recommended Page Object steps:

```java
@Step("Open home page")
public void open() {
    open(baseUrl());
}

@Step("Verify home page is opened")
public void shouldBeOpened() {
    assertThat(page).hasTitle(PAGE_TITLE);
    assertThat(header).containsText(EXPECTED_HEADER);
}
```

Keep Allure steps business-level. Avoid adding a separate step for every low-level locator action.

## CI/CD

GitHub Actions workflow:

```text
.github/workflows/tests.yml
```

The pipeline should:

```text
checkout repository
setup Java 21
cache Maven dependencies
install Playwright browsers
run smoke tests
upload Surefire reports
upload Allure results
```

Required GitHub secrets:

```text
ADMIN_USERNAME
ADMIN_PASSWORD
```

Smoke command used in CI:

```bash
mvn -B test -Psmoke
```

## GitHub Actions example

```yaml
name: Automated Tests

on:
  push:
    branches:
      - main
      - master
  pull_request:
    branches:
      - main
      - master
  workflow_dispatch:

jobs:
  smoke-tests:
    name: Smoke tests
    runs-on: ubuntu-latest

    env:
      BASE_URL: https://automationintesting.online
      BASE_API_URL: https://automationintesting.online/api
      ADMIN_USERNAME: ${{ secrets.ADMIN_USERNAME }}
      ADMIN_PASSWORD: ${{ secrets.ADMIN_PASSWORD }}
      BROWSER: chromium
      HEADLESS: true
      DEFAULT_TIMEOUT_MS: 10000
      API_TIMEOUT_MS: 10000
      LOG_SENSITIVE_DATA: false

    steps:
      - name: Checkout repository
        uses: actions/checkout@v4

      - name: Set up Java 21
        uses: actions/setup-java@v4
        with:
          distribution: temurin
          java-version: 21
          cache: maven

      - name: Install dependencies
        run: mvn -B -DskipTests clean test

      - name: Install Playwright browsers
        run: mvn exec:java -e -Dexec.mainClass=com.microsoft.playwright.CLI -Dexec.args="install --with-deps chromium"

      - name: Run smoke tests
        run: mvn -B test -Psmoke

      - name: Upload Surefire reports
        if: always()
        uses: actions/upload-artifact@v4
        with:
          name: surefire-reports
          path: target/surefire-reports

      - name: Upload Allure results
        if: always()
        uses: actions/upload-artifact@v4
        with:
          name: allure-results
          path: target/allure-results
```

## Repository hygiene

The following files and directories must not be committed:

```text
.env
.env.*
.idea/
target/
.allure/
allure-report/
allure-results/
playwright-report/
test-results/
.DS_Store
._*
```

The following files should be committed:

```text
.env.example
.gitignore
pom.xml
src/
README.md
.github/workflows/tests.yml
```

## Recommended local workflow

```bash
cp .env.example .env
mvn -B -DskipTests clean test
mvn exec:java -e -Dexec.mainClass=com.microsoft.playwright.CLI -Dexec.args="install"
mvn clean test -Psmoke
allure serve target/allure-results
```

## Test design guidelines

- Use risk-based testing.
- Keep smoke tests fast and stable.
- Keep regression tests deeper and more exhaustive.
- Avoid hard waits.
- Prefer Playwright auto-waits and stable locators.
- Keep raw locators inside Page Objects.
- Keep API request construction inside domain API classes and `RequestBuilder`.
- Prefer DTOs and centralized test data for stable contracts.
- Ensure every failure gives useful evidence: status code, response body, screenshot or Allure attachment.
