package com.cmpt370T7.PRJFlow.llm;


import java.io.*;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PopulateCsv {
    private static final Logger logger = LoggerFactory.getLogger(PopulateCsv.class);


    /**
     * Appends or inserts data at a specific (x, y) position in a CSV file.
     *
     * @param x The column index where the data should be inserted.
     * @param y The row index where the data should be inserted.
     * @param data The data to insert at the specified position.
     * @param filename The path to the CSV file.
     */
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

    /**
     * Generates a prompt from data in a CSV format using a local neural net model.
     *
     * @param input The input string to be processed.
     * @return The response from the model as a string.
     */
    public static String promptFromDataCsv(String input) {
        // $env:OLLAMA_HOST="127.0.0.1:8000"
        // ollama serve
        // Args to start model 8000 is port num

        try {
            String response = AiEngine.getInstance().createCSVSummary(input);
            logger.info("Response from Ollama Model: {}", response);
            return response;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "Errored out";
    }


    /**
     * Writes cleaned data to a specified CSV file.
     *
     * @param filePath The path to the output CSV file.
     * @param data The data to be written.
     */
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

    /**
     * Extracts data from a PDF file, processes it with the model, and writes the result to a CSV file.
     *
     * @param inputFilePath The path to the input PDF file.
     * @param outputFilePath The path to the output CSV file.
     */
    public static void GenerateCsv(String inputFilePath, String outputFilePath) {
        String parsedData = PdfParser.extractDataElementsFromPdf(inputFilePath);
        String returnedPrompt = promptFromDataCsv(parsedData);
        PasteToCsv(outputFilePath, returnedPrompt);

    }


    public static void main(String[] args) {
        //String parsedData = PdfParser.extractDataElementsFromPdf("sample-files/TestFiles/ExampleInvoice.pdf");
        //String returnedPrompt = promptFromData(parsedData);
        //PasteToCsv("sample-files/TestFiles/TestCsv.csv", returnedPrompt);
    }
}
