# 🔍 Java Port Scanner Tool


## Get this repository

Clone the current project (this repository's canonical GitHub URL), then follow the installation and usage steps below.

```bash
git clone https://github.com/cyberworlduniversity/PortScannerTool.git
cd PortScannerTool
```

**Author:** hemanathan115  
**Version:** 1.0  
**Language:** Java (JDK 8+)

---

## 📌 Description

A fast, multithreaded **TCP Port Scanner** built in Java. It connects to ports on a target system, detects which ports are open, identifies running services, and generates a clean scan report — all from the command line.

---

## 🚀 Features

| Feature | Description |
|---|---|
| ⚡ Fast Scanning | Uses multithreading (up to 200+ concurrent threads) |
| 🎯 Target Input | Accepts IP address or hostname |
| 🔓 Open Port Detection | TCP socket-based detection |
| 🗂️ Service Identification | Maps 30+ well-known ports to service names |
| 📊 Scan Report | Summary report after scan completes |
| 🛠️ Custom Range | Scan any port range from 1 to 65535 |

---

## 🧠 Java Concepts Used

- **`Socket`** — TCP connection to each port
- **`InetAddress`** — Hostname resolution
- **`InetSocketAddress`** — Socket connect with timeout
- **`ExecutorService` / `Executors`** — Thread pool management
- **`Multithreading`** — Concurrent port scanning for speed
- **`AtomicInteger`** — Thread-safe progress counter
- **`Collections.synchronizedList`** — Thread-safe results list

---

## 📁 Project Structure

```
PortScannerTool/
├── src/
│   └── PortScanner.java      ← Main source file
├── README.md                 ← This file
└── run.sh / run.bat          ← Build & run scripts
```

---

## ⚙️ Setup & Usage

### Prerequisites
- Java Development Kit (JDK) 8 or higher
- Command line / terminal

### Compile

```bash
cd src
javac PortScanner.java
```

### Run

```bash
java PortScanner
```

### Or use the provided scripts:

**Linux/Mac:**
```bash
chmod +x run.sh
./run.sh
```

**Windows:**
```bat
run.bat
```

---

## 🖥️ Example Output

```
╔══════════════════════════════════════════════════════╗
║           JAVA PORT SCANNER TOOL v1.0               ║
║              by hemanathan115                        ║
╚══════════════════════════════════════════════════════╝

  Enter target IP or hostname: 192.168.1.1

  Select scan mode:
    1. Quick Scan   (ports 1 – 1024)
    2. Common Ports (ports 1 – 10000)
    3. Full Scan    (ports 1 – 65535)
    4. Custom Range
  Choice [1-4]: 1

  Target Host : 192.168.1.1
  Resolved IP : 192.168.1.1
  Port Range  : 1 – 1024  (1,024 ports)
  Threads     : 200

  [OPEN]  Port 22      Service: SSH
  [OPEN]  Port 80      Service: HTTP
  [OPEN]  Port 443     Service: HTTPS

  ═════════════════════════════════════════════════════
                     SCAN REPORT
  ═════════════════════════════════════════════════════
  Host        : 192.168.1.1
  Port Range  : 1 – 1024
  Scan Time   : 3.45 seconds
  Open Ports  : 3

  PORT     SERVICE              STATUS
  ──────────────────────────────────────────────────
  22       SSH                  OPEN
  80       HTTP                 OPEN
  443      HTTPS                OPEN
```

---

## 🗂️ Scan Modes

| Mode | Port Range | Use Case |
|---|---|---|
| Quick Scan | 1 – 1024 | Standard well-known ports |
| Common Ports | 1 – 10000 | Extended common services |
| Full Scan | 1 – 65535 | Complete scan (slower) |
| Custom Range | Any | Targeted scanning |

---

## ⚠️ Legal Disclaimer

> This tool is intended for **educational purposes** and **authorized security testing only**.  
> Scanning systems without explicit permission is **illegal** and **unethical**.  
> Always get proper authorization before scanning any network or system.

---

## 📜 License

This project is open source and free to use for educational purposes.
