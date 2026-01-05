# The Arena

A simple multiplayer arena server written in Java. Players connect over TCP, choose a name, and move around a 10x10 grid using `w`, `a`, `s`, `d` commands. A boss monster also roams the map and chases players.

## Features

- TCP server that accepts multiple player connections
- 10x10 grid map displayed as ASCII
- Player movement with `w` (up), `a` (left), `s` (down), `d` (right)
- Boss monster that automatically moves toward the nearest player
- Thread-safe map and player management

## Project layout

- `ArenaServer.java` - Main server loop, accepts connections and starts handlers
- `ClientHandler.java` - Handles a single player's I/O and commands
- `ArenaMap.java` - Holds player positions and renders the ASCII map
- `BossMonster.java` - Autonomous enemy that chases players
- `Main.java` - Launches the `ArenaServer`

## How to compile

Open a terminal in the `The_Arena` folder and run:

```powershell
javac *.java
```

## How to run

Start the server (defaults to port 8080):

```powershell
java The_Arena.Main
```

Then connect using `telnet` or `nc` from the same machine. Example with PowerShell's `telnet` (enable feature if needed) or on systems with `nc` (netcat):

```powershell
# Windows: (you may need to enable Telnet client)
telnet localhost 8080

# Linux/macOS:
nc localhost 8080
```

When you connect you'll be prompted for a name. After joining you'll see the ASCII map and can type moves (`w`, `a`, `s`, `d`). Type `quit` to leave.

## Map / Coordinates

- The map is 10x10 (0..9 for both x and y)
- `(0,0)` is the top-left corner
- Rows are numbered on the left and columns at the top
- Players are represented by the first letter of their name; the boss is `B`

## Controls

- `w` — move up
- `a` — move left
- `s` — move down
- `d` — move right
- `quit` — disconnect from the server

## Concurrency and Safety

- `ArenaMap` uses a `ConcurrentHashMap` and synchronized methods to protect map operations.
- `ArenaServer` uses a `CopyOnWriteArrayList` to track connected client handlers safely.

## Known Behaviors & Notes

- Boss spawns at server start and is added as a player named `BOSS`.
- The boss movement runs on a background thread and moves every 2 seconds.
- Player spawn is currently at `(0, 0)` for all players (can be randomized).
- Movement checks boundaries so players cannot leave the map.

## Troubleshooting

- If you cannot connect, verify the server printed `Arena Server is running on port 8080`.
- Use `netstat -ano | Select-String ":8080"` (PowerShell) to ensure port 8080 is listening.
- If map displays incorrectly, ensure your client does not buffer newline characters; pressing Enter after commands is required.

## Suggested Improvements

- Randomize spawn positions and ensure no collision on join
- Add per-player IDs and richer views (nearby players only)
- Implement persistent player state or reconnect support
- Improve CLI UX (clear screen, redraw map in place)
- Add graceful shutdown for server and boss thread

## License

For educational use.