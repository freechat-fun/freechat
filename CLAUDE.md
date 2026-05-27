# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Overview
FreeChat is a Spring Boot (Java 25) backend + React/Vite frontend for an AI virtual character platform.
The backend uses LangChain4j for LLM orchestration; MySQL, Redis, and Milvus for persistence/cache/vector store.
The frontend (`freechat-web`) is React 19 + MUI + TypeScript, talking to the backend exclusively through the
generated `freechat-sdk` (the TypeScript build is consumed via `file:../freechat-sdk/typescript`).

## Build, lint, test (backend)
- Build all jars (skips tests): `make build`  (= `mvn -U clean package -DskipTests`)
- Format check (changed files only, ratchet from `origin/main`): `make lint`  (spotless:check, palantir-java-format)
- Apply formatting: `make format`
- Check/format ALL files regardless of git diff: `make lint-all` / `make format-all`
- Unit tests: `mvn test`  — for a single test: `mvn test -Dtest=ClassName#method -pl <module>`
- Integration tests (classes ending in `IT`, run by failsafe): `mvn verify`
- CI runs `mvn -B -U -T8C test` on JDK 25 and `mvn verify` with `-Dgib.referenceBranch=...` (see `.github/workflows/build.yml`)

## Build, lint, run (frontend — `freechat-web`)
- Dev server: `cd freechat-web && npm run dev`  (vite on 127.0.0.1:3000)
- Production build: `npm run build`  (tsc + vite build; emits `dist/`)
- Lint: `npm run lint`  (eslint, `--max-warnings 0`)
- Format: `npm run format`  (prettier)
- Preview built bundle: `npm run preview`
- The TypeScript SDK in `freechat-sdk/typescript` is a sibling dependency — if you edit OpenAPI or regenerate, run `npm install && npm run build` in `freechat-sdk/typescript` first.

## Full-stack build & local run
- Bundled web→backend build: `scripts/build.sh`  (builds web, copies `dist/assets,img` into `freechat-start/src/main/resources/static`, then `mvn package`).
- Web-only build piped into the backend's static resources: `scripts/build-web.sh`.
- Start dependencies (MySQL, Redis, Milvus, etcd, MinIO via Docker): `scripts/local-deps.sh`
- Full local stack in Docker (app + deps): `scripts/local-run.sh` (use `--help` for flags)
- Run from IDE: launch `freechat-start/src/main/java/fun/freechat/Application.java` with VM options:
  ```
  -Dspring.config.location=classpath:/application.yml
  -DAPP_HOME=local-data/freechat
  -Dspring.profiles.active=local
  ```

## SDK and DAL generation
- Regenerate `freechat-sdk/{java,python,typescript,bash}` from the running backend's OpenAPI doc:
  `scripts/sdk/gen-sdk.sh` (uses openapi-generator-cli; reads from `http://127.0.0.1:8080/public/openapi/v3/api-docs/g-all`, so the backend must be running locally).
- Regenerate MyBatis mappers/models from the MySQL schema: `scripts/mbg.sh` (spins up a throwaway MySQL container).
- OpenAPI i18n tooling lives under `scripts/openapi-i18n/`.

## Architecture (module dependency order, bottom → top)
- **freechat-common** — Cross-cutting annotations, exceptions, utilities. Foundation module.
- **freechat-dal** — Persistence layer. MyBatis (dynamic-sql) mappers + models under `fun.freechat.{mapper,model}`. SQL migrations and MyBatis generator config under `src/main/resources/{sql,mybatis-generator}`.
- **freechat-langchain4j** — LangChain4j integrations and wrappers (LLM, embedding, RAG building blocks).
- **freechat-service** — Business logic. Domain packages under `fun.freechat.service.{account, agent, ai, cache, character, chat, organization, plugin, prompt, rag, stats, config, common, enums, util}`.
- **freechat-start** — Spring Boot entrypoint + HTTP layer.
  - `Application.java` is the main class.
  - REST APIs (OpenAPI-exposed; consumed by SDKs) live in `fun.freechat.api.*Api` (`AccountApi`, `CharacterApi`, `ChatApi`, `PromptApi`, `RagApi`, `TtsApi`, etc.), with sub-packages `access/`, `admin/`, `biz/`, `dto/`, `util/`.
  - Non-SDK web controllers (`AuthController`, `DataController`, `DocController`, `ErrorController`, `MainController`) live in `fun.freechat.web`.
  - Config & access-control beans under `fun.freechat.{config,access}`.

Request flow: `freechat-start` (controller) → `freechat-service` (domain service) → `freechat-dal` (mapper) → MySQL/Redis/Milvus. LLM/RAG paths additionally call into `freechat-langchain4j`.

## Deployment / infra
- Helm chart at `configs/helm/` (`Chart.yaml`, `values.yaml`, plus `values-private.yaml` for site-specific overrides).
- Dockerfiles at `configs/docker/Dockerfile` (backend) and `Dockerfile_web` (frontend).
- Install/upgrade/uninstall lifecycle scripts: `scripts/install*.sh`, `scripts/upgrade*.sh`, `scripts/uninstall*.sh`, `scripts/restart*.sh`.
- See `README.md` for the deployment steps (Kubernetes via Helm or `scripts/local-run.sh` for local Docker).

## Code style notes
- Java is formatted with **palantir-java-format** via spotless. The `spotless` profile only checks files changed since `origin/main`; the `spotless-all` profile checks everything. CI does not enforce spotless — run `make lint` before pushing.
- Lombok is on the annotation processor path; `-parameters` is enabled in the compiler config.
