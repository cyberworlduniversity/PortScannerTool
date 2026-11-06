public class PortScannerValidationTest {
    private static void expectInvalid(Runnable action, String description) {
        try {
            action.run();
            throw new AssertionError("Expected invalid configuration: " + description);
        } catch (IllegalArgumentException expected) {
            // Expected validation failure.
        }
    }

    public static void main(String[] args) {
        new PortScanner("example.com", 1, 65535, 500, 200);

        expectInvalid(() -> new PortScanner("", 1, 10, 500, 10), "empty host");
        expectInvalid(() -> new PortScanner("example.com", 0, 10, 500, 10), "port zero");
        expectInvalid(() -> new PortScanner("example.com", 1, 65536, 500, 10), "port above 65535");
        expectInvalid(() -> new PortScanner("example.com", 20, 10, 500, 10), "reversed range");
        expectInvalid(() -> new PortScanner("example.com", 1, 10, 0, 10), "zero timeout");
        expectInvalid(() -> new PortScanner("example.com", 1, 10, 60001, 10), "excessive timeout");
        expectInvalid(() -> new PortScanner("example.com", 1, 10, 500, 0), "zero threads");
        expectInvalid(() -> new PortScanner("example.com", 1, 10, 500, 201), "excessive threads");

        System.out.println("PortScanner validation tests passed.");
    }
}
