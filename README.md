# MSc Artificial Intelligence — Thesis Project

**Generated:** 2025-08-26 22:06:31

## What this project is
This repository accompanies the MSc thesis **“Αξιολόγηση Μεγάλων Γλωσσικών Μοντέλων (LLMs) στην Παραγωγή Υπολογιστικής Αστρολογικής Ανάλυσης”**. 
It delivers a reproducible pipeline that:
- Computes astronomical baselines with Swiss Ephemeris.
- Calls multiple LLMs through OpenRouter to generate structured JSON astrology reports.
- Serves a web UI to submit birth data and visualise results.
- Logs metrics, validates JSON and sanitises embedded SVG safely.


## High-level architecture
- **Python analysis**: Jupyter/Python notebooks and scripts compute ground-truth positions (Swiss Ephemeris) and perform evaluation.
- **Spring Boot backend**: REST API that orchestrates LLM calls via OpenRouter, streams responses (SSE), enforces JSON schema, and fixes `chart_svg` escaping.
- **Frontend app**: React-based UI to input data and render results.

## Repository structure (truncated)
```
.
  ephemeris/
    ephemeris_checker.ipynb
    input.csv
    sepl_18.se1
  astroapi-service/
    README.md
    makePrediction.http
    pom.xml
    astroapi-service/src/
      astroapi-service/src/main/
      astroapi-service/src/test/
  astrology-app/
    README.md
    package-lock.json
    package.json
    astrology-app/public/
      favicon.ico
      index.html
      logo192.png
      logo512.png
      manifest.json
      robots.txt
    astrology-app/src/
      App.css
      App.js
      App.test.js
      index.css
      index.js
      logo.svg
      reportWebVitals.js
      setupTests.js
      astrology-app/src/components/
        AstrologyForm.js
        AstrologyForm.success.test.js
        AstrologyForm.test.js
        AstrologyResult.js
        AstrologyResult.test.js
      astrology-app/src/utils/
        mappers.js
        mappers.test.js
        svgUtils.js
        svgUtils.test.js
```

### Detected modules and key manifests
- Maven projects:
  - `astroapi-service/pom.xml`
- Gradle projects: none detected
- Node projects:
  - `astrology-app/package.json`
- Python requirements: none detected

### Spring Boot services found
- `astroapi-service` — artifact `spring-boot-starter-parent`

### Frontend apps found
- `astrology-app` — name `astrology-app` (react)

### Python services with web servers
- None auto-detected; analysis runs via notebooks.

## How to run

### Prerequisites
- Java 21+
- Maven 3.8+
- Node.js 18+ and npm
- Python 3.10+ with Jupiter Notebook
- OpenRouter API key (set `OPENROUTER_API_KEY`)

### 1) Start the backend service (Spring Boot)

Please check relative README.md file

### 2) Start the frontend app
```bash
npm install
npm start   # for local development
```

### 4) Use the app
- Open the frontend URL printed by Vite/Next (commonly http://localhost:5173 or http://localhost:3000).
- Input birth data and submit.
- The frontend calls the backend to trigger OpenRouter LLMs and streams JSON back (SSE).
- Results render with sanitised `chart_svg` inside the UI.

## Contributing
- Use feature branches. 
- Run test suites before PRs.
- Keep 80 percent+ coverage in backend modules where applicable.
