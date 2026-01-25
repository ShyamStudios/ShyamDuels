# 🎮 ShyamDuels

> **A premium 1v1 and team dueling plugin for Minecraft servers**

![Version](https://img.shields.io/badge/version-1.0--BETA-orange)
![Minecraft](https://img.shields.io/badge/Minecraft-1.21.x-brightgreen)
![Status](https://img.shields.io/badge/status-beta-yellow)

---

## ⚠️ Beta Notice

> [!CAUTION]
> **This plugin is currently in BETA stage.**
> 
> While core features are functional, you may encounter bugs or unexpected behavior.
> Please report any issues on our [GitHub Issues](../../issues) page.

---

## ✨ Features

### 🗡️ Duel System
- **1v1 Ranked Duels** - Compete against other players with ELO-based matchmaking
- **Party Duels** - Team up with friends for exciting team battles
- **Best-of-X Rounds** - Configurable round counts for competitive matches
- **Custom Kit System** - Create and manage multiple fighting kits

### 🏟️ Arena Management
- **Multi-Arena Support** - Set up unlimited arenas with custom boundaries
- **Arena-Kit Linking** - Assign specific kits to specific arenas
- **Build Mode** - Optional building during matches with whitelist support
- **Mob Spawning Control** - Prevent mob spawning in arenas

### ⚔️ Free For All (FFA)
- **FFA Arenas** - Endless PvP action in dedicated arenas
- **Kit Selection** - Players choose their kit before joining
- **Spawn Protection** - Brief invincibility on spawn/respawn

### 📊 Statistics & Ranking
- **ELO System** - Competitive ranking with gain/loss based on performance
- **30+ Ranks** - From Bronze V to Conqueror with unique colors
- **Kill/Death Tracking** - Comprehensive player statistics


### 🎨 Kit Editor
- **Drag & Drop Interface** - Easy inventory arrangement
- **Armor Trim Support** - VIP players can customize armor trims
- **Per-Player Layouts** - Each player can personalize kit layouts

### 👥 Party System
- **Party Creation** - Create and manage player parties
- **Party Queue** - Queue for matches as a team
- **Party vs Party** - Challenge other parties to duels

### 🎬 Spectator Mode
- **Match Spectating** - Watch ongoing duels in spectator mode
- **Boundary Enforcement** - Spectators stay within arena bounds

---

## 📋 All Commands

### Duel Commands
| Command | Description |
|---------|-------------|
| `/duel <player>` | Open kit selection GUI to duel a player |
| `/duel accept <player>` | Accept a duel request from a player |
| `/duel deny <player>` | Deny a duel request |
| `/duel spectate <player>` | Spectate a player's match |

### Queue Commands
| Command | Aliases | Description |
|---------|---------|-------------|
| `/queue` | `/play` | Open the queue GUI to join matchmaking |

### FFA Commands
| Command | Description |
|---------|-------------|
| `/ffa` | Open the FFA GUI to select an arena |
| `/ffa join <arena>` | Join a specific FFA arena |
| `/ffa leave` | Leave the current FFA match |

### Arena Commands (Admin)
| Command | Aliases | Description |
|---------|---------|-------------|
| `/arena create <name>` | | Create a new arena |
| `/arena delete <name>` | | Delete an arena |
| `/arena list` | | List all arenas |
| `/arena corner1 <arena>` | `/arena pos1` | Set first corner at your location |
| `/arena corner2 <arena>` | `/arena pos2` | Set second corner at your location |
| `/arena spawn1 <arena>` | `/arena setspawn1`, `/arena p1` | Set spawn point 1 |
| `/arena spawn2 <arena>` | `/arena setspawn2`, `/arena p2` | Set spawn point 2 |
| `/arena center <arena>` | `/arena setcenter` | Set the arena center point |
| `/arena addkit <arena> <kit>` | | Link a kit to the arena |
| `/arena build <arena> <true/false>` | | Enable/disable build mode |
| `/arena ffa <arena> <true/false>` | | Set arena as FFA type |
| `/arena tp <arena>` | `/arena teleport` | Teleport to arena center |

### Kit Commands (Admin)
| Command | Description |
|---------|-------------|
| `/kit create <name>` | Create a kit from your current inventory |
| `/kit delete <name>` | Delete a kit |
| `/kit list` | List all kits |
| `/kit load <name>` | Equip a kit to yourself |
| `/kit setinv <name>` | Update kit with your current inventory |
| `/kit seticon <name>` | Set kit icon to held item |
| `/kit setitem <name>` | Set kit display item to held item |
| `/kit allowblock <name>` | Add held block to build whitelist |
| `/kit removeblock <name>` | Remove held block from build whitelist |

### Kit Editor Command
| Command | Aliases | Description |
|---------|---------|-------------|
| `/kiteditor [kit]` | `/editkit` | Open the Kit Editor GUI |

### Party Commands
| Command | Aliases | Description |
|---------|---------|-------------|
| `/party` | `/p` | Show party help |
| `/party create` | | Create a new party |
| `/party invite <player>` | | Invite a player to your party |
| `/party accept` | | Accept a pending invite |
| `/party deny` | | Deny a pending invite |
| `/party leave` | | Leave your current party |
| `/party disband` | | Disband your party (owner only) |
| `/party kick <player>` | | Kick a player from party |
| `/party public` | | Make party joinable by anyone |
| `/party private` | | Make party invite-only |
| `/party join <player>` | | Join a public party |
| `/party chat` | | Toggle party chat mode |
| `/party settings` | | Open party settings GUI |
| `/party info` | | View party information |
| `/party duel` | | Open party duel GUI |
| `/party duel accept` | | Accept a party duel invite |

### Party Chat Command
| Command | Aliases | Description |
|---------|---------|-------------|
| `/partychat` | `/pc` | Toggle party chat mode |

### Utility Commands
| Command | Aliases | Description |
|---------|---------|-------------|
| `/leavefight` | `/leave`, `/spawn` | Leave current fight or teleport to lobby |
| `/spectate [player]` | | Spectate active matches |
| `/shyamduels` | `/sd` | Main plugin admin command |
| `/shyamduels reload` | | Reload plugin configuration |

---

## 🔑 Permissions

| Permission | Description |
|------------|-------------|
| `shyamduels.admin` | Access to all admin commands |
| `shyamduels.vip` | VIP features (armor trims, etc.) |

---

## 📦 Installation

1. Download the latest JAR from [Releases](../../releases)
2. Place the JAR in your server's `plugins/` folder
3. Restart your server
4. Configure the plugin in `plugins/ShyamDuels/config.yml`
5. Set up arenas and kits using the in-game commands (see guides below)

**Dependencies:**
- FastAsyncWorldEdit (Required)
- PlaceholderAPI (Optional)

---

## 🏟️ Arena Setup Guide

Follow these steps to create a fully functional arena:

### Step 1: Create the Arena
```
/arena create MyArena
```

### Step 2: Set Arena Boundaries
Stand at one corner of your arena and run:
```
/arena corner1 MyArena
```
Then go to the opposite diagonal corner and run:
```
/arena corner2 MyArena
```
> **Note:** This also automatically saves the arena schematic for reset functionality!

### Step 3: Set Spawn Points
Go to where Player 1 should spawn and run:
```
/arena spawn1 MyArena
```
Go to where Player 2 should spawn and run:
```
/arena spawn2 MyArena
```
> **Tip:** Make sure spawns face each other for the best experience!

### Step 4: Set Center (Optional but Recommended)
Stand at the center of your arena and run:
```
/arena center MyArena
```
This is used for spectator teleportation and boundary checks.

### Step 5: Link a Kit (Required)
Link which kits can be used in this arena:
```
/arena addkit MyArena NoDebuff
```
You can link multiple kits to one arena by running this command multiple times.

### Step 6: Enable Build Mode (Optional)
If you want players to be able to place/break blocks during fights:
```
/arena build MyArena true
```
To disable: `/arena build MyArena false`

### Making an FFA Arena
To convert a regular arena into an FFA arena:
```
/arena ffa MyArena true
```

---

## ⚔️ Kit Setup Guide

### Step 1: Prepare Your Inventory
Equip yourself with the items you want in the kit:
- Put items in your hotbar and inventory slots
- Wear the armor you want players to have
- Apply any potion effects you want players to receive
- Hold items in your offhand if needed

### Step 2: Create the Kit
Run the following command to create a kit from your current inventory:
```
/kit create NoDebuff
```
This automatically saves your:
- All inventory items
- Armor pieces
- Active potion effects
- Default icon (Diamond Sword)

### Step 3: Customize the Icon (Optional)
Hold the item you want as the kit's icon and run:
```
/kit seticon NoDebuff
```

### Step 4: Allow Building Blocks (Optional)
If your kit includes blocks (like for SkyWars or Bridge):
1. Hold the block in your hand
2. Run: `/kit allowblock NoDebuff`
3. Repeat for each block type you want to allow

### Updating a Kit
To update an existing kit with your current inventory:
```
/kit setinv NoDebuff
```

### Kit Editor (For Players)
Players can customize their personal kit layout using:
```
/kiteditor NoDebuff
```
This opens a GUI where they can:
- Rearrange items with drag & drop
- Customize armor placement
- **VIP Feature:** Right-click armor to open Armor Trim Editor
- Save or reset their personal layout

---

## 🆚 FFA Setup Guide

### Step 1: Create and Configure an Arena
Follow the Arena Setup Guide above, then:
```
/arena ffa MyFFAArena true
```

### Step 2: Add Kits to the FFA Arena
```
/arena addkit MyFFAArena NoDebuff
```

### Step 3: Players Can Now Join!
- Using command: `/ffa join MyFFAArena`
- Using GUI: `/ffa` → Select arena

---

## 👥 Party System Guide

### For Party Owner

1. **Create a Party:**
   ```
   /party create
   ```

2. **Invite Players:**
   ```
   /party invite PlayerName
   ```

3. **Manage Your Party:**
   - `/party kick PlayerName` - Remove a member
   - `/party public` - Allow anyone to join
   - `/party private` - Invite-only mode
   - `/party settings` - Open settings GUI
   - `/party disband` - Disband the party

4. **Start Party Duels:**
   ```
   /party duel
   ```
   This opens a GUI to challenge other parties.

### For Party Members

- `/party accept` - Accept a pending invite
- `/party deny` - Deny an invite
- `/party leave` - Leave the party
- `/party chat` - Toggle party chat
- `/party info` - View party details

---

## ⚙️ Configuration Files

| File | Description |
|------|-------------|
| `config.yml` | Main settings (ELO, spawn location, etc.) |
| `messages.yml` | All plugin messages (customizable) |
| `gui.yml` | GUI layouts and items |
| `scoreboards.yml` | Scoreboard configuration |

---

## 🐛 Bug Reports

Found a bug? Please report it on our [GitHub Issues](../../issues) page with:
- Detailed description of the issue
- Steps to reproduce
- Server version and plugin version
- Any relevant error logs

---

## 📜 License

**⚠️ All Rights Reserved - See [LICENSE.md](LICENSE.md) for full terms**

### Summary:
- ❌ **NO Redistribution** - You cannot share this plugin
- ❌ **NO Reselling** - You cannot sell or resell this plugin
- ❌ **NO Modification & Redistribution** - You cannot modify and share
- ✅ **Personal Use** - Use on your own servers is permitted

**Violators will face legal action.**

---

<div align="center">

**Made with ❤️ for the Minecraft PvP community**

</div>
