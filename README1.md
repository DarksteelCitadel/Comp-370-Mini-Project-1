/*
# Server Redundancy Management System Simulation

This project simulates a **primary-backup redundancy system** in Java, with a monitor process that detects failures, promotes backups, and logs events.
It also includes an **admin interface** for manual commands, log inspection, and failover testing.

---

## Components

- **Monitor** (`Monitor.java`)  
  - Runs on **port 7100**.  
  - Tracks all servers, listens for heartbeats, and promotes backups when the primary fails.  
  - Responds to admin commands (e.g., `status`, `failover`).

- **PrimaryServer** (`PrimaryServer.java`)  
  - Example: `java PrimaryServer 1 6000`  
  - Listens on **port 6000**.  
  - Handles client requests and heartbeats.

- **BackupServer** (`BackupServer.java`)  
  - Example: `java BackupServer 2 6001`  
  - Listens on **port 6001 (Server 2)** or **6002 (Server 3)**.  
  - Sends heartbeats and can be promoted to primary on failover.

- **AdminInterface** (`AdminInterface.java`)  
  - Interactive CLI for administrators.  
  - Commands: `status`, `failover`, `exit`.

- **Client** (`Client.java`)  
  - Can send test requests to whichever server is primary.

---

## Port Layout

| Process            | Port |
|--------------------|------|
| Monitor            | 7100 |
| Server 1 (Primary) | 6000 |
| Server 2 (Backup)  | 6001 |
| Server 3 (Backup)  | 6002 |

---
## Running the code
Open a terminal, navigate to the project folder:
cd ~/Comp-370-Mini-Project-1

Make scripts executable:
chmod +x scripts/*.sh

Run all components:
./scripts/run.sh
This will start:
Monitor (port 7100)
PrimaryServer (Server 1, port 6000)
BackupServer 2 (port 6001)
BackupServer 3 (port 6002)
AdminInterface (CLI for commands)
In this terminal, you will mostly see log messages from Monitor and servers.

Open a new terminal window for AdminInterface:
cd ~/Comp-370-Mini-Project-1
java -cp out AdminInterface

Open another terminal to simulate delays in heartbeat (for testing failover timing):
cd ~/Comp-370-Mini-Project-1
./scripts/delay-heartbeat.sh 2 (Replace 2 with 3 to delay Backup Server 3 instead.)

Open yet another terminal to simulate a primary server failure:
cd ~/Comp-370-Mini-Project-1
./scripts/kill-primary.sh

## Scripts

1. **run.sh** – compiles and runs all components:

```bash
./scripts/run.sh

kill-primary.sh – kills the current primary server:
./scripts/kill-primary.sh

delay-heartbeat.sh – delays heartbeat for a backup server:
./scripts/delay-heartbeat.sh

