package fileUtils;


import Managers.ReportInstanceManager;
import com.github.pjfanning.xlsx.SharedStringsImplementationType;
import com.github.pjfanning.xlsx.StreamingReader;
import com.relevantcodes.extentreports.LogStatus;
import dateTimeUtils.DateTime;
import dateTimeUtils.DateUtils;
import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;
import java.io.*;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static constantsUtils.CommonConstants.EMPTY_STRING;


/**
 * Class holds methods to work with Excel files
 */
public class ExcelUtils {
    private static Logger logger = LoggerFactory.getLogger(ExcelUtils.class);

    /**
     * get Excel sheet to work on it
     * @param filePath - Excel file path
     * @param sheetIndex - index of sheet to work on it
     * @return the sheet to work on it
     * @throws IOException IOException
     * @throws InvalidFormatException InvalidFormatException
     * @author abo_saleh.rawand
     * @since 03.10.2022
     */
    public static Sheet getExcelWorkSheet(String filePath,int sheetIndex) throws IOException, InvalidFormatException {
        File file =new File(filePath);
        XSSFWorkbook workbook = new XSSFWorkbook(file);
        return workbook.getSheetAt(sheetIndex);
    }
    /**
     * Method opens the file with the name sent to method and retrieves a random value from the column with the index
     * sent to the method
     *
     * @param fileName    Name of the file to extract random value from
     * @param columnIndex index of the column to extract random value from
     * @return The text in the random location in the specified column
     * @throws IOException when InputStream or XSSFWorkbook can't be closed
     */
    public static String getRandomValueFromSpecifiedColumn(String fileName, int columnIndex) throws IOException {
        String randomValue = EMPTY_STRING;
        InputStream inputFS = null;
        XSSFWorkbook workbook = null;
        Random rand = new Random();
        try {
            logger.info("Opening excel file: " + fileName);
            inputFS = new FileInputStream(fileName);
//        	POIFSFileSystem poifs = new POIFSFileSystem(inputFS);
            workbook = new XSSFWorkbook(inputFS);
//    		Workbook workbook2 = new SXSSFWorkbook(workbook1, 100);

            // Creating a Workbook from an Excel file (.xls or .xlsx)
//            Workbook workbook = WorkbookFactory.create(poifs);


            // Getting the Sheet at index zero
            Sheet sheet = workbook.getSheetAt(0);
            logger.info("Opening first sheet");


            // Create a DataFormatter to format and get each cell's value as String
            DataFormatter dataFormatter = new DataFormatter();

            int randomNum = rand.nextInt(sheet.getLastRowNum()) + 1;
            while (!(dataFormatter.formatCellValue(sheet.getRow(randomNum).getCell(columnIndex)).trim().length() > 0)) {
                randomNum = rand.nextInt(sheet.getLastRowNum()) + 1;
            }

            randomValue = dataFormatter.formatCellValue(sheet.getRow(randomNum).getCell(columnIndex)).trim();
            logger.info("Random value found: " + randomValue);


            // Closing the workbook
            workbook.close();
            inputFS.close();
            logger.info("Closing file: " + fileName);


        } catch (Throwable e) {

            if (workbook != null) {
                workbook.close();
            }

            if (inputFS != null) {
                inputFS.close();
            }

            e.printStackTrace();
        }
        return randomValue;
    }


    /**
     * get number of rows in the first sheet of excel file
     * @param filePath   -  file path to .xlsx file
     * @param sheetIndex - the index of the sheet to find its number of rows
     * @return the number of rows in the file
     * @author - Lior umflat
     * @since - 13.7.2021
     */
    public static int getNumberOfRowsInExcelFile(String filePath, int sheetIndex) {
        FileInputStream fis = null;
        XSSFWorkbook workbook = null;
        int rowNum = 0;
        try {
            fis = new FileInputStream(filePath);
            workbook = new XSSFWorkbook(fis);
            // Getting the Sheet at index sheetIndex
            Sheet sheet = workbook.getSheetAt(sheetIndex);
            //get number of rows
            rowNum = sheet.getLastRowNum() + 1;
        } catch (Exception e) {
            logger.error("couldn't read the file. view error: " + e.getMessage());
            throw new Error("couldn't read the file. view error: " + e.getMessage());
        } finally {
            try {
                //close workbook and fis
                if (workbook != null) {
                    workbook.close();
                }
                if (fis != null) {
                    fis.close();
                }
            } catch (Exception e) {
                logger.error("couldn't close the file. see error: " + e.getMessage());
            }
        }

        return rowNum;

    }

    /**
     * get list of unique random values from specific column in excel file
     *
     * @param filePath            - the path of the excel file
     * @param columnIndex         - the index of the column to choose random values from
     * @param numOfValuesToSelect - number of unique random values to select
     * @return List of all the unique random values that were chosen
     * @author - Lior Umflat
     * @since - 18.7.2021
     */
    public static List<String> getAListOfUniqueRandomValuesFromSpecificColumn(int numOfValuesToSelect, String filePath, int columnIndex) throws IOException {
        //create list that will contain all the chosen random values
        List<String> randomChosenValues = new ArrayList<>();
        //if numOfValuesToSelect is greater than number of rows in the excel file throw an error
        if(numOfValuesToSelect>getNumberOfRowsInExcelFile(filePath,0)){
            logger.error("number of values to select from the excel file is more than the actual rows in the file");
            throw new Error("number of values to select from the excel file is more than the actual rows in the file");
        }
        //get random values from the excel file
        for (int i = 0; i < numOfValuesToSelect; i++) {
            //get random value from the column
            String stringToAddToList = getRandomValueFromSpecifiedColumn(filePath, columnIndex);
            //while the string exists in the randomChosenValues list choose different string
            //create attempt value that will end the loop if we try up to 500 attempts
            int attempt=0;
            while (randomChosenValues.contains(stringToAddToList)) {
                stringToAddToList = getRandomValueFromSpecifiedColumn(filePath, columnIndex);
                attempt++;
                //if we got to attempt #500, throw error, enter message to the log and break from the loop
                if(attempt==500)
                {
                    logger.error("ExcelUtils - getAListOfUniqueRandomValuesFromSpecificColumn - 500 attempts were executed, but still didn't find random value that doesn't exist in the list of randomChosenValues");
                    throw new Error("ExcelUtils - getAListOfUniqueRandomValuesFromSpecificColumn - 500 attempts were executed, but still didn't find random value that doesn't exist in the list of randomChosenValues");
                }
            }
            //add the string to the list
            randomChosenValues.add(stringToAddToList);
        }

        //return the list
        return  randomChosenValues;
    }

    /**
     * Update value in Excel, based on file name, sheet, row and column.
     * @param updatedValue New value
     * @param excelFilePath Excel file
     * @param sheetNumber Sheet number in file
     * @param rowNumber Number of row
     * @param columnNumber Number of column
     * @author plot.ofek
     * @since 17.08.2021
     */
    public static void updateSpecificCellInExcelFile(String updatedValue, String excelFilePath, int sheetNumber, int rowNumber, int columnNumber) {

        FileInputStream inputStream;
        Workbook workbook = null;
        FileOutputStream outputStream = null;
        
        try {
            inputStream = new FileInputStream(excelFilePath);
            workbook = WorkbookFactory.create(inputStream);

            Sheet sheet = workbook.getSheetAt(sheetNumber);

            Cell cell = sheet.getRow(rowNumber).getCell(columnNumber);
            cell.setCellValue(updatedValue);

            inputStream.close();

            outputStream = new FileOutputStream(excelFilePath);
            workbook.write(outputStream);

        } catch (IOException | EncryptedDocumentException ex) {
            logger.error(ex.getMessage(),ex);
            ReportInstanceManager.getCurrentTestReport().log(LogStatus.ERROR, "Error with excel file " + excelFilePath + "</br>" + ex.getMessage());
        }
        finally {
            try {
                //close workbook and outputStream
                if (workbook != null) {
                    workbook.close();
                }
                if (outputStream != null) {
                    outputStream.close();
                }
            } catch (Exception e) {
                logger.error("couldn't close the file. see error: " + e.getMessage());
            }
        }
    }

    /**
     * Delete a row's content in Excel file, leaving the row blank
     * @param excelFilePath file path
     * @param sheet Sheet can be either sheet number (int) / sheet name (String) / Sheet object
     * @param rowIndexToDelete index of row to delete
     * @param <S> generic type of sheet
     */
    public static <S> void deleteRowContent(String excelFilePath, S sheet, int rowIndexToDelete){

        if(rowIndexToDelete < 0)
            throw new Error(String.format("Cannot delete a row in index %s from excel sheet", rowIndexToDelete));

        FileInputStream fileInputStream;
        Workbook workbook = null;
        FileOutputStream fileOutputStream = null;

        try {
            File file = new File(excelFilePath);
            if(!file.exists()) {
                throw new Error(String.format("Excel file '%s' cannot be found", excelFilePath));
            }
            fileInputStream = new FileInputStream(excelFilePath);
            workbook = new XSSFWorkbook();
            workbook = WorkbookFactory.create(fileInputStream);
            Sheet sheetObj = getSheetObject(workbook, sheet);

            // total no. of rows
            int totalRows = sheetObj.getLastRowNum();
            System.out.println("Total no of rows : " + totalRows);

            // remove values from third row but keep third row blank
            if (sheetObj.getRow(rowIndexToDelete) != null) {
                sheetObj.removeRow(sheetObj.getRow(rowIndexToDelete));
            }

            fileOutputStream = new FileOutputStream(excelFilePath);
            workbook.write(fileOutputStream);

        } catch (Throwable ex) {
            logger.error(ex.getMessage(), ex);
            ReportInstanceManager.getCurrentTestReport().log(LogStatus.ERROR, "Error with excel file " + excelFilePath + "</br>" + ex.getMessage());
        }
        finally {
            try {
                //close workbook and outputStream
                if (workbook != null) {
                    workbook.close();
                }
                if (fileOutputStream != null) {
                    fileOutputStream.close();
                }
            } catch (Exception e) {
                logger.error("couldn't close the Excel file. Error: " + e.getMessage());
            }
        }
    }

    /**
     * Delete the last row in Excel file, leaving the row blank
     * @param excelFilePath file path
     * @param sheet Sheet can be either sheet number (int) / sheet name (String) / Sheet object
     * @param <S> generic type of sheet
     */
    public static <S> void deleteLastRow(String excelFilePath, S sheet){

        FileInputStream fileInputStream;
        Workbook workbook = null;
        FileOutputStream fileOutputStream = null;

        try {
            File file = new File(excelFilePath);
            if(!file.exists()) {
                throw new Error(String.format("Excel file '%s' cannot be found", excelFilePath));
            }
            fileInputStream = new FileInputStream(excelFilePath);
            workbook = new XSSFWorkbook();
            workbook = new XSSFWorkbook(fileInputStream);
           // workbook = WorkbookFactory.create(fileInputStream);
            Sheet sheetObj = getSheetObject(workbook, sheet);

            sheetObj.setSelected(true);

            int rowIndexToDelete = sheetObj.getLastRowNum();

            // remove values from third row but keep third row blank
            if (sheetObj.getRow(rowIndexToDelete) != null) {
                sheetObj.removeRow(sheetObj.getRow(rowIndexToDelete));
            }

            rowIndexToDelete = sheetObj.getLastRowNum();

            fileOutputStream = new FileOutputStream(excelFilePath);
            workbook.write(fileOutputStream);

        } catch (Throwable ex) {
            logger.error(ex.getMessage(), ex);
            ReportInstanceManager.getCurrentTestReport().log(LogStatus.ERROR, "Error with excel file " + excelFilePath + "</br>" + ex.getMessage());
        }
        finally {
            try {
                //close workbook and outputStream
                if (workbook != null) {
                    workbook.close();
                }
                if (fileOutputStream != null) {
                    fileOutputStream.close();
                }
            } catch (Exception e) {
                logger.error("couldn't close the Excel file. Error: " + e.getMessage());
            }
        }
    }

    private static <S> Sheet getSheetObject(Workbook workbook, S sheet){

        Sheet sheetObject;

        if(sheet instanceof Sheet)
            return (Sheet)sheet;
        else if(sheet instanceof String)
            return workbook.getSheet((String)sheet);
        else if(sheet instanceof Integer)
            return workbook.getSheetAt((int)sheet);
        else throw new Error("Sheet type received in getSheetObject() must be of types: String, int or Sheet object");
    }

    /**
     * Create the Excel file if does not exist and add new rows to Excel, based on file name, sheet, and  cells and values from List of HashMap.
     * @param excelFilePath Excel file
     * @param sheet Sheet can be either sheet number (int) / sheet name (String) / Sheet object
     * @param linesToUpdate -  list of hashMap each item in List is a row, each item in HashMap is cell. Key=cellIndex, value=value to set in cell
     * @author Yael.Rozenfeld
     * @since 25/1/2022
     * @author Dafna Genosar
     * @since 13.11.2022
     */
    public static <S, T> void writeNewRowsToExcel(String excelFilePath, S sheet, List<HashMap<Integer, T>> linesToUpdate) {

        FileInputStream inputStream;
        Workbook workbook = null;
        FileOutputStream outputStream = null;

        try {
            File file = new File(excelFilePath);
            if(!file.exists()) {
                if(sheet instanceof String)
                    file = createExcelFile(excelFilePath, (String)sheet);
                else
                    file = createExcelFile(excelFilePath, null);
            }

            inputStream = new FileInputStream(excelFilePath);
            workbook = WorkbookFactory.create(inputStream);

            Sheet sheetObject;

            if(sheet instanceof Sheet)
                sheetObject = (Sheet)sheet;
            else if(sheet instanceof String)
                sheetObject = workbook.getSheet((String)sheet);
            else if(sheet instanceof Integer)
                sheetObject = workbook.getSheetAt((int)sheet);
            else throw new Error("Sheet type received in writeNewRowsToExcel() must be of types: String, int or Sheet object");

            for (HashMap<Integer, T> lineToUpdate : linesToUpdate) {
                int rowNumber = sheetObject.getLastRowNum()+1;
                Row row = sheetObject.createRow(rowNumber);
                for (Map.Entry<Integer, T> valuesToUpdate : lineToUpdate.entrySet()) {
                    int columnNumber = valuesToUpdate.getKey();
                    Cell cell = row.createCell(columnNumber);

                    Object updatedValue = valuesToUpdate.getValue();

                    try {

                        if (updatedValue instanceof String)
                            cell.setCellValue((String) updatedValue);
                        else if (updatedValue instanceof Integer)
                            cell.setCellValue((Integer) updatedValue);
                        else if (updatedValue instanceof Double)
                            cell.setCellValue((Double) updatedValue);
                        else if (updatedValue instanceof Long) {
                            Long l = Long.valueOf(updatedValue.toString());
                            double d = l.doubleValue();
                            cell.setCellValue(d);
                        } else if (updatedValue instanceof Date)
                            cell.setCellValue((Date) updatedValue);
                        else if (updatedValue instanceof DateTime)
                            cell.setCellValue(((DateTime) updatedValue).getDateObject());
                    }
                    catch(IllegalArgumentException eex){
                        logger.error(String.format("Failed to set value: %s in column %d. Error: %s", updatedValue, columnNumber, eex.getMessage()), eex);
                        ReportInstanceManager.getCurrentTestReport().log(LogStatus.ERROR, String.format("Failed to set value: %s in column %d. Error: %s", updatedValue, columnNumber, eex.getMessage()));
                        throw eex;
                    }
                }
            }

            inputStream.close();

            outputStream = new FileOutputStream(excelFilePath);
            workbook.write(outputStream);

        } catch (IOException | EncryptedDocumentException ex) {
            logger.error(ex.getMessage(), ex);
            ReportInstanceManager.getCurrentTestReport().log(LogStatus.ERROR, "Error with excel file " + excelFilePath + "</br>" + ex.getMessage());
        } finally {
            try {
                //close workbook and outputStream
                if (workbook != null) {
                    workbook.close();
                }
                if (outputStream != null) {
                    outputStream.close();
                }
            } catch (Exception e) {
                logger.error("couldn't close the file. see error: " + e.getMessage());
            }
        }
    }

    /**
     * add new rows to Excel, based on file name, sheet, and  cells and values HashMap.
     * @param excelFilePath Excel file
     * @param sheetNumber Sheet number in file
     * @param lineToUpdate -  HashMap  -  each item in HashMap is cell. Key=cellIndex, value=value to set in cell
     * @author Yael.Rozenfeld
     * @since 25/1/2022
     * @author Dafna Genosar
     * @since 13.11.2022
     */
    public static <T> void writeNewSingleRowToExcel(String excelFilePath, int sheetNumber, HashMap<Integer, T> lineToUpdate) {

        List<HashMap<Integer, T>> linesToUpdate = new ArrayList<>();
        linesToUpdate.add(lineToUpdate);
        writeNewRowsToExcel(excelFilePath, sheetNumber, linesToUpdate);
    }

    /**
     * add new rows to Excel, based on file name, sheet, and  cells and values from List of HashMap.
     * @param excelFilePath Excel file
     * @param sheetNumber Sheet number in file
     * @param linesToUpdate -  list of hashMap each item in List is a row, each item in HashMap is cell. Key=column name, value=value to set in cell
     * @author Yael.Rozenfeld
     * @since 25/1/2022
     * @author Dafna Genosar
     * @since 13.11.2022
     */
    public static <T> void writeNewLinesToExcelByColumnName(String excelFilePath, int sheetNumber,  List<HashMap<String, T>> linesToUpdate) {

        List<HashMap<Integer,T>> rowsColumnsIndexAndValues = new ArrayList<>();

        for(HashMap<String, T> line : linesToUpdate){
            List<String> columns = new ArrayList<>(line.keySet());
            HashMap<String,Integer> columnsNameAndIndex= getColumnsIndex(excelFilePath,sheetNumber,0,columns);
            HashMap<Integer,T> columnsIndexAndValues=new HashMap<>();
            for(Map.Entry<String, Integer> columnIndex:columnsNameAndIndex.entrySet()){
                columnsIndexAndValues.put(columnIndex.getValue(),line.get(columnIndex.getKey()));
            }
            rowsColumnsIndexAndValues.add(columnsIndexAndValues);
        }

        writeNewRowsToExcel(excelFilePath,sheetNumber,rowsColumnsIndexAndValues);
    }

    /**
     * add new single row to Excel, based on file name, sheet, and  cells and values HashMap.
     * @param excelFilePath Excel file
     * @param sheetNumber Sheet number in file
     * @param lineToUpdate -  HashMap  -  each item in HashMap is cell. Key=column Name, value=value to set in cell
     * @author Yael.Rozenfeld
     * @since 25/1/2022
     */
    public  static void writeNewSingleRowToExcelByColumnName(String excelFilePath, int sheetNumber, HashMap<String, Object> lineToUpdate) {

        List<HashMap<String, Object>> linesToUpdate = new ArrayList<>();
        linesToUpdate.add(lineToUpdate);
        writeNewLinesToExcelByColumnName(excelFilePath, sheetNumber, linesToUpdate);
    }

    /**
     * Create a new Excel file
     * @param filePath the file path
     * @param sheetName optional sheet name or null
     * @return the File object
     * @author genosar.dafna
     * @since 2.5.2023
     */
    public static File createExcelFile(String filePath, @Nullable String sheetName){

        File file = FileUtils.createNewFile(filePath);

        try {
            FileOutputStream outputStream = new FileOutputStream(filePath);
            XSSFWorkbook workbook = new XSSFWorkbook();
            XSSFSheet sheet = createSheet(workbook, sheetName);
            workbook.write(outputStream);
        }
        catch(FileNotFoundException e1){
            throw new Error(String.format("File could not be found in path: %s<br>Error: %s", filePath, e1.getMessage()));
        }
        catch(IOException e2){
            throw new Error(String.format("Cannot create a new XSSFWorkbook in path: %s<br>Error: %s", filePath, e2.getMessage()));
        }
        return file;
    }

    /**
     * Create a new sheet
     * @param workbook the workbook object
     * @param sheetName optional sheet name or null
     * @return the sheet object
     * @author genosar.dafna
     * @since 2.5.2023
     */
    public static XSSFSheet createSheet(XSSFWorkbook workbook, @Nullable String sheetName){
        XSSFSheet sheet;
        if(sheetName != null)
            sheet = workbook.createSheet(sheetName);
        else
            sheet = workbook.createSheet();

        return sheet;
    }

    /**
     * Get Excel columns Index .function return a HashMap with column name and column index in each entry.
     * @param excelFilePath Excel file
     * @param sheetNumber Sheet number in file
     * @param ValuesToSearch -  List  -  each value in list is a column name in the excel
     * @param rowIndex - row index to search the value
     * @return HashMap<String,Integer> Key contains column name value contains column index
     * @author Yael.Rozenfeld
     * @since 25/1/2022
     */
        public static HashMap<String,Integer> getColumnsIndex(String excelFilePath, int sheetNumber,int rowIndex, List<String> ValuesToSearch){
        FileInputStream inputStream;
        Workbook workbook = null;
        FileOutputStream outputStream = null;
        HashMap<String,Integer> columnsIndex= new HashMap<>();
        try {
            inputStream = new FileInputStream(excelFilePath);
            workbook = WorkbookFactory.create(inputStream);
            Sheet sheet = workbook.getSheetAt(sheetNumber);
            Row row = sheet.getRow(rowIndex);

            for (String column : ValuesToSearch) {
                boolean cellFound=false;
                for(Cell cell:row){
                    if(cell.getStringCellValue().trim().equalsIgnoreCase(column)){
                        columnsIndex.put(column,cell.getColumnIndex());
                        cellFound=true;
                        break;
                    }
                }
                if(!cellFound){
                    columnsIndex.put(column,-1);
                    logger.info(String.format("column: %s doesn't exist in excel: %s",column,excelFilePath ));
                }

            }
            inputStream.close();
        } catch (IOException | EncryptedDocumentException ex) {
            logger.error(ex.getMessage(), ex);
            ReportInstanceManager.getCurrentTestReport().log(LogStatus.ERROR, "Error with excel file " + excelFilePath + "</br>" + ex.getMessage());
        } finally {
            try {
                //close workbook and outputStream
                if (workbook != null) {
                    workbook.close();
                }
                if (outputStream != null) {
                    outputStream.close();
                }
            } catch (Exception e) {
                logger.error("couldn't close the file. see error: " + e.getMessage());
            }
        }
        return columnsIndex;
    }


    /**
     * Convert xlsx file to csv file.
     * @param sourceExcelFile - path to xlsx file
     * @param destinationCsvFile - path to save the new CSV file
     * @throws IOException
     * @author Yael Rozenfeld
     * @since 9.2.2023
     */

    public static void convertXlsxToCsv (String sourceExcelFile, String destinationCsvFile) throws IOException {
        // Read the xlsx file
        FileInputStream fis = new FileInputStream(sourceExcelFile);
        XSSFWorkbook workbook = new XSSFWorkbook(fis);
        XSSFSheet sheet = workbook.getSheetAt(0);

        // Write the csv file
        FileWriter writer = new FileWriter(destinationCsvFile);
        for (Row row : sheet) {
            for (Cell cell : row) {
                try{
                cell.setCellValue(cell.getStringCellValue().replaceAll(",",""));}
                catch (Exception e){}
                writer.append(cell.toString());
                writer.append(',');
            }
            writer.append('\n');
        }
        writer.flush();
        writer.close();
        workbook.close();
        fis.close();
    }

    /**
     * Convert file XLSX to file CSV without X Top rows
     *
     * @param reportPath xpath of file XLSX
     * @param csvReportFixedName xpath and name of file CSV
     * @param numOfRows number of top rows that don't need to be moved to file CSV
     * @author sela.zvika
     * @since 01.05.2023
     */

    public void  convertCSVFromExcelWithoutXTopRows(String reportPath, String csvReportFixedName, int numOfRows) {

        File csvOutputFile = new File(csvReportFixedName);
        File xlsxFile = new File(reportPath);


        List<String[]> dataLines = new ArrayList<>();

        try (Workbook workbook = StreamingReader.builder()
                .rowCacheSize(100)
                .bufferSize(4096)
                .setSharedStringsImplementationType(SharedStringsImplementationType.TEMP_FILE_BACKED)
                .setEncryptSstTempFile(true)
                .open(xlsxFile)) {
            for (Sheet sheet : workbook) {
                System.out.println("Sheet: " + sheet.getSheetName());
                int i=0;
                for (Row r : sheet) {
                    if (i<numOfRows) {
                        i+=1;
                        continue;
                    }
                    List<String> dataLine = new ArrayList<String>();
                    for (Cell c : r) {
                        System.out.print('"');
                        System.out.print(c.getStringCellValue());
                        System.out.print("\",");
                        dataLine.add(c.getStringCellValue());
                    }
                    dataLines.add( dataLine.toArray(new String[0]));

                }
            }
            try (PrintWriter pw = new PrintWriter(csvOutputFile)) {
                dataLines.stream()
                        .map(this::convertToCSV)
                        .forEach(pw::println);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public String convertToCSV(String[] data) {
        return Stream.of(data)
                .map(this::escapeSpecialCharacters)
                .collect(Collectors.joining(","));
    }

    public String escapeSpecialCharacters(String data) {
        String escapedData = data.replaceAll("\\R", " ");
        if (data.contains(",") || data.contains("\"") || data.contains("'")) {
            data = data.replace("\"", "\"\"");
            escapedData = "\"" + data + "\"";
        }
        return escapedData;
    }

    /**
     * read all Excel file header as key and rows are the values
     * @param filePath - path of Excel file
     * @param sheetIndex - sheet index to extract
     * @return  HashMap<String, ArrayList<String>>
     * @since 15.06.2023
     * @author abo_saleh.rawand
     */
    public static HashMap<String, ArrayList<String>> readExcelFile(String filePath, int sheetIndex) {
        HashMap<String, ArrayList<String>> excelData = new HashMap<>();
        try {
            FileInputStream fis = new FileInputStream(filePath);

            // Create an Excel workbook object from the file
            Workbook workbook = new XSSFWorkbook(fis);

            // Get the first sheet of the workbook
            Sheet sheet = workbook.getSheetAt(sheetIndex);

            // Get the header row to retrieve column names
            Row headerRow = sheet.getRow(0);

            // Iterate over each column in the header row
            for (int columnIndex = 0; columnIndex < headerRow.getLastCellNum(); columnIndex++) {
                // Get the column name from the header row
                Cell headerCell = headerRow.getCell(columnIndex);
                String columnName = headerCell.getStringCellValue();

                // Iterate over each row in the column
                String cellValue = null;
                ArrayList<String> values = excelData.getOrDefault(cellValue, new ArrayList<>());
                for (int rowIndex = 1; rowIndex <= sheet.getLastRowNum(); rowIndex++) {
                    Row row = sheet.getRow(rowIndex);
                    Cell cell = row.getCell(columnIndex);
                    // Get the cell value based on the cell type
                    cellValue = "";
                    if (cell != null) {
                        cellValue = getCellValueAsString(cell);
                        // Add data to the HashMap with multiple values
                        values.add(cellValue);
                        excelData.put(columnName, values);
                    }
                }
                //there are sheets that have no rows , so we enter a "" to null values
                if (cellValue == null) {
                    cellValue = "";
                    values.add(cellValue);
                    excelData.put(columnName, values);
                }
            }
            // Close the workbook and input stream
            workbook.close();
            fis.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return excelData;
    }

    /**
     * Return all lines of text from Excel file as a list of hashmaps
     * @return all lines of text from Excel file as a list of hashmaps
     * @author Dafna Genosar
     * @since 26.06.2023
     */
    public static List<Map<String, Object>> readExcel(String filePath, int sheetNumber) {

        List<Map<String, Object>> dataList = new ArrayList<>();

        try (FileInputStream fileInputStream = new FileInputStream(filePath);
            Workbook workbook = new XSSFWorkbook(fileInputStream)) {

            Sheet sheet = workbook.getSheetAt(sheetNumber);

            Row headerRow = sheet.getRow(0);            //header row
            int numColumns = headerRow.getLastCellNum();  //number of cells

            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                Map<String, Object> dataMap = new HashMap<>();

                for (int j = 0; j < numColumns; j++) {
                    Cell cell = row.getCell(j);
                    String header = headerRow.getCell(j).getStringCellValue();
                    String value = cell == null ? "" : cell.toString();
                    dataMap.put(header, value);
                }

                dataList.add(dataMap);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return dataList;
    }

    /**
     * get cell value as string
     * @param dataCell - value of the cell
     * @return value of the cell as string
     * @since 16.06.2023
     * @author abo_saleh.rawand
     */
    public static String getCellValueAsString(Cell dataCell) {
        String data = "";
        switch (dataCell.getCellType()) {
            case STRING:
                data = dataCell.getStringCellValue();
                break;
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(dataCell)) {
                    Date date = dataCell.getDateCellValue();
                    data = DateUtils.convertDateToStringInFormat("MM/dd/yyy", date);
                } else
                    data = String.valueOf(dataCell.getNumericCellValue());
                break;
            case BOOLEAN:
                data = String.valueOf(dataCell.getBooleanCellValue());
                break;
            case FORMULA:
                data = dataCell.getCellFormula();
                break;
            default:
                // Handle other cell types as needed
                break;
        }
        return data;
    }
    /**
     * Get the first excel row that has the expected value under the column name
     * @param filePath path to excel
     * @param sheetIndex sheet index
     * @param columnName the desired column name to search under
     * @param columnValue the desired value
     * @return the first excel row that has the expected value under the column name
     * @author genosar.dafna
     * @since 26.06.2023
     */
    public static Map<String, Object> getRowByColumnValue(String filePath, int sheetIndex, String columnName, String columnValue) {

        List<Map<String, Object>> excelDataList = readExcel(filePath, sheetIndex);
        try {
            return excelDataList.stream().filter(x -> x.get(columnName).toString().equalsIgnoreCase(columnValue)).findFirst().orElse(null);
        }
        catch(Exception e)
        {
            throw new Error(String.format("Error when trying to return an excel row with column name '%s' and column value '%s'<br>Error: %s", columnName, columnValue, e.getMessage()));
        }
    }

    /**
     * Get All excel rows that has the expected value under the column name
     * @param filePath path to excel
     * @param sheetIndex sheet index
     * @param columnName the desired column name to search under
     * @param columnValue the desired value
     * @return All excel rows that has the expected value under the column name
     * @author abo_saleh.rawand
     * @since 05.07.2023
     */
    public static List<Map<String, Object>> getAllRowsByColumnValue(String filePath, int sheetIndex, String columnName, String columnValue) {
        List<Map<String, Object>> excelDataList = readExcel(filePath, sheetIndex);
        try {
            return  excelDataList.stream()
                    .filter(row -> row.containsKey(columnName) && row.get(columnName).equals(columnValue))
                    .collect(Collectors.toList());
        } catch (Exception var6) {
            throw new Error(String.format("Error when trying to return an excel row with column name '%s' and column value '%s'<br>Error: %s", columnName, columnValue, var6.getMessage()));
        }
    }
}
