package fileUtils;

import com.opencsv.CSVReader;
import Enumerations.MessageLevel;
import miscellaneousUtils.RandomUtils;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.io.input.BOMInputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static ReportUtils.Report.reportAndLog;


/**
 * Class holds methods to work with CSV files
 */
public class CsvUtils {

    private static Logger logger= LoggerFactory.getLogger(CsvUtils.class);

    /**
     * Return all lines of text from CSV file
     * @return all lines of text from CSV file
     * @author plot.ofek
     * @since 02.05.2021
     */
    public static List<String[]> getAllLinesOfTextFromCsv(String filePath) {

        List<String> csvList = new ArrayList<>();
        Reader reader = null;
        try {
            reader = Files.newBufferedReader(Paths.get(filePath));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return readAll(reader);
    }

    /**
     * Return all lines of text from CSV file
     * @param reader Reader instance
     * @return all lines of text from CSV file
     * @author plot.ofek
     * @since 02.05.2021
     */
    public static List<String[]> readAll(Reader reader) {

        List<String[]> list = new ArrayList<>();
        try {
            CSVReader csvReader = new CSVReader(reader);
            list = csvReader.readAll();
            reader.close();
            csvReader.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return list;
    }

    /**
     * Return all lines of text from CSV file as a list of hashmaps
     * @return all lines of text from CSV file as a list of hashmaps
     * @author Dafna Genosar
     * @since 22.12.2021
     */
    public static List<HashMap<String, String>> getAllLinesFromCsvAsHashMap(String filePath) {

        List<String[]> csvListLines = getAllLinesOfTextFromCsv(filePath);

        List<HashMap<String, String>> mapToReturn = new ArrayList<>();

        String[] columns = csvListLines.get(0);

        for(int i=1; i< csvListLines.size(); i++)
        {
            String[] currentLine = csvListLines.get(i);
            HashMap<String, String> currentLineHash = new HashMap<>();

            for(int c=0; c< currentLine.length; c++)
            {
                String columnName = columns[c];
                String columnValue = currentLine[c];
                currentLineHash.put(columnName, columnValue);
            }
            mapToReturn.add(currentLineHash);
        }
        return mapToReturn;
    }

    /**
     * get List of all the rows in CSV file
     *
     * @param csvPath      - the path of the csv file
     * @return list of all the data in each row (each String contain all the data of one row)
     * @author - Lior Umflat
     * @since - 2.6.2021
     */
    public static List<String> getCSVFileRows(String csvPath) {
        //create list that will contains the csv file rows
        List<String> csvFileRows = new ArrayList<>();
        try {
            BufferedReader csvReader = new BufferedReader(new FileReader(csvPath));
            String row = csvReader.readLine();
            while (row!=null) //while we didn't get to the last row add the rows to the list
            {
                //add all the rows to the list
                csvFileRows.add(row);
                //read the next line
                row = csvReader.readLine();

            }
            //close csvReader
            csvReader.close();
        } catch (Exception e) {
            System.out.println("couldn't readLine or close the csv file");
            logger.info("couldn't readLine or close the csv file. see details - " + e.getStackTrace());
        }
        return csvFileRows;
    }

    /**
     * get List of all the rows in CSV file by numberOfRows in the file
     *
     * @param csvPath      - the path of the csv file
     * @param numberOfRows - number of rows in the file
     * @return list of all the data in each row (each String contain all the data of one row)
     * @author - Lior Umflat
     * @since - 2.6.2021
     */
    public static List<String> getCSVFileRows(String csvPath, int numberOfRows) {
        //create list that will contains the csv file rows
        List<String> csvFileRows = new ArrayList<String>();
        try {
            BufferedReader csvReader = new BufferedReader(new FileReader(csvPath));
            String row;
            for (int i = 0; i < numberOfRows + 1; i++) //adding one because the first row is the headers row
            {
                row = csvReader.readLine();
                //close the csvReader and break from the loop when reaching the end of the file
                if (row == null) {
                    csvReader.close();
                    break;
                }
                //add all the rows to the list
                csvFileRows.add(row);

            }
        } catch (Exception e) {
            logger.warn("couldn't readLine or close the csv file. see details - " + e.getStackTrace());
        }
        return csvFileRows;
    }

    /**
     * function search value in a specific column in csv file and return the record row
     * @param columnNameToSearch - column to search value
     * @param valueToSearch -value to search
     * @param filepath - file to search
     * @return record with request value in request column
     * @throws Exception
     * @author Yael.Rozenfeld
     * @since 1.11.2021
     */
    public static CSVRecord getRecordFromCSV(String columnNameToSearch,String valueToSearch, String filepath) throws Exception {
        //loud csv file
        URL url = new File(filepath).toURI().toURL();
        //read CSV file
        Reader in = new InputStreamReader(new BOMInputStream(url.openStream()), "UTF-8");
        CSVParser parser = new CSVParser(in, CSVFormat.EXCEL.withHeader());
//      search value in the request column
        for (CSVRecord record : parser) {
            String value = record.get(columnNameToSearch);
            if(value.equalsIgnoreCase(valueToSearch))
                return record;
        }
        //if value could not be found / if value was not found
        logger.info("value: " + valueToSearch +" wasn't found in column: "  +columnNameToSearch+ "in file: " + filepath);
        return null;
    }

    /**
     * Get all rows from csv file
     * @param csvFilePath - file get its rows
     * @param isCommaDelimiter - true for comma delimiter, false for tab delimiter
     * @author Yael Rozenfeld
     * @since 23.02.2023
     * @throws IOException
     */
    public static List<String[]> getCsvRows(String csvFilePath,boolean isCommaDelimiter) throws IOException {
        // List to store CSV rows
        List<String[]> rows = new ArrayList<>();
        String delimiter =isCommaDelimiter?",":"\t";
        BufferedReader br = new BufferedReader(new FileReader(csvFilePath));
        String line;
        while ((line = br.readLine()) != null) {
            String[] row = line.split(delimiter);
            rows.add(row);
        }
        return rows;
    }

        /**
     * Get random row from csv file
     * @param filePath - file to random row
     * @param startRandom - first row to start random (first row in file is 0)
     * @param isCommaDelimiter - true for comma delimiter, false for tab delimiter
     * @return String[] contains random row
     * @author Yael Rozenfeld
     * @since 23.02.2023
     * @throws IOException
     */
    public static String[] getRandomRowFromCsvFile(String filePath,int startRandom,boolean isCommaDelimiter) throws IOException {
        // List to store CSV rows
        List<String[]> rows = getCsvRows(filePath,isCommaDelimiter);
        reportAndLog("count of rows in csv file: " + rows.size(), MessageLevel.INFO);
        // Get a random row from the list
        int index= RandomUtils.getRandomNumber(startRandom,rows.size());
        reportAndLog("random row is "  + index,MessageLevel.INFO);
        return rows.get(index);
    }
}
