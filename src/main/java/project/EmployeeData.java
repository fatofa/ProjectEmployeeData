package project;


import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class EmployeeData
{

    private static final String DATA_TYPES_READ = "src/main/resources/data/Du lieu may cham cong 1.11-sang 11.11.xlsx";
    private static final String DATA_TYPES_WRITE = "src/main/resources/data/data.txt";
    private static int countID = 0;

    public static void main(String[] args) {
        Map<String, String> idAndName = new HashMap<String, String>();
        Map<String, Long> idAndTime = new HashMap<String, Long>();
        Map<String, Float> idAndDay = new HashMap<String, Float>();


        countEmployee(idAndName, idAndTime, idAndDay);
        countDays(idAndName, idAndTime, idAndDay);
//        System.out.println("Sum Employee is: " + (idAndName.size() - 2));
//        int i = -1;
//        for (Map.Entry<String, String> m : idAndName.entrySet()) {
//            i++;
//            if (i == 0) {
//                continue;
//            } else if (i == (idAndName.size() - 1)) {
//                break;
//            } else {
//
//                System.out.println(m.getKey() + " : " + m.getValue() + " : " + idAndDay.get(m.getKey()) + " : " + (((float)((idAndTime.get(m.getKey()) / 3600) - 1)) > 0 ? ((float)((idAndTime.get(m.getKey()) / 3600) - 1)) : 0 ));
//            }
//
//        }

        writeFile(idAndName, idAndTime, idAndDay);
    }
    public static void countEmployee(Map<String, String> names , Map<String, Long> times , Map<String, Float> days) {
        try(FileInputStream fis = new FileInputStream(DATA_TYPES_READ)) {
            Workbook workbook = new XSSFWorkbook(fis);
            Sheet mySheet = workbook.getSheetAt(0);
            Iterator<Row> it = mySheet.iterator();
            while(it.hasNext()) {
                Row currRow = it.next();
                Cell id = currRow.getCell(2);
                Cell name = currRow.getCell(3);
                String currId = "", currName = "";
                if(name != null && id != null) {
                    if (id.getCellType() == CellType.STRING && name.getCellType() == CellType.STRING) {
                        currId = id.getStringCellValue();
                        currName = name.getStringCellValue();
                        if(currId.length() < 1 || currId.equalsIgnoreCase("ID")) {
                            countID++;
                        }
                    }
                    names.put(currId, currName);
                    times.put(currId , 0L);
                    days.put(currId , 0F);
                }
            }
            System.out.println("Successfully count employee.");
            workbook.close();
        } catch(IOException e) {
            e.printStackTrace();
        }
    }


    public static void countDays(Map<String, String> names , Map<String, Long> times , Map<String, Float> days) {
        try(FileInputStream fis = new FileInputStream(DATA_TYPES_READ)) {
            Workbook workbook = new XSSFWorkbook(fis);
            Sheet mySheet = workbook.getSheetAt(0);
            Iterator<Row> it = mySheet.iterator();
            float countDay = 1;
            while(it.hasNext()) {
                Row currRow = it.next();
                // lay id nhan vien
                Cell id = currRow.getCell(2);
                // Check in and check out
                Cell timeIn = currRow.getCell(4);
                Cell timeOut = currRow.getCell(5);
                // Time check in and check out chuẩn theo ca (đang lấy theo khung 8h - 17h)
                Cell timeInCheck = currRow.getCell(7);
                Cell timeOutCheck = currRow.getCell(8);
                String currId = "";
                long time = 0;
                if(timeIn != null && timeOut != null && timeInCheck != null && timeOutCheck != null && timeIn.getCellType() == CellType.NUMERIC && timeOut.getCellType() == CellType.NUMERIC && id.getCellType() == CellType.STRING) {
                    currId = id.getStringCellValue();
                    // CHECK TIME ĐI MUỘN VỀ SỚM
                    if (!checkTime(timeIn.getDateCellValue(), timeInCheck.getDateCellValue(), "IN")) {
                        time += compareTime(timeIn.getDateCellValue(), timeInCheck.getDateCellValue());
                    }

                    if (!checkTime(timeOut.getDateCellValue(), timeOutCheck.getDateCellValue(), "OUT") && (checkDays(timeIn.getDateCellValue(), timeOut.getDateCellValue()) == 1)) {
                        time += compareTime(timeOut.getDateCellValue(), timeOutCheck.getDateCellValue());
                    }

                    // GÁN LẠI GIÁ TRỊ CHO THỜI GIAN ĐI MUỘN VÀ NGÀY CÔNG SAU MỖI NGÀY ĐI LÀM (NẾU CÓ)
                    long timeTemp = times.get(currId);
                    timeTemp += time;

                    times.put(currId, timeTemp);

                    float dayTemp = days.get(currId);
                    dayTemp += checkDays(timeIn.getDateCellValue(), timeOut.getDateCellValue());

                    days.put(currId, dayTemp);
                }
            }
            workbook.close();
            System.out.println("Successfully count times and days.");
        } catch(IOException | ParseException e) {
            e.printStackTrace();
        }
    }

    private static long compareTime(Date dateCellValue, Date dateCellValue1) throws ParseException {
        SimpleDateFormat simpleDateFormat
                = new SimpleDateFormat("HH:mm:ss");

        // Calculating the difference in milliseconds
        long differenceInMilliSeconds
                = Math.abs(dateCellValue1.getTime() - dateCellValue.getTime());

        // Calculating the difference in Hours
        long differenceInHours
                = (differenceInMilliSeconds / (60 * 60 * 1000))
                % 24;

        // Calculating the difference in Minutes
        long differenceInMinutes
                = (differenceInMilliSeconds / (60 * 1000)) % 60;

        // Calculating the difference in Seconds
        long differenceInSeconds
                = (differenceInMilliSeconds / 1000) % 60;

        // Printing the answer
//        System.out.println(
//                "Difference is " + differenceInHours + " hours "
//                        + differenceInMinutes + " minutes "
//                        + differenceInSeconds + " Seconds. ");
        return (differenceInHours * 60 * 60) + (differenceInMinutes * 60) + (differenceInSeconds);
    }

    private static float checkDays(Date dateCellValue, Date dateCellValue1) throws ParseException {
        SimpleDateFormat simpleDateFormat
                = new SimpleDateFormat("HH:mm:ss");

        // Calculating the difference in milliseconds
        long differenceInMilliSeconds
                = Math.abs(dateCellValue1.getTime() - dateCellValue.getTime());

        // Calculating the difference in Hours
        long differenceInHours
                = (differenceInMilliSeconds / (60 * 60 * 1000))
                % 24;

        // Calculating the difference in Minutes
        long differenceInMinutes
                = (differenceInMilliSeconds / (60 * 1000)) % 60;

        // Calculating the difference in Seconds
        long differenceInSeconds
                = (differenceInMilliSeconds / 1000) % 60;

        // Printing the answer
        float check =  differenceInHours + (float)(differenceInMinutes / 60) + (float)(differenceInSeconds / 3600);

        // Trường hợp làm buổi sáng sau đó đầu giờ chiều mới ra về -> nửa công
        if (check < 5.7) return 0.5F;
        else return 1.0F;
    }

    private static boolean checkTime(Date dateCellValue, Date dateCellValue1, String checkInOut) throws ParseException {
        boolean check = true;

        try  {
            if (checkInOut == "IN") {
                // CHECK IN
                if (dateCellValue.getTime() <= dateCellValue1.getTime()) {
                    check = true;
                } else {
                    check = false;
                }
            }
            else if (checkInOut == "OUT") {
                // CHECK OUT
                if (dateCellValue.getTime() >= dateCellValue1.getTime()) {
                    check = true;
                } else {
                    check = false;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return check;
    }

    public static void writeFile(Map<String, String> names , Map<String, Long> times , Map<String, Float> days) {
        try {
            FileWriter myWriter = new FileWriter(DATA_TYPES_WRITE);

            myWriter.write("Sum Employee is: " + (names.size() - 1 - countID) + "\n");
            myWriter.write("ID : NAME : DAYS : TIME ĐI MUỘN TRONG THÁNG (ĐÃ TRỪ 1H/1 THÁNG)" + "\n");
            for (Map.Entry<String, String> m : names.entrySet()) {
                if(m.getKey().length() < 1 || m.getKey().equalsIgnoreCase("ID")) {
                    continue;
                }
                // CHUYỂN TIME ĐI MUỘN THÀNH GIỜ -> MỖI THÁNG CÓ 60 PHÚT ĐI MUỘN NÊN TRỪ ĐI 1H
                float countTime = ((float)times.get(m.getKey()) / 3600 ) - 1F;
                myWriter.write(m.getKey() + " : " + m.getValue() + " : " + days.get(m.getKey()) + " : " + ( countTime > 0 ? countTime : 0 ) + "\n");
            }
            myWriter.close();
            System.out.println("Successfully wrote to the file.");
        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }
}
