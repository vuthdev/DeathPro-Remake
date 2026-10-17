## Getting started
1. Fork the repo and clone your fork
2. ./gradlew build
3. Copy build/libs/*.jar to a test Paper server's plugins/ folder

## Workflow
1. Create a branch: feat/ghost-api or fix/corpse-expire
2. Commit using Conventional Commits (fix(ghost): ..., feat(api): ...)
3. Open a PR against main, linking the issue

## Guidelines
- Follow existing Kotlin style
- Test on a real server before submitting
- One feature or fix per PR