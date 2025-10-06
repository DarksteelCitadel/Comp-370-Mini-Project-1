# Server Redundancy Management System Simulation

This project simulates a **primary-backup redundancy system** in Java, with a monitor process that detects failures, promotes backups, and logs events.  
It also includes an **admin interface** for manual commands, log inspection, and failover testing.

---

## Components

- **Monitor** (`Monitor.java`)  
  - Runs on **port 7100**.  
  - Tracks all servers, listens for heartbeats, and promotes backups when the primary fails.  
  - Responds to admin commands (e.g., `GET_PRIMARY`).

- **PrimaryServer** (`PrimaryServer.java`)  
  - Example: `java PrimaryServer 1`  
  - Listens on **port 6000**.  
  - Handles client requests and heartbeats.

- **BackupServer** (`BackupServer.java`)  
  - Example: `java BackupServer 2`  
  - Listens on **port 9002** (Server 2), **6001** (Server 3).  
  - Sends heartbeats, can be promoted to primary.

- **AdminInterface** (`AdminInterface.java`)  
  - Interactive CLI for administrators.  
  - Commands: `status`, `failover`,`exit`.

- **Client** (`Client.java`)  
  - Can send test requests to whichever server is primary.

---

## Port Layout

| Process            | Port |
|--------------------|------|
| Monitor            | 7100 |
| Server 1 (Primary) | 6000 |
| Server 2 (Backup)  | 6001 |




## Running the System


```bash
# compile and run all components
./run.sh

# in another terminal, simulate failure
./kill-primary.sh
./delay-heartbeat.sh
