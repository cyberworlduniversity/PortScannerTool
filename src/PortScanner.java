import java.net.*;
import java.io.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * PortScanner - A multithreaded network port scanning tool
 * Author: hemanathan115
 * Description: Scans TCP ports on a target system and reports open ports with services.
 */
public class PortScanner {

    // Common port-to-service mappings
    private static final Map<Integer, String> SERVICE_MAP = new LinkedHashMap<>();

    static {
        SERVICE_MAP.put(21,   "FTP");
        SERVICE_MAP.put(22,   "SSH");
        SERVICE_MAP.put(23,   "Telnet");
        SERVICE_MAP.put(25,   "SMTP");
        SERVICE_MAP.put(53,   "DNS");
        SERVICE_MAP.put(67,   "DHCP");
        SERVICE_MAP.put(80,   "HTTP");
        SERVICE_MAP.put(110,  "POP3");
        SERVICE_MAP.put(119,  "NNTP");
        SERVICE_MAP.put(123,  "NTP");
        SERVICE_MAP.put(135,  "MS-RPC");
        SERVICE_MAP.put(139,  "NetBIOS");
        SERVICE_MAP.put(143,  "IMAP");
        SERVICE_MAP.put(161,  "SNMP");
        SERVICE_MAP.put(194,  "IRC");
        SERVICE_MAP.put(389,  "LDAP");
        SERVICE_MAP.put(443,  "HTTPS");
        SERVICE_MAP.put(445,  "SMB");
        SERVICE_MAP.put(465,  "SMTPS");
        SERVICE_MAP.put(514,  "Syslog");
        SERVICE_MAP.put(587,  "SMTP Submission");
        SERVICE_MAP.put(636,  "LDAPS");
        SERVICE_MAP.put(993,  "IMAPS");
        SERVICE_MAP.put(995,  "POP3S");
        SERVICE_MAP.put(1433, "MS-SQL");
        SERVICE_MAP.put(1521, "Oracle DB");
        SERVICE_MAP.put(3306, "MySQL");
        SERVICE_MAP.put(3389, "RDP");
        SERVICE_MAP.put(5432, "PostgreSQL");
        SERVICE_MAP.put(5900, "VNC");
        SERVICE_MAP.put(6379, "Redis");
        SERVICE_MAP.put(8080, "HTTP-Alt");
        SERVICE_MAP.put(8443, "HTTPS-Alt");
        SERVICE_MAP.put(27017,"MongoDB");
    }

    private final String targetHost;
    private final int startPort;
    private final int endPort;
    private final int timeout;
    private final int threadCount;
    private final List<Integer> openPorts = Collections.synchronizedList(new ArrayList<>());
    private final AtomicInteger scannedCount = new AtomicInteger(0);

    public PortScanner(String targetHost, int startPort, int endPort, int timeout, int threadCount) {
        if (targetHost == null || targetHost.trim().isEmpty()) {
            throw new IllegalArgumentException("Target host must not be empty.");
        }
        if (startPort < 1 || endPort > 65535 || startPort > endPort) {
            throw new IllegalArgumentException("Port range must satisfy 1 <= start <= end <= 65535.");
        }
        if (timeout < 1 || timeout > 60000) {
            throw new IllegalArgumentException("Timeout must be between 1 and 60000 milliseconds.");
        }
        if (threadCount < 1 || threadCount > 200) {
            throw new IllegalArgumentException("Thread count must be between 1 and 200.");
        }
        this.targetHost   = targetHost.trim();
        this.startPort    = startPort;
        this.endPort      = endPort;
        this.timeout      = timeout;
        this.threadCount  = threadCount;
    }

    /**
     * Checks if a single port is open using a TCP socket connection.
     */
    private boolean isPortOpen(int port) {
        try (Socket socket = new Socket()) {
            socket.connect(new InetSocketAddress(targetHost, port), timeout);
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    /**
     * Returns the service name for a given port, or "Unknown" if not mapped.
     */
    private String getServiceName(int port) {
        return SERVICE_MAP.getOrDefault(port, "Unknown");
    }

    /**
     * Runs the scan using a thread pool.
     */
    public void scan() throws InterruptedException, UnknownHostException {
        // Resolve the host first
        InetAddress address = InetAddress.getByName(targetHost);
        String resolvedIP   = address.getHostAddress();

        int totalPorts = endPort - startPort + 1;

        printBanner(resolvedIP, totalPorts);

        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        long startTime = System.currentTimeMillis();

        for (int port = startPort; port <= endPort; port++) {
            final int p = port;
            executor.submit(() -> {
                if (isPortOpen(p)) {
                    openPorts.add(p);
                    System.out.printf("  [OPEN]  Port %-6d  Service: %s%n", p, getServiceName(p));
                }
                int done = scannedCount.incrementAndGet();
                if (done % 5000 == 0) {
                    System.out.printf("  ... scanned %,d / %,d ports%n", done, totalPorts);
                }
            });
        }

        executor.shutdown();
        try {
            executor.awaitTermination(Long.MAX_VALUE, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            executor.shutdownNow();
            Thread.currentThread().interrupt();
            throw e;
        }

        long elapsed = System.currentTimeMillis() - startTime;
        printReport(resolvedIP, elapsed);
    }

    private void printBanner(String resolvedIP, int totalPorts) {
        System.out.println();
        System.out.println("╔══════════════════════════════════════════════════════╗");
        System.out.println("║           JAVA PORT SCANNER TOOL v1.0               ║");
        System.out.println("║              by hemanathan115                        ║");
        System.out.println("╚══════════════════════════════════════════════════════╝");
        System.out.println();
        System.out.println("  Target Host : " + targetHost);
        System.out.println("  Resolved IP : " + resolvedIP);
        System.out.printf ("  Port Range  : %d – %d  (%,d ports)%n", startPort, endPort, totalPorts);
        System.out.println("  Timeout     : " + timeout + " ms per port");
        System.out.println("  Threads     : " + threadCount);
        System.out.println();
        System.out.println("  Scanning... (open ports appear below)");
        System.out.println("  ─────────────────────────────────────────────────────");
    }

    private void printReport(String resolvedIP, long elapsedMs) {
        Collections.sort(openPorts);
        System.out.println();
        System.out.println("  ═════════════════════════════════════════════════════");
        System.out.println("                     SCAN REPORT                      ");
        System.out.println("  ═════════════════════════════════════════════════════");
        System.out.println("  Host        : " + targetHost + " (" + resolvedIP + ")");
        System.out.printf ("  Port Range  : %d – %d%n", startPort, endPort);
        System.out.printf ("  Scan Time   : %.2f seconds%n", elapsedMs / 1000.0);
        System.out.println("  Open Ports  : " + openPorts.size());
        System.out.println();

        if (openPorts.isEmpty()) {
            System.out.println("  No open ports found in the specified range.");
        } else {
            System.out.printf("  %-8s %-20s %s%n", "PORT", "SERVICE", "STATUS");
            System.out.println("  ──────────────────────────────────────────────────");
            for (int port : openPorts) {
                System.out.printf("  %-8d %-20s %s%n", port, getServiceName(port), "OPEN");
            }
        }

        System.out.println();
        System.out.println("  ═════════════════════════════════════════════════════");
        System.out.println("  Scan completed.");
        System.out.println();
    }

    // ─── main ────────────────────────────────────────────────────────────────

    public static void main(String[] args) {
        try (Scanner input = new Scanner(System.in)) {
            System.out.println("╔══════════════════════════════════════════════════════╗");
            System.out.println("║           JAVA PORT SCANNER TOOL v1.0               ║");
            System.out.println("║              by hemanathan115                        ║");
            System.out.println("╚══════════════════════════════════════════════════════╝");
            System.out.println();

            System.out.print("  Enter target IP or hostname: ");
            String host = input.nextLine().trim();

            System.out.println();
            System.out.println("  Select scan mode:");
            System.out.println("    1. Quick Scan   (ports 1 – 1024)");
            System.out.println("    2. Common Ports (ports 1 – 10000)");
            System.out.println("    3. Full Scan    (ports 1 – 65535)");
            System.out.println("    4. Custom Range");
            System.out.print("  Choice [1-4]: ");
            String modeChoice = input.nextLine().trim();

            int startPort = 1, endPort = 1024;
            switch (modeChoice) {
                case "1": startPort = 1;  endPort = 1024;  break;
                case "2": startPort = 1;  endPort = 10000; break;
                case "3": startPort = 1;  endPort = 65535; break;
                case "4":
                    System.out.print("  Start port: ");
                    startPort = Integer.parseInt(input.nextLine().trim());
                    System.out.print("  End port  : ");
                    endPort = Integer.parseInt(input.nextLine().trim());
                    break;
                default:
                    System.out.println("  Invalid choice. Using Quick Scan.");
            }

            System.out.print("  Number of threads [default 200]: ");
            String threadInput = input.nextLine().trim();
            int threads = threadInput.isEmpty() ? 200 : Integer.parseInt(threadInput);

            System.out.print("  Timeout per port in ms [default 500]: ");
            String timeoutInput = input.nextLine().trim();
            int timeout = timeoutInput.isEmpty() ? 500 : Integer.parseInt(timeoutInput);

            PortScanner scanner = new PortScanner(host, startPort, endPort, timeout, threads);
            scanner.scan();
        } catch (UnknownHostException e) {
            System.err.println("  ERROR: Cannot resolve target host. Check the address and try again.");
        } catch (InterruptedException e) {
            System.err.println("  ERROR: Scan interrupted.");
            Thread.currentThread().interrupt();
        } catch (IllegalArgumentException e) {
            System.err.println("  ERROR: " + e.getMessage());
        }
    }
}
