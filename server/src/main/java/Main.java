import org.apache.poi.ss.formula.functions.Match;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import static spark.Spark.*;

public class Main {

    public static void main(String[] args) {

        //This is required to allow GET and POST requests with the header 'content-type'
        options("/*",
                (request, response) -> {
                        response.header("Access-Control-Allow-Headers",
                                "content-type");

                        response.header("Access-Control-Allow-Methods",
                                "GET, POST");


                    return "OK";
                });

        //This is required to allow the React app to communicate with this API
        before((request, response) -> response.header("Access-Control-Allow-Origin", "http://localhost:3000"));

        //TODO: Return JSON containing the candies for which the stock is less than 25% of it's capacity
        get("/low-stock", (request, response) -> {
            return getLowStockCandy();
        });

        //TODO: Return JSON containing the total cost of restocking candy
        post("/restock-cost", (request, response) -> {
            return calculateRestockCost(request.body());
        });

    }

    private static JSONArray getLowStockCandy() throws FileNotFoundException {
        String excelFilePath = "resources/Inventory.xlsx";
        FileInputStream inputStream = new FileInputStream(excelFilePath);
        try {
            Workbook wb = WorkbookFactory.create(inputStream);
            Sheet sheet = wb.getSheetAt(0);
            JSONArray array = new JSONArray();
            for (int i = 1; i < sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                double stock = row.getCell(1).getNumericCellValue();
                double capacity = row.getCell(2).getNumericCellValue();
                if (stock < capacity * 0.25) {
                    JSONObject item = new JSONObject();
                    item.put("sku", row.getCell(3).getNumericCellValue());
                    item.put("name", row.getCell(0).getStringCellValue());
                    item.put("stock", stock);
                    item.put("capacity", capacity);
                    array.put(item);
                }
            }
            return array;

        } catch(Exception e){
            System.out.println(e);
        }
        return null;
    }
    private static double calculateRestockCost(String orderDetails) throws FileNotFoundException {
        String excelFilePath = "resources/Distributors.xlsx";
        FileInputStream inputStream = new FileInputStream(excelFilePath);
        double restockCost = 0.0;
        JSONObject order = new JSONObject(orderDetails);
        try {
            Workbook wb = WorkbookFactory.create(inputStream);
            Iterator<String> keys = order.keys();
            while (keys.hasNext()) {
                String key = keys.next();
                double lowestCost = Double.POSITIVE_INFINITY;
                if (order.get(key) instanceof Object) {
                    for (int i = 0; i < wb.getNumberOfSheets(); i++) {
                        Sheet sheet = wb.getSheetAt(i);
                        for (int j = 1; j < sheet.getLastRowNum(); j++) {
                            if (sheet.getRow(j) != null) {
                                Row row = sheet.getRow(j);
                                if (row.getCell(1) != null && row.getCell(1).getNumericCellValue() == Integer.parseInt(key)) {
                                    double current = row.getCell(2).getNumericCellValue();
                                    lowestCost = Math.min(lowestCost, current);
                                }
                            }
                        }
                    }
                }
                restockCost += lowestCost * Integer.parseInt(order.get(key).toString());
            }
            return restockCost;
        } catch(Exception e) {
            System.out.println(e);
        }
        return restockCost;
    }
}
