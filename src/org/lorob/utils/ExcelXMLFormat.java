package org.lorob.utils;


/**
 * ExcelXMLFormat is a calss that can aid the creation of multi tabbed XML
 * format EXCEL reports. To create a report use code as in the following:
 *  data is the date for the first tab. data 2 is the data for the second tab 
 * 		String[][] data={
 *				{"a1","b1","c1"}, 
 *				{"a2","b2","c2"} };
 *		String[][] data2={
 *				{"aa1","bb1","cc1","dd1"},
 *				{"aa2","bb2","cc2","dd2"},
 *				{"aa3","bb3","cc3","dd3"}
 *				};
 *		StringBuffer excelWorkseet=new StringBuffer(getExcelWorkBookStart());
 *		excelWorkseet.append(createWorkSheetFromData(data,"page 1"));
 *		excelWorkseet.append(createWorkSheetFromData(data2,"page 2"));
 *		excelWorkseet.append(getExcelWorkBookEnd());
 *
 * The excelWorksheet will now have an EXCEL xml formatted report whih can be sent to a file
 * for loading into EXCEL. 
 * @author lorob
 *
 */
public class ExcelXMLFormat
{
	private static String EXCEL_START="<?xml version=\"1.0\"?>\n"+
		 "<Workbook xmlns=\"urn:schemas-microsoft-com:office:spreadsheet\"\n"+
		 "xmlns:o=\"urn:schemas-microsoft-com:office:office\"\n"+
		 "xmlns:x=\"urn:schemas-microsoft-com:office:excel\"\n"+
		 "xmlns:ss=\"urn:schemas-microsoft-com:office:spreadsheet\"\n"+
		 "xmlns:html=\"http://www.w3.org/TR/REC-html40\">\n"+
		 "<DocumentProperties xmlns=\"urn:schemas-microsoft-com:office:office\">\n"+
		 "<Author>lorob</Author>\n"+
		 "<LastAuthor>lorob</LastAuthor>\n"+
		 "<Created>2007-04-13T13:54:15Z</Created>\n"+
		 "<Company>PPA</Company>\n"+
		 "<Version>10.2625</Version>\n"+
		 "</DocumentProperties>\n"+
		 "<OfficeDocumentSettings xmlns=\"urn:schemas-microsoft-com:office:office\">\n"+
		 "<DownloadComponents/>\n"+
		 "<LocationOfComponents HRef=\"file:///\\\\distribute_srv\\APPS\\Microsoft\\Office\\MS%20Office%20XP%20Professional\\\"/>\n"+
		 "</OfficeDocumentSettings>\n"+
		 "<ExcelWorkbook xmlns=\"urn:schemas-microsoft-com:office:excel\">\n"+
		 "<WindowHeight>9345</WindowHeight>\n"+
		 "<WindowWidth>11340</WindowWidth>\n"+
		 "<WindowTopX>480</WindowTopX>\n"+
		 "<WindowTopY>75</WindowTopY>\n"+
		 "<ProtectStructure>False</ProtectStructure>\n"+
		 "<ProtectWindows>False</ProtectWindows>\n"+
		 "</ExcelWorkbook>\n"+
		 "<Styles>\n"+
		 "<Style ss:ID=\"Default\" ss:Name=\"Normal\">\n"+
		 "<Alignment ss:Vertical=\"Bottom\"/>\n"+
		 "<Borders/>\n"+
		 "<Font/>\n"+
		 "<Interior/>\n"+
		 "<NumberFormat/>\n"+
		 "<Protection/>\n"+
		 "</Style>\n"+
		 "<Style ss:ID=\"s21\">"+
		 "<Font x:Family=\"Swiss\" ss:Bold=\"1\"/>"+
		 "</Style>"+
		 "<Style ss:ID=\"s22\">"+
		 "<Font x:Family=\"Swiss\" ss:Bold=\"1\"/>"+
		 "<Interior ss:Color=\"#CCFFFF\" ss:Pattern=\"Solid\"/>"+
		 "</Style>"+
		 "<Style ss:ID=\"s23\">"+
		 "<Interior ss:Color=\"#C0C0C0\" ss:Pattern=\"Solid\"/>"+
		 "</Style>"+
		 "<Style ss:ID=\"s24\">"+
		 "<Interior ss:Color=\"#00FF00\" ss:Pattern=\"Solid\"/>"+
		 "</Style>"+
		 "<Style ss:ID=\"s25\">"+
		 "<Interior ss:Color=\"#FF0000\" ss:Pattern=\"Solid\"/>"+
		 "</Style>"+
		 "<Style ss:ID=\"s26\">"+
		 "<Interior ss:Color=\"#FFFF00\" ss:Pattern=\"Solid\"/>"+
		 "</Style>"+
		 "<Style ss:ID=\"s27\">"+
		 "<Interior ss:Color=\"#C0C0C0\" ss:Pattern=\"Solid\"/>"+
		 "<Font x:Family=\"Swiss\" ss:Bold=\"1\"/>"+
		 "</Style>"+
		 "<Style ss:ID=\"s28\">"+
		 "<Alignment ss:Vertical=\"Bottom\" ss:WrapText=\"1\"/>"+
   		 "<Borders>"+
		 "<Border ss:Position=\"Bottom\" ss:LineStyle=\"Continuous\" ss:Weight=\"1\"/>"+
    	 "<Border ss:Position=\"Left\" ss:LineStyle=\"Continuous\" ss:Weight=\"1\"/>"+
    	 "<Border ss:Position=\"Right\" ss:LineStyle=\"Continuous\" ss:Weight=\"1\"/>"+
    	 "<Border ss:Position=\"Top\" ss:LineStyle=\"Continuous\" ss:Weight=\"1\"/>"+
   	  	 "</Borders>"+
   		 "<Font x:Family=\"Swiss\" ss:Bold=\"1\"/>"+
		 "</Style>"+
		 "<Style ss:ID=\"s29\">"+
		 "<Alignment ss:Vertical=\"Bottom\" ss:WrapText=\"1\"/>"+
   		 "<Borders>"+
		 "<Border ss:Position=\"Bottom\" ss:LineStyle=\"Continuous\" ss:Weight=\"1\"/>"+
    	 "<Border ss:Position=\"Left\" ss:LineStyle=\"Continuous\" ss:Weight=\"1\"/>"+
    	 "<Border ss:Position=\"Right\" ss:LineStyle=\"Continuous\" ss:Weight=\"1\"/>"+
    	 "<Border ss:Position=\"Top\" ss:LineStyle=\"Continuous\" ss:Weight=\"1\"/>"+
   	  	 "</Borders>";
	
	private static String EXCEL_STYLE_END="</Style></Styles>\n";
	
	private static String EXCEL_END="</Workbook>\n";
	
	private static String WORKSHEET_START=" <Worksheet ss:Name=\"";
	private static String WORKSHEET_START_END="\">";
	private static String WORKSHEET_END="</Worksheet>\n";
	
	private static String WORKSHEET_OPTIONS="<WorksheetOptions xmlns=\"urn:schemas-microsoft-com:office:excel\">\n"+
   "<Selected/>\n"+
   "<Panes>\n"+
    "<Pane>\n"+
     "<Number>3</Number>\n"+
     "<ActiveRow>0</ActiveRow>\n"+
     "<ActiveCol>0</ActiveCol>\n"+
    "</Pane>\n"+
   "</Panes>\n"+
   "<ProtectObjects>False</ProtectObjects>\n"+
   "<ProtectScenarios>False</ProtectScenarios>\n"+
  "</WorksheetOptions>\n";
	
	
	
	
	/**
	 * Create a worksheet from the data passed in
	 * @param data - a 2d array of data to put in cells 
	 * @param name - the name for the tab
	 * @return
	 */
	public static StringBuffer createWorkSheetFromData(String[][] data,String name)
	{
		StringBuffer buffer=new StringBuffer(WORKSHEET_START);
		buffer.append(name);
		buffer.append(WORKSHEET_START_END);
		
		int rows=data.length;
		int cols=data[0].length;
		buffer.append("\n<Table ss:ExpandedColumnCount=\"");
		buffer.append(cols);
		buffer.append("\" ss:ExpandedRowCount=\"");
		buffer.append(rows);
		buffer.append("\" x:FullColumns=\"1\" x:FullRows=\"1\">\n");
		
		
		for(int i=0;i<rows;i++)
		{
			buffer.append("<Row>\n");
			for(int j=0;j<cols;j++)
			{
				String thisData=(String)data[i][j];
				if(i==0)
				{
					buffer.append("<Cell ss:StyleID=\"s22\"><Data ss:Type=\"String\">");
				}
				else
				{
					buffer.append("<Cell><Data ss:Type=\"String\">");
				}
				buffer.append(Strings.makeXMLCompatible(thisData));
				buffer.append("</Data></Cell>\n");
			}
			buffer.append("</Row>\n");
		}
		
		
		buffer.append("</Table>\n");
		buffer.append(WORKSHEET_OPTIONS);
		buffer.append("\n");
		buffer.append(WORKSHEET_END);
		return buffer;
	}
	
	/**
	 * Return the excel start of a workbook
	 * @return
	 */
	public static String getExcelWorkBookStart(String additionalStyles)
	{
		StringBuffer string=new StringBuffer(EXCEL_START);
		if(additionalStyles!=null)
		{
			string.append(additionalStyles);
		}
		string.append(EXCEL_STYLE_END);
		return string.toString();
	}
	
	/**
	 * Return the excel end of a workbook
	 * @return
	 */
	public static String getExcelWorkBookEnd()
	{
		return EXCEL_END;
	}
	
		
	/**
	 * This is a small test 
	 * @param args
	 */
	public static void main(String[] args)
	{
		String[][] data={
				{"a1","b1","c1"},
				{"a2","b2","c2"} };
		String[][] data2={
				{"aa1","bb1","cc1","dd1"},
				{"aa2","bb2","cc2","dd2"},
				{"aa3","bb3","cc3","dd3"}
				};
		StringBuffer excelWorkseet=new StringBuffer(getExcelWorkBookStart(null));
		excelWorkseet.append(createWorkSheetFromData(data,"page 1"));
		excelWorkseet.append(createWorkSheetFromData(data2,"page 2"));
		excelWorkseet.append(getExcelWorkBookEnd());
		System.out.println(excelWorkseet.toString());
	}
}
