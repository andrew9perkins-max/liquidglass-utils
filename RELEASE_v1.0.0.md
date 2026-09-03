# Release v1.0.0 (Draft)

This is a draft release placeholder for Liquid Glass Utils v1.0.0 (branch: feature/weather).

What this release will contain (current build)
- Core mod scaffold (Fabric Loom, Java 21)
- Dashboard UI skeleton with Liquid Glass placeholder panels
- Ping module (live sampling from PlayerListEntry latency, ring buffer, session persistence)
- Weather module (Open‑Meteo integration, periodic fetch, current + short forecast)
- Keybind: Backslash (\) opens Dashboard
- Simple Ping HUD (compact pill)

How to produce and attach the JAR artifact
1. Trigger the GitHub Actions workflow that builds the mod for branch `feature/weather`:
   - Repo → Actions → "Build LiquidGlass Utils" workflow → Run workflow → choose branch `feature/weather` → Run
2. Wait for the workflow run to complete (check the run logs for success).
3. In the workflow run summary, download the artifact named `liquidglass-utils-jar`.
4. Attach the artifact to this Release (Edit release → upload asset) or upload it yourself to the Releases page.

If you prefer, instead of manually attaching the artifact I can:
- Wait for you to tell me the workflow run ID or the artifact download URL and then I'll attach it to the Release for you (I cannot auto-download the artifact from Actions without additional permissions).

Notes / Next steps after you’ve downloaded the JAR
- Put the jar into your .minecraft/mods folder alongside Fabric API for 1.21.11 and launch with Fabric Loader 0.19.5.
- Press Backslash (\\) to open the Dashboard; Weather fetch will run on join and periodically based on config (config/liquidglass.json).

If you want me to change the Release title/notes, or add binary assets once the CI finishes, tell me and I’ll take care of it.
