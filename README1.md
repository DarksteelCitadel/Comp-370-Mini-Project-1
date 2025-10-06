
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
  - Listens on **port 6000**.  
  - Handles client requests and heartbeats.

- **BackupServer** (`BackupServer.java`)    
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
## Running the system
Running the System

1.Open a terminal, navigate to the project folder:
```bash
cd ~/Comp-370-Mini-Project-1
```
2. Make all scripts executable:
```bash
chmod +x scripts/*.sh
```
3. Start the system:
```bash
./scripts/run.sh
```
This will start: Monitor (port 7100), PrimaryServer (Server 1, port 6000), BackupServer 2 (port 6001), BackupServer 3 (port 6002), AdminInterface (CLI)

In this terminal, you will mostly see log messages from Monitor and servers.

4. Open a new terminal window for AdminInterface:
```bash
cd ~/Comp-370-Mini-Project-1
java -cp out AdminInterface
```
5. Open another terminal to simulate delays in heartbeat:
```bash
cd ~/Comp-370-Mini-Project-1
./scripts/delay-heartbeat.sh 2
```
This delays Backup Server 2’s heartbeat for 5 seconds, then resumes automatically.
Replace 2 with 3 to delay Backup Server 3 instead.

6. Open another terminal to simulate a primary server failure:
```bash
cd ~/Comp-370-Mini-Project-1
./scripts/kill-primary.sh
```
This kills the current primary server. 

## Testing Scenarios 

After running ./scripts/run.sh, the system components start in the following order:
Monitor (port 7100)
PrimaryServer (Server 1, port 6000)
BackupServer 2 (port 6001)
BackupServer 3 (port 6002)
AdminInterface (CLI for commands)

You can then test different failure and recovery scenarios using separate terminal windows for each of these actions:

1. Admin Interface Terminal:
 ```bash
java -cp out AdminInterface
```
Commands: status, failover, exit.

3. Simulate Primary Crash:
```bash
./scripts/kill-primary.sh
```
Monitor detects the primary failure.
BackupServer 2 (or 3) is promoted to primary automatically.
Clients reconnect and continue sending requests successfully.

3.Simulate Backup Crash:

Stop a backup server manually or with kill command.
Monitor continues receiving primary heartbeats.
No failover occurs; primary continues serving clients normally.

4.Simultaneous Failures:

Stop both the primary and all backups.
Monitor detects no alive backup servers and logs:
```bash
No alive backup servers available for failover.
```
System enters an unavailable state; clients retry connections unsuccessfully.

5.Network Delay Simulation:
```bash
./scripts/delay-heartbeat.sh <backup-id>
```
Delays the heartbeat from the specified backup server (2 or 3).
Monitor continues receiving heartbeats from the primary.
No false failover is triggered.
Clients still receive valid responses from the primary server.

6.System Recovery:

Restart stopped servers manually.
Monitor re-registers the servers, and heartbeats resume.
Clients can reconnect to the primary and continue sending requests successfully.

## Scripts


```bash
./scripts/run.sh - # compile and run all components

kill-primary.sh – kills the current primary server:
./scripts/kill-primary.sh

delay-heartbeat.sh – delays heartbeat for a backup server:
./scripts/delay-heartbeat.sh


