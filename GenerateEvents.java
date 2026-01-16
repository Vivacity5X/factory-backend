import java.io.FileWriter;
import java.io.IOException;

public class GenerateEvents {
    public static void main(String[] args) throws IOException {

        FileWriter writer = new FileWriter("events_1000.json");
        writer.write("[\n");

        for (int i = 1; i <= 1000; i++) {
            writer.write("  {\n");
            writer.write("    \"eventId\": \"E-" + i + "\",\n");
            writer.write("    \"eventTime\": \"2026-01-13T05:00:00Z\",\n");
            writer.write("    \"machineId\": \"M-001\",\n");
            writer.write("    \"durationMs\": 1000,\n");
            writer.write("    \"defectCount\": " + (i % 10 == 0 ? 1 : 0) + "\n");
            writer.write("  }");

            if (i < 1000) writer.write(",");
            writer.write("\n");
        }

        writer.write("]");
        writer.close();

        System.out.println("events_1000.json created with 1000 events");
    }
}
