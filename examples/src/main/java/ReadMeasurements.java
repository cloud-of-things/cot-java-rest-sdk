import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import com.google.gson.JsonObject;
import com.telekom.m2m.cot.restsdk.CloudOfThingsPlatform;
import com.telekom.m2m.cot.restsdk.measurement.Measurement;
import com.telekom.m2m.cot.restsdk.measurement.MeasurementApi;
import com.telekom.m2m.cot.restsdk.measurement.MeasurementCollection;
import com.telekom.m2m.cot.restsdk.util.CotSdkException;
import com.telekom.m2m.cot.restsdk.util.Filter;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.*;

public class ReadMeasurements {
    private static final String NEW_LINE_SEPARATOR = "\n";

    public static void main(String[] args) {
        String host = "https://cot-url";
        String tenant = "tenant";
        String user = "user";
        String password = "password";

        Calendar from = new GregorianCalendar(2016, Calendar.JANUARY, 1, 0, 0, 0);
        Calendar to = new GregorianCalendar(2016, Calendar.JANUARY, 31, 23, 59, 59);
        SimpleDateFormat dtFileFormat = new SimpleDateFormat("yyyy-MM-dd_HH-mm");

        String outputFile = "cot-records_" + dtFileFormat.format(from.getTime()) + "_" + dtFileFormat.format(to.getTime()) + "_grabbedon_" + dtFileFormat.format(new Date()) + ".csv";
        FileWriter fileWriter = null;
        CSVFormat csvFileFormat = CSVFormat.DEFAULT.withRecordSeparator(NEW_LINE_SEPARATOR);
        CSVPrinter csvFilePrinter = null;
        try {

            fileWriter = new FileWriter(outputFile);

            csvFilePrinter = new CSVPrinter(fileWriter, csvFileFormat);


            CloudOfThingsPlatform platform = new CloudOfThingsPlatform(
                    host,
                    tenant, user, password);
            MeasurementApi mApi = platform.getMeasurementApi();
            SimpleDateFormat dt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            SimpleDateFormat csv = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
            System.out.println("Grabbing data From: '" + dt.format(from.getTime()) + "' To: '" + dt.format(to.getTime()) + "'");
            MeasurementCollection mcol = mApi.getMeasurements(Filter.build().bySource("142300").byDate(from.getTime(), to.getTime()), 2000);
            Table<Date, String, Double> records = HashBasedTable.create();
            ArrayList<Date> list = new ArrayList<Date>();
            while (true) {
                int maxRetry = 5;
                Measurement[] measurements = new Measurement[0];

                for (int i = 0; i < maxRetry; i++) {
                    System.out.println("Loading measurements");
                    boolean flagRetry = false;
                    try {
                        measurements = mcol.getMeasurements();
                    } catch (CotSdkException e) {
                        if (e.getCause() instanceof SocketTimeoutException ||
                                e.getCause() instanceof UnknownHostException) {
                            System.err.println("Got SocketTimeoutException, will retry (" + (i + 1) + "/" + maxRetry + ")");
                            flagRetry = true;
                        } else {
                            throw e;
                        }
                    }

                    if (!flagRetry) {
                        break;
                    }

                    if (i + 1 == maxRetry) {
                        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
                        System.out.println("Reached max retry, should I stop (Enter 'stop') or continue (just type anything and enter)");
                        String input = br.readLine();
//                        String input = System.console().readLine();
                        if (input.equals("stop")) {
                            System.exit(2);
                        }
                        i = 0;

                    }
                }

                if (measurements.length == 0)
                    break;

                for (int i = 0; i < measurements.length; i++) {
                    Measurement m = measurements[i];
                    JsonObject rawData;
                    double value;
                    switch (m.get("type").toString()) {
                        case "HumiditySensor":
                            rawData = (JsonObject) m.get("de_patrickbreucking_i2cagent_cumulocity_HumiditySensor");
                            value = rawData.get("humidity").getAsJsonObject().get("value").getAsDouble();
                            records.put(m.getTime(), m.get("type").toString(), value);
                            break;
                        case "LightSensor":
                            rawData = (JsonObject) m.get("de_patrickbreucking_i2cagent_cumulocity_LightSensor");
                            value = rawData.get("light").getAsJsonObject().get("value").getAsDouble();
                            records.put(m.getTime(), m.get("type").toString(), value);
                            break;
                        case "PressureSensorRepresentation":
                            rawData = (JsonObject) m.get("de_patrickbreucking_i2cagent_cumulocity_PressureSensorRepresentation");
                            value = rawData.get("pressure").getAsJsonObject().get("value").getAsDouble();
                            records.put(m.getTime(), m.get("type").toString(), value);
                            break;
                        case "TemperatureSensor":
                            rawData = (JsonObject) m.get("de_patrickbreucking_i2cagent_cumulocity_TemperatureSensor");
                            value = rawData.get("temperature1").getAsJsonObject().get("value").getAsDouble();
                            records.put(m.getTime(), m.get("type").toString(), value);
                            break;
                        case "AirQuality":
                            rawData = (JsonObject) m.get("de_patrickbreucking_i2cagent_cumulocity_AirQualitySensor");
                            value = rawData.get("quality").getAsJsonObject().get("value").getAsDouble();
                            records.put(m.getTime(), m.get("type").toString(), value);
                            break;
                    }
                    if (!list.contains(m.getTime())) {
                        list.add(m.getTime());
                    }
                }


                System.out.println("Measurements: " + measurements.length);

                mcol.next();
            }

            for (Date date : list) {
                System.out.println(dt.format(date));
                Map<String, Double> row = records.row(date);
                ArrayList<String> entries = new ArrayList<>();
                entries.add(csv.format(date));
                for (Map.Entry<String, Double> entry : row.entrySet()) {
                    System.out.println("Entry: " + entry.getKey() + " - " + entry.getValue());
                    entries.add("" + entry.getValue());
                }

                csvFilePrinter.printRecord(entries);

            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                fileWriter.flush();
                fileWriter.close();
                csvFilePrinter.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }


}