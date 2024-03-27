package com.Automation.CommonUtils;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class GenericUtil {

    public static <T> T getValueFromJsonFile(String jsonFileName, Class<T> contentClass) {

        try {
            File file = new File(
                    GenericUtil.class.getClassLoader().getResource(jsonFileName).getFile()
            );
            ObjectMapper mapper = new ObjectMapper();

            T someClassObj = mapper.readValue(file, contentClass);

            return someClassObj;
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static ArrayList<Map<String, Object>> readCSVFile(String filepath) throws InterruptedException, IOException {

        ArrayList<Map<String, Object>> testCases = new ArrayList<>();
        String cvsSplitBy = ",";

        String csvFile = GenericUtil.class.getClassLoader().getResource(filepath).getFile();
        BufferedReader br = new BufferedReader(new FileReader(csvFile));

        String[] columnData = null;
        int columnCount = 0;

        String lineItem = "";
        for(int lineCounter = 0; (lineItem = br.readLine()) != null; lineCounter++) {

            if(lineCounter == 0) {
                columnData = lineItem.split(cvsSplitBy);
                columnCount = columnData.length;
                continue;
            }

            String[] testdata = new String[columnCount];
            Map<String, Object> testDataMap = new HashMap<>();
            for (int i=0; i < columnData.length; i++){
                testDataMap.put(columnData[i],"");
            }

            String[] data = lineItem.split(cvsSplitBy);
            for (int i=0; i < data.length; i++){
                testDataMap.put(columnData[i],data[i]);
            }

            testCases.add(testDataMap);

        }

        return testCases;
    }

    public static Iterator<Object[]> readTestDataCSVFile(String filepath) throws InterruptedException, IOException {

        List<Object[]> testCases = new ArrayList<>();
        String cvsSplitBy = ",";

        String csvFile = GenericUtil.class.getClassLoader().getResource(filepath).getFile();
        BufferedReader br = new BufferedReader(new FileReader(csvFile));

        int columnCount = 0;

        String lineItem = "";
        for(int lineCounter = 0; (lineItem = br.readLine()) != null; lineCounter++) {

            if(lineCounter == 0) {
                String[] lineData = lineItem.split(cvsSplitBy);
                columnCount = lineData.length;
                continue;
            }

            String[] testdata = new String[columnCount];

            for (int i=0; i < testdata.length; i++){
                testdata[i] = "";
            }

            String[] data = lineItem.split(cvsSplitBy);
            for (int i=0; i < data.length; i++){
                testdata[i] = data[i];
            }

            testCases.add(testdata);

        }

        return testCases.iterator();
    }
}
