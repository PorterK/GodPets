# GodPets

A Paper plugin for **Minecraft 26.3**: anything with a name tag can't die. Name a mob and it's in god mode, immune to players, mobs, fire, lava, falling, drowning, explosions, everything. Named mobs can't be set on fire either.

## Download

**[⬇ GodPets.jar (latest build)](https://github.com/PorterK/GodPets/releases/download/latest/GodPets.jar)**

Drop it into your server's `plugins/` folder and restart. You need Paper 26.3 running on Java 25.

## Rules

- Any entity with a custom name is protected (name tag, anvil-renamed spawn egg, `/data`, etc.).
- Players are never protected.
- `/kill` and the void still work by default, so you can get rid of a named mob if you need to. Turn either off in the config to make named mobs completely unkillable.

## Commands

| Command | Permission | Default |
|---|---|---|
| `/godpets` shows the current settings | — | everyone |
| `/godpets reload` reloads config.yml | `godpets.admin` | op |

`/gp` works as well as `/godpets`.

## Configuration

`plugins/GodPets/config.yml`:

```yaml
allow-kill-command: true   # /kill still removes named mobs
allow-void: true           # named mobs still die in the void
disabled-worlds: []        # worlds where names don't protect
```

## Building

```sh
./gradlew build    # needs JDK 25 → build/libs/GodPets-<version>.jar
```

GitHub Actions builds every push and tests it on a live Paper 26.3 server. Pushes to `main` update the **latest** release. Pushing a tag like `v1.0.1` creates a versioned release.
