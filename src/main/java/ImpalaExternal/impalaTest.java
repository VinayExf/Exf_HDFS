package ImpalaExternal;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;

import org.apache.hadoop.fs.Path;
import org.apache.log4j.Logger;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.RowFactory;
import org.apache.spark.sql.types.DataTypes;
import org.apache.spark.sql.types.StructField;
import org.apache.spark.sql.types.StructType;
import org.bson.Document;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.simple.JSONArray;

import com.exf.Utils.Impalaconstant;



public class impalaTest {
	static Logger logger = Logger.getLogger(impalaTest.class);
	static JSONObject jsontxtarr = new JSONObject();
	static Dataset<Row> txtrowencrypt;
	static Dataset<Row> txtPlainrow;
	public static void main(String[] args) throws ClassNotFoundException, SQLException, IllegalArgumentException, IOException, JSONException, ParseException {

		impalaTest imptest = new impalaTest();
		Impalaconstant.loadProp();
		
		BufferedReader br = null;
	if(Impalaconstant.pathtype.equalsIgnoreCase("hdfs"))
		br = new BufferedReader(new InputStreamReader (Impalaconstant.fileSystem.open(new Path(Impalaconstant.TextFile))));
	else if(Impalaconstant.pathtype.equalsIgnoreCase("local"))
		 br = new BufferedReader(new FileReader(new File(Impalaconstant.TextFile)));
	else{
		logger.info("No pathType has been selected in impala.properties! Program Terminated");
		System.exit(0);
	}
	txtPlainrow = Impalaconstant.sparkimpala.read().format("com.databricks.spark.csv").option("header", "true").option("delimiter", ",").load(Impalaconstant.TextFile);
	
	JSONArray arrtxt = new JSONArray();
		
	 String line1=null;
	// List<Integer> index =new ArrayList<Integer>();
	 while((line1=br.readLine())!=null){
		 String[] colsplit =line1.split(",");
		 for(int i=0;i<colsplit.length;i++){

				JSONObject temp1 = new JSONObject();
				temp1.put("name", colsplit[i]);
				temp1.put("tablename", Impalaconstant.apiTabelname);
				arrtxt.add(temp1);
		 }
		 break;
	 }
	 JSONArray jsonrows = new JSONArray();
	//Taking particular columns from the text file 
	 while ((line1 = br.readLine()) != null){
	  	String[] splitrow = line1.split(",");
	  	int assign = 0;
	  	//JSONObject temp = new JSONObject();
	  	LinkedHashMap<String, Object> temp = new LinkedHashMap<String, Object>();
	  	//for (int i : index) {
	  	for(int i=0;i<splitrow.length;i++){
	  			temp.put(String.valueOf(assign), splitrow[i]);
	  			assign++;
	  	 }
	  	jsonrows.add(temp);
	 }
	 //preparing path7 api call
	 jsontxtarr.put("RECORDS", jsonrows);
	 jsontxtarr.put("COLUMNS", arrtxt);
	 jsontxtarr.put("aid", Impalaconstant.appID);
	 jsontxtarr.put("tid", "0");
	 logger.debug(jsontxtarr.toString());
	 
	 
	 JSONObject outputjson = imptest.readJsonFromUrl("http://"+Impalaconstant.apiHost+":45670/path7", jsontxtarr);
		Document doc = Document.parse(outputjson.toString());
		ArrayList<Document> columns = (ArrayList<Document>) doc.get("COLUMNS");
		List<String> columnames = new ArrayList<String>();
		for(int i=0;i<columns.size(); i++){
			Document doc2 = columns.get(i);
			columnames.add(doc2.get("name").toString());
		}
		
			List<Row> data2 =new ArrayList<Row>();
	        List<Row> arrfulldata = new ArrayList<Row>();
	       
			
			ArrayList<Document> records =(ArrayList<Document>) doc.get("RECORDS");
			for(int i=0;i<records.size(); i++){
				Document doc2 = records.get(i);
				Set<String> keys = doc2.keySet();
				String val ="";
				 ArrayList<Integer> set=new ArrayList<Integer>();  
				 for(String s: keys)
					 set.add(Integer.parseInt(s));
				Collections.sort(set);
				for(int keyset : set)
					val +=doc2.get(String.valueOf(keyset))+",";
				 val = val.substring(0, val.length()-1);
				 data2 = Arrays.asList(
					        RowFactory.create(val.split(",")));
				 arrfulldata.addAll(data2);
			}
			StructType schema = imptest.createSchema(columnames);
		    txtrowencrypt = Impalaconstant.sparkimpala.createDataFrame(arrfulldata, schema);
		    impalaPush.hadoop(txtrowencrypt,txtPlainrow);   
		    // rdtxt.hadoop(txtrowencrypt);
		    
 
		
		


		
	}
	public static JSONObject readJsonFromUrl(String url, JSONObject jsonip) throws IOException, JSONException, java.text.ParseException {
		//InputStream is = new URL(url).openStream();
		URL object = new URL(url);
		HttpURLConnection con = (HttpURLConnection) object.openConnection();
		try {
			
			con.setDoOutput(true);
			con.setDoInput(true);
			con.setRequestProperty("Content-Type", "application/json");
			con.setRequestProperty("Accept", "application/json");
			con.setRequestMethod("POST");
			 OutputStream os = con.getOutputStream();
	            os.write(jsonip.toString().getBytes());
	            os.flush();
			BufferedReader rd = new BufferedReader(new InputStreamReader(con.getInputStream()));
			String jsonText = readAll(rd);
			JSONObject json = new JSONObject(jsonText);
			return json;
		} finally {
			con.disconnect();
		}
	}
	/**
	 * This method reads the API output as a Reader and reads line by and converts
	 * to the String.
	 * @param rd Reader from the API output
	 * @return String sb
	 * @throws IOException
	 */
	public static String readAll(Reader rd) throws IOException {
		StringBuilder sb = new StringBuilder();
		int cp;
		while ((cp = rd.read()) != -1) {
			sb.append((char) cp);
		}
		return sb.toString();
	}
	/**
	 * This method reads the array and creates structType for every columns as 
	 * String
	 * 
	 * @param tableColumns all the encrypted columns 
	 * @return StructType it will creates everycolumns as a String.
	 */
	 public static StructType createSchema(List<String> tableColumns){

	        List<StructField> fields  = new ArrayList<StructField>();
	        for(String column : tableColumns){         

	                fields.add(DataTypes.createStructField(column, DataTypes.StringType, true));            

	        }
	        return DataTypes.createStructType(fields);
	    }
}
