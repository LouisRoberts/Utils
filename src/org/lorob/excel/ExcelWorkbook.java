package org.lorob.excel;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;

/**
 * @author Grrob
 *
 * Excel utilities using open source excel library from
 * apache
 */
public class ExcelWorkbook {
    
   /**
     * @param sheet1 The String[] should have two elements, the first should be
     *            the input file name, the second the input sheet name.
     * @return The contents as a String array. The columns are padded
     * on the right with spaces to make each row have the same no of columns.
     * Reads in the specified Excel spreadsheet sheet and returns the contents as an
     * array of String arrays.
     */
    public static String[][] sheetToArray(String[] sheet1) {
	    String[][] results = null;
	    List[] res = null;
        try {
            // Sheet 1
            POIFSFileSystem fs1 = new POIFSFileSystem(new FileInputStream(sheet1[0]));
            HSSFWorkbook wb1 = new HSSFWorkbook(fs1);
            HSSFSheet sh1 = wb1.getSheet(sheet1[1]);
            
            System.out.println( sheet1[0]);
            System.out.println( sheet1[1]);

            // Create output array of correct size
            int sh1RowStart = sh1.getFirstRowNum();
            int sh1RowEnd = sh1.getLastRowNum();
            
            res = new List[sh1RowEnd - sh1RowStart + 1];

            int currRes = 0;
            for(int a = sh1RowStart; a <= sh1RowEnd; a++) {
                HSSFRow row = sh1.getRow(a);
                res[currRes] = new ArrayList();
                short sh1CellStart = row.getFirstCellNum();
                short sh1CellEnd = row.getLastCellNum();
                for(short b = sh1CellStart; b < sh1CellEnd; b++) {
                    
                    HSSFCell cell = row.getCell(b);
                    int cellType = cell==null ? 0 : cell.getCellType();
                    if (cellType == HSSFCell.CELL_TYPE_BLANK || cellType == 0) {
                        res[currRes].add("");
                    } else if (cellType == HSSFCell.CELL_TYPE_BOOLEAN) {
                        res[currRes].add(cell.getBooleanCellValue() ? "TRUE" : "FALSE");
                    } else if (cellType == HSSFCell.CELL_TYPE_ERROR) {
                        res[currRes].add("ERROR");
                    } else if (cellType == HSSFCell.CELL_TYPE_FORMULA) {
                        res[currRes].add(cell.getRichStringCellValue().toString());
                    } else if (cellType == HSSFCell.CELL_TYPE_NUMERIC) {
                        res[currRes].add(Double.toString(cell.getNumericCellValue()));
                    } else if (cellType == HSSFCell.CELL_TYPE_STRING) {
                        res[currRes].add(cell.getRichStringCellValue().toString());
                    } else {
                        throw new RuntimeException("Error in ExcelWorkbook");
                    }
                    
                }
                currRes++;
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        if (res != null) {
            int max = 0;
            for(int a = 0; a<res.length; a++) {
                if (res[a].size() > max) {
                    max = res[a].size();
                }
            }
            results = new String[res.length][max];
            for(int a = 0; a<res.length; a++) {
                for(int b=0; b<max; b++) {
                    if (b<res[a].size()) {
                        results[a][b] = (String)res[a].get(b);
                    } else {
                        results[a][b] = "";
                    }
                }
            }
        }
        
	    return results;
	}    
    
   /**
     * Copies the specified Excel worksheets from the specified spreadies into
     * the specified output Excel file giving each sheet the specified name and
     * position.
     * 
     * @param source
     *            Each String[] should have two elements, the first should be
     *            the input file name, the second the input sheet name.
     *            Optionally there can be a third element which should be the
     *            row number at which to start copying - defaults to first row.
     * @param outputFilename
     *            The name of the output file.
     * @param target
     *            Each String[] should have two elements, the first should be
     *            the sheet number as a String and the second should be the
     *            sheet name.
     */
    public static void copyWorkbook(String[][] source, String outputFilename, String[][] target) {
        try {
            HSSFWorkbook outputWB = new HSSFWorkbook();

            FileOutputStream out = new FileOutputStream(outputFilename);

            for (int count = 0; count < source.length; count++) {
                int outputSheetno = Integer.parseInt(target[count][0]);
                String outputSheetname = target[count][1];
                String inputFilename = source[count][0];
                String inputSheetname = source[count][1];
                int startRowShift = 0;
                if (source[count].length > 2) {
                    startRowShift = Integer.parseInt(source[count][2]);
                }

                HSSFSheet outputSheet = outputWB.createSheet();
                outputWB.setSheetName(outputSheetno, outputSheetname);
                POIFSFileSystem fs = new POIFSFileSystem(new FileInputStream(inputFilename));
                HSSFWorkbook wb = new HSSFWorkbook(fs);
                HSSFSheet sh = wb.getSheet(inputSheetname);
                int lastRowNum = sh.getLastRowNum();
                int newRowNo = 0;
                for (int f = sh.getFirstRowNum() + startRowShift; f <= lastRowNum; f++) {
                    HSSFRow rw = sh.getRow(f);
                    if (rw != null) {
                        HSSFRow outputRow = outputSheet.createRow(newRowNo++);
                        short lastCellNum = rw.getLastCellNum();
                        for (short c = rw.getFirstCellNum(); c <= lastCellNum; c++) {
                            HSSFCell cl = rw.getCell(c);
                            if (cl != null) {
                                HSSFCell outputCell = outputRow.createCell(c);
                                if (cl.getCellType() == HSSFCell.CELL_TYPE_BLANK) {

                                } else if (cl.getCellType() == HSSFCell.CELL_TYPE_BOOLEAN) {
                                    outputCell.setCellValue(cl.getBooleanCellValue());
                                } else if (cl.getCellType() == HSSFCell.CELL_TYPE_ERROR) {
                                    outputCell.setCellValue(new HSSFRichTextString("ERROR"));
                                } else if (cl.getCellType() == HSSFCell.CELL_TYPE_FORMULA) {
                                    outputCell.setCellValue(new HSSFRichTextString(cl.getRichStringCellValue()
                                            .toString()));
                                } else if (cl.getCellType() == HSSFCell.CELL_TYPE_NUMERIC) {
                                    outputCell.setCellValue(cl.getNumericCellValue());
                                } else if (cl.getCellType() == HSSFCell.CELL_TYPE_STRING) {
                                    outputCell.setCellValue(new HSSFRichTextString(cl.getRichStringCellValue()
                                            .toString()));
                                } else {
                                    throw new RuntimeException("Error in ExcelWorkbook");
                                }
                            }
                        }
                    } else {
                        break;
                    }
                }

            }
            outputWB.write(out);
            out.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Write the handed results to the file with sheetname
     * @param filename
     * @param sheetname
     * @param results
     */
    public static void outputResultsToExcel(String filename, String sheetname,String[][] results)
    {
        try {
            HSSFWorkbook outputWB = new HSSFWorkbook();
            FileOutputStream out = new FileOutputStream(filename);
            HSSFSheet outputSheet = outputWB.createSheet();
            outputWB.setSheetName(0, sheetname);

            if (results == null) 
            {
                //System.out.println("No results.");
            }
            else 
            {
                //System.out.println("Results");
                for (int i = 0; i < results.length; i++) {
                    HSSFRow outputRow = outputSheet.createRow(i);
                    for (short j = 0; j < results[i].length; j++) {
                        HSSFCell outputCell = outputRow.createCell(j);
                        String q = results[i][j];
                        outputCell.setCellValue(new HSSFRichTextString(q));
                    }
                }
            }
            outputWB.write(out);
            out.close();
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    
    /**
     * Creates the specified Excel file, with the sheets in the specified array. Each sheet
     * has the corresponding 2 dimensional array of results dumped into it, ie sheetname[1] corresponds
     * to results[1] etc.
     */
    public static void outputResultsToExcel(String filename, String[] sheetname,String[][][] results)
    {
        try {
            HSSFWorkbook outputWB = new HSSFWorkbook();
            FileOutputStream out = new FileOutputStream(filename);
            for(int sh=0; sh<sheetname.length; sh++) {
	            HSSFSheet outputSheet = outputWB.createSheet();
	            outputWB.setSheetName(sh, sheetname[sh]);
	
	            if (results == null) 
	            {
	                //System.out.println("No results.");
	            }
	            else 
	            {
	                //System.out.println("Results");
	                //System.out.println( sh);
	                for (int i = 0; i < results[sh].length; i++) {
	                    HSSFRow outputRow = outputSheet.createRow(i);
	                    for (short j = 0; j < results[sh][i].length; j++) {
	                        HSSFCell outputCell = outputRow.createCell(j);
	                        String q = results[sh][i][j];
	                        outputCell.setCellValue(new HSSFRichTextString(q));
	                    }
	                }
	            }
            }
            outputWB.write(out);
            out.close();
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }    
    
    public static void fireExcel(String file)
    	throws IOException
	{
    	// Launch Excel
		try
		{
			Runtime.getRuntime().exec("cmd /C start /B excel.exe \""+file+"\"");
		}
		catch(IOException ioe)
		{
			throw ioe;
		}
	}

}

