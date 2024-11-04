package com.cmpt370T7.PRJFlow;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class PopulateCsv {

    public static void appendDataToCsv(int x, int y, String data, String filename) {
        File file = new File(filename);
        List<List<String>> csvData = new ArrayList<>();

        // Reads existing CSV file if it exists
        if (file.exists()) {
            try (BufferedReader br = new BufferedReader(new FileReader(file))) {
                String line;
                // List of lists with data already in csv
                // Necessary, so we can append or edit data, without creating entire file each time
                while ((line = br.readLine()) != null) {
                    List<String> row = new ArrayList<>();
                    String[] values = line.split(",");
                    for (String value : values) {
                        row.add(value);
                    }
                    csvData.add(row);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // Ensures the CSV data structure is large enough for y
        // While it isn't appends more lists
        while (csvData.size() <= y) {
            csvData.add(new ArrayList<>());
        }
        // Ensures the CSV data structure is large enough for x
        // Adds empty placeholders since csv files require ,'s for index value
        List<String> row = csvData.get(y);
        while (row.size() <= x) {
            row.add("");
        }

        // Sets the value at the specific x, y position
        row.set(x, data);

        // Writes and saves data back into the CSV file
        try (PrintWriter pw = new PrintWriter(new FileWriter(file))) {
            for (List<String> csvRow : csvData) {
                pw.println(String.join(",", csvRow));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String promptFromData(String input) {
        // $env:OLLAMA_HOST="127.0.0.1:8000"
        // ollama serve
        // Args to start model 8000 is port num

        try {
            // Define your prompt and model
            String prompt = "Generate only the following do not speak or add anything unnesecary.  " +
                    "From the following data I would like you to output in csv format any key info you " +
                    "deem nessecary from the following parsed data.  Do not speak, generate only one csv " +
                    "format message surrounded by {}.  You should also try to align the columns and rows in a manner that would make" +
                    "the best sense to a human.  The pdf is a parsed invoice statement, and the csv is needed for managing financials" + input;


            String model = "llama3.1"; // Model can be changed

            // Sets up the requested JSON payload
            String requestBody = String.format("{\"model\": \"%s\", \"prompt\": \"%s\", \"stream\": false}", model, prompt);

            // Creates the HTTP client
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:8000/api/generate"))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                    .build();

            // Sends the request and gets the response
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());


            // Parses and prints the response
            ObjectMapper mapper = new ObjectMapper();
            JsonNode jsonResponse = mapper.readTree(response.body());
            String output = jsonResponse.get("response").asText();
            System.out.println("Response from Ollama Model:");
            System.out.println(output);

            // Returns the output of the model
            return output;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "Errored out";
    }


    public static void PasteToCsv(String filePath, String data) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            // Strips unnecessary data
            data = data.replace("{", "").replace("}", "").trim();
            // Writes the data to the CSV file
            writer.write(data);
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static void main(String[] args) {

        String parsedData = PdfParser.extractDataElementsFromPdf("sample-files/TestFiles/ExampleInvoice.pdf");
        String returnedPrompt = promptFromData(parsedData);
        PasteToCsv("sample-files/TestFiles/TestCsv.csv", returnedPrompt);
    }
}
