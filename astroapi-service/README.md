# AstroAPI Service

A Spring Boot 3.5 service that calls OpenRouter to generate Western astrology reports. Reactive stack (WebFlux). Java 21.

## Requirements

- JDK 21
- Maven 3.9+
- Network access to OpenRouter API
- Secrets via environment variables

## Configuration

These properties are read from env vars:

- `openrouter.api.key` ← `OPENROUTER_API_KEY`
- `openrouter.api.url` ← `OPENROUTER_API_URL` (example: `https://openrouter.ai/api/v1/chat/completions`)
- `openrouter.model` ← `OPENROUTER_MODEL` (example: `openrouter/auto` or a specific model id)

`src/main/resources/application.properties`:

```
openrouter.api.key=${OPENROUTER_API_KEY}
openrouter.api.url=${OPENROUTER_API_URL}
openrouter.model=${OPENROUTER_MODEL}
```

## Run (dev)

### Linux or macOS
```bash
export OPENROUTER_API_KEY=your-secret
export OPENROUTER_API_URL=https://openrouter.ai/api/v1/chat/completions
export OPENROUTER_MODEL=openrouter/auto
mvn spring-boot:run
```

Inline:
```bash
OPENROUTER_API_KEY=your-secret OPENROUTER_API_URL=https://openrouter.ai/api/v1/chat/completions OPENROUTER_MODEL=openrouter/auto mvn spring-boot:run
```

### Windows CMD
```cmd
set OPENROUTER_API_KEY=your-secret
set OPENROUTER_API_URL=https://openrouter.ai/api/v1/chat/completions
set OPENROUTER_MODEL=openrouter/auto
mvn spring-boot:run
```

Inline:
```cmd
set OPENROUTER_API_KEY=your-secret && set OPENROUTER_API_URL=https://openrouter.ai/api/v1/chat/completions && set OPENROUTER_MODEL=openrouter/auto && mvn spring-boot:run
```

### Windows PowerShell
```powershell
$env:OPENROUTER_API_KEY="your-secret"
$env:OPENROUTER_API_URL="https://openrouter.ai/api/v1/chat/completions"
$env:OPENROUTER_MODEL="openrouter/auto"
mvn spring-boot:run
```

### Pass via Maven arguments
```bash
mvn spring-boot:run -Dspring-boot.run.arguments="--openrouter.api.key=your-secret --openrouter.api.url=https://openrouter.ai/api/v1/chat/completions --openrouter.model=openrouter/auto"
```

## Build a JAR

```bash
mvn clean package
```

Run the packaged JAR:

**Linux/macOS**
```bash
export OPENROUTER_API_KEY=your-secret
export OPENROUTER_API_URL=https://openrouter.ai/api/v1/chat/completions
export OPENROUTER_MODEL=openrouter/auto
java -jar target/astroapi-service-0.0.1-SNAPSHOT.jar
```

**Windows CMD**
```cmd
set OPENROUTER_API_KEY=your-secret
set OPENROUTER_API_URL=https://openrouter.ai/api/v1/chat/completions
set OPENROUTER_MODEL=openrouter/auto
java -jar target\astroapi-service-0.0.1-SNAPSHOT.jar
```

## API

Base path:
```
/api/astrology
```

Predict (Western):
```
POST /api/astrology/predict/western
Content-Type: application/json
```

Request body (`AstrologyRequest`):
```json
{
  "birthDate": "1979-12-11",
  "birthTime": "07:00",
  "address": "Athens, GR",
  "yearOfForecast": 2025,
  "sex": "F"
}
```

Response is a streaming Flux of `AstrologyResponse` (SSE-friendly).

Example cURL:
```bash
curl -N -X POST http://localhost:8080/api/astrology/predict/western \
  -H "Content-Type: application/json" \
  -d '{"birthDate":"1979-12-11","birthTime":"07:00","address":"Athens, GR","yearOfForecast":2025,"sex":"F"}'
```

## Tests and Coverage

Run tests:
```bash
mvn -q -Dspring.main.web-application-type=none test
```

Open JaCoCo report:
```
target/site/jacoco/index.html
```

If you see an error about `reactor.test.StepVerifier`, either add:
```xml
<dependency>
  <groupId>io.projectreactor</groupId>
  <artifactId>reactor-test</artifactId>
  <scope>test</scope>
</dependency>
```
or assert on `Flux` using `blockFirst()` in tests.

## Environment variable notes

- Do not commit secrets.
- Windows: use `set` per session or System Environment Variables for permanence.
- Linux/macOS: add `export` lines to your shell rc file if you want persistence.

## Troubleshooting

- 401 or 403 from OpenRouter
  - Key invalid or missing. Check `OPENROUTER_API_KEY`.
- `Could not resolve placeholder 'openrouter.*'`
  - Missing env var or Maven `--openrouter.*` arguments.
- Port collision
  - Run with `--server.port=8081` or set `SERVER_PORT` env.

## Security

- Keep secrets out of `application.properties` and `pom.xml`.
- Use per-developer env files or CI secret stores. Rotate keys regularly.
