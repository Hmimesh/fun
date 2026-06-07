# Dungo — Java Swing Text RPG

> **A personal learning project by Ben Farjun (@Hmimesh)**

![GUI Screenshot](docs/gui-screenshot.png)
*(Screenshot coming soon — see the [How to Run](#how-to-run) section to try it yourself)*

---

## About

Dungo started as a terminal-based D&D-style combat game I wrote to practice Java.
It began with a single `Fight.java` file and a `Scanner`-based loop, then grew into a
Swing GUI prototype as I explored event-driven programming.

This is a **work-in-progress** learning project. It is not a polished commercial game.
The code reflects my progression as a developer — from procedural console scripts to
object-oriented design with Swing UI and background threads.

---

## Features

- **Turn-based combat** — d20 rolls, hit bonuses, AC, crits, and critical misses
- **4 player races** — Human, Elf, Dwarf, Gnome (each with small stat bonuses/penalties)
- **Procedurally generated enemies** — Slime → Wolf → Goblin → Dragon, scaling to player level
- **Boss encounters** — random boss promotions with Legendary tier for high-level bosses
- **11 feats** — Power, Tank, Luck, Dodge, Dicer, Rich, Alchemist, Fire Magic, Poison, Water Mage, Recovery
- **Weighted feat selection** — rarer feats appear less frequently in the pool
- **Inventory system** — potions (weak → super), revives, auto-heal when HP is low
- **Poison mechanic** — POISON feat enables a chance to apply 1d4 DOT to enemies
- **Elemental magic** — Fire (1d6) and Water (1d8) extra damage dice added to attacks
- **Final boss** — at level 9, a supercharged "Evil \<yourname\>" appears
- **Shop** — buy weapons, armour, and potions between fights *(terminal wiring WIP)*
- **Secret cheat codes** — enter `GOD` or `SEND HELP` as your character name

---

## GUI Features

- **Swing window** with a split-pane layout: scene panel on top, combat log below
- **Monospaced ASCII art** title screen
- **Colour themes** — 7 presets (Default, Green/Black, White/Black, Dark Gray, Black/White, Blue, Cyan)
- **Options button** — opens Help and Colours sub-menus at any time
- **Input field + Send button** — type commands or answers and press Enter/Send
- **Auto-scrolling log** — combat events stream in real time via a SwingWorker background thread
- **Thread-safe UI updates** — all GUI writes from the combat thread go through `SwingUtilities.invokeLater`

---

## Tech Stack

| Area | Choice |
|---|---|
| Language | Java (JDK 17+) |
| GUI | Java Swing |
| Background work | `SwingWorker` |
| Build | Plain `javac` (no Maven/Gradle — intentional for learning) |
| Version control | Git |

---

## Project Structure

```
fun/                          ← repository root
├── src/
│   └── com/hmimesh/game/     ← all Java source files
│       ├── Main.java         ← entry point
│       ├── GameEngine.java   ← game loop, combat logic, state transitions
│       ├── GameWindow.java   ← Swing GUI window
│       ├── GameState.java    ← enum: ENTER_NAME, READY, DOOR_CHOOSE, …
│       ├── Commands.java     ← in-game text commands (help, stats, inventory, …)
│       ├── Entity.java       ← abstract base for Player and Enemy
│       ├── Player.java       ← player character (stats, feats, inventory, combat)
│       ├── Enemy.java        ← enemy (procedural generation, boss logic, drops)
│       ├── Weapon.java       ← weapon generation and enhancement
│       ├── Armor.java        ← armour generation and enhancement
│       ├── Item.java         ← consumable items (potions, revive)
│       ├── Shop.java         ← shop purchase logic
│       ├── Feat.java         ← feat enum (11 passive abilities)
│       ├── Cheats.java       ← secret cheat-code enum
│       ├── Acolor.java       ← ANSI terminal colour codes
│       └── AcolorConfig.java ← detects whether the terminal supports ANSI colours
├── out/                      ← compiled .class files (git-ignored)
├── docs/
│   └── gui-screenshot.png    ← add your own screenshot here
├── run.sh                    ← Linux / macOS / WSL build-and-run script
├── run.bat                   ← Windows build-and-run script
├── .gitignore
└── README.md
```

---

## How to Compile

Requires **Java JDK 17 or higher** on your `PATH`.

```bash
mkdir -p out
javac -d out src/com/hmimesh/game/*.java
```

---

## How to Run

```bash
java -cp out com.hmimesh.game.Main
```

Or use the helper scripts:

**Linux / macOS / WSL:**
```bash
chmod +x run.sh
./run.sh
```

**Windows:**
```bat
run.bat
```

---

## How to Play

1. Enter your character name and press **Send** (or Enter).
2. Review your randomly rolled stats and race, then type `y` to begin.
3. Choose a door: `1` / `left` for an easier fight, `2` / `right` for a harder one.
4. Watch the combat log. The fight runs automatically — potions are used if HP drops low.
5. After winning, type `y` to continue or `n` to rest.
6. Reach **level 10** to beat the game.

---

## Commands

| Command | Aliases | Description |
|---|---|---|
| `help` | `h`, `Help` | Show command list |
| `inventory` | `i` | Show weapon, armour, items, gold |
| `stats` | `s` | Show AC, HP, dice pool |
| `feats` | `f` | Show acquired feats |
| `yes` / `no` | `y` / `n` | Answer prompts |
| `1` / `left` | `l` | Choose left door (easy) |
| `2` / `right` | `r` | Choose right door (hard) |
| `exit` | `quit` | Quit the game |

**Secret codes** (enter as character name):
- `GOD` — god mode (high stats, lots of gold)
- `SEND HELP` — challenge mode (very low stats)

---

## What I Learned

- Object-oriented design in Java (inheritance, abstract classes, enums with methods)
- Event-driven programming with Swing components and action listeners
- Using `SwingWorker` to run blocking logic off the EDT
- Thread safety: `SwingUtilities.invokeLater` for GUI updates from background threads
- Game state management with a typed `enum` instead of raw strings
- Weighted random selection for feat rarity
- Procedural content generation (enemy stats scaled to player level)
- Organising a growing project into multiple source files with a standard package structure

---

## Current Status

This is a **prototype / work in progress**.

What works in the GUI:
- Full name entry, character creation, race selection
- Door selection and combat loop
- Auto-heal and poison
- Level-up stat scaling
- Game-over screen with restart
- Colour themes via Options menu

Known limitations / planned work:
- Feat selection is stored but not yet wired to the GUI (shows in terminal only)
- Shop is implemented but not integrated into the GUI flow
- Elemental magic dice (fire/water) print to the console, not the GUI log
- No persistent save system

---

## Roadmap

- [ ] Wire feat selection into the Swing UI (dialog or log-based choice)
- [ ] Integrate the shop into the GUI post-battle flow
- [ ] Route all console `System.out` prints into the GUI log
- [ ] Add ASCII art scenes per enemy type
- [ ] Add a map / room system
- [ ] Add title screen animation
- [ ] Persistent save / load via file I/O

---

## Notes for Reviewers

- This is an **educational project**, not a production game.
- The code intentionally uses plain `javac` so I could learn the compilation pipeline before adding build tools.
- Some methods still call `System.out.println` — these are leftovers from the terminal era and are on the roadmap to be routed into the GUI log.
- The `Scanner`-based shop methods exist from v1 and are preserved as a historical artifact of the project's evolution.
- Feedback is welcome — feel free to open an issue or reach out on GitHub.

---

*Made with Java 17+ and a lot of learning · Ben Farjun (@Hmimesh on GitHub)*
