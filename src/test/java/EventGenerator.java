import java.io.FileWriter;
import java.io.IOException;
import java.time.Instant;

public class EventGenerator {

    public static void main(String[] args) throws IOException {

        FileWriter writer = new FileWriter("events_1000.json");

        writer.write("[\n");

        Instant baseTime =
                Instant.now().minusSeconds(3600);

        for (int i = 1; i <= 1000; i++) {

            String machineId =
                    "M-00" + ((i % 5) + 1);

            Instant eventTime =
                    baseTime.plusSeconds(i * 10L);

            int defectCount =
                    (i % 10 == 0) ? 1 : 0;

            long durationMs =
                    500 + (i % 5) * 250;

            writer.write("  {\n");

            writer.write(
                    "    \"eventId\": \"E-" + i + "\",\n"
            );

            writer.write(
                    "    \"eventTime\": \"" +
                            eventTime +
                            "\",\n"
            );

            writer.write(
                    "    \"machineId\": \"" +
                            machineId +
                            "\",\n"
            );

            writer.write(
                    "    \"durationMs\": " +
                            durationMs +
                            ",\n"
            );

            writer.write(
                    "    \"defectCount\": " +
                            defectCount +
                            "\n"
            );

            writer.write("  }");

            if (i < 1000) {
                writer.write(",");
            }

            writer.write("\n");
        }

        writer.write("]");

        writer.close();

        System.out.println(
                "events_1000.json created successfully with 1000 realistic events"
        );
    }
}