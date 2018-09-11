package ImpalaExternal;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;


import org.apache.log4j.Logger;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;

import com.exf.Utils.Impalaconstant;

public class impalaPush {
	static Logger logger = Logger.getLogger(impalaPush.class);
	
	
	public static void hadoop(Dataset<Row> encRow, Dataset<Row> plainRow) throws SQLException, ClassNotFoundException {
		//Class.forName("org.apache.hive.jdbc.HiveDriver");
		Impalaconstant.loadProp();
		//hdfs
		String impalaurl = Impalaconstant.hdfsPath.split("//")[1];
		String hdfs = impalaurl.split(":")[0];
		//Connection con = DriverManager.getConnection("jdbc:hive2://"+hdfs+":21050/"+Impalaconstant.impalaDatabase+";auth=noSasl","","");
		//System.out.println("Impala connection established");
	//	Statement stmt = con.createStatement();
		String[] allcol = encRow.columns();
		String allcolumnsNames="";
		
		for(String s: allcol)
			allcolumnsNames+=s+" String, ";
		
		String encpath = Impalaconstant.hdfsPath+Impalaconstant.impalaEncryptedtable;
		String plainpath = Impalaconstant.hdfsPath+Impalaconstant.impalaPlaintable;
		
		allcolumnsNames = allcolumnsNames.substring(0, allcolumnsNames.length()-2);
		try{
		encRow.write().mode("append").format("com.databricks.spark.csv").option("header", "false").save(encpath);
		plainRow.repartition(1).write().mode("append").format("com.databricks.spark.csv").option("header", "false").save(plainpath);
		}finally{
			encRow.unpersist();
			plainRow.unpersist();
		}
		encpath = "'"+encpath+"'";
		plainpath = "'"+plainpath+"'";
		
		
		
	    //plain table creation		
		//String path ="hdfs://192.168.0.184:8020/user/cloudera/Individual/Individual_profile_schema.txt";
		//"'"+mig.hdfsPath+"/"+mig.jdbcTargetDatabasename+"/'";

		//Plain Table creation
		//String allcolumns = "CREATE EXTERNAL TABLE exf.test123 (STRING, swid STRING, customer_id STRING, email_address STRING, first_name STRING, last_name STRING, gender STRING, birthday STRING, geography_key STRING, age STRING, facebook_connect_flg STRING, employee_flg STRING, registration_date STRING, registration_affiliate_name STRING, marketing_effort_code_key STRING, registration_flg STRING, affluence STRING, affluent_suburbia STRING, numzip STRING, tenure STRING, postal_code STRING, country STRING, state_province STRING, state_abbreviation STRING, city STRING, dma_code STRING, dma_name STRING, msa_name STRING, age_range STRING, ethnic_code STRING, ethnic_group STRING, ethnic_group_cd STRING, household_income STRING, aerobic_exercise_ind STRING, asimilation_cd STRING, cable_tv_ind STRING, child_age_0to2_present STRING, child_age_3to5_present STRING, child_age_6to10_present STRING, child_age_11to15_present STRING, child_age_16to17_present STRING, card_holder STRING, gas_department_retail_card_holder STRING, travel_entertainment_card_holder STRING, premium_card_holder STRING, dwelling_type_cd STRING, first_individual_age_range_cd STRING, first_individual_education_cd STRING, first_individual_gender_cd STRING, first_individual_occ_cd STRING, home_assessed_value_num STRING, home_equity_available_cd STRING, home_market_value_cd STRING, home_owner_ind STRING, own_rent STRING, household_size_cd STRING, international_travel_ind STRING, investing_grouping_ind STRING, length_of_residence_cd STRING, likely_investors_ind STRING, marital_status STRING, net_worth_cd STRING, number_of_adults STRING, number_of_childern STRING, personal_investments_ind STRING, children_present STRING, running_exercise_ind STRING, satellite_dish_tv_ind STRING, second_individual_age_range_cd STRING, second_individual_education_cd STRING, second_individual_gender_cd STRING, stocks_bound_inverstment_ind STRING, vacation_travel_rv_ind STRING, vacation_travel_us_ind STRING) ROW FORMAT DELIMITED FIELDS TERMINATED BY ',' STORED AS TEXTFILE LOCATION 'hdfs://192.168.0.184:/user/cloudera/test/' ";
		
		/*stmt.execute("DROP TABLE IF EXISTS "+Impalaconstant.impalaDatabase+"."+Impalaconstant.impalaEncryptedtable);
		stmt.execute("DROP TABLE IF EXISTS "+Impalaconstant.impalaDatabase+"."+Impalaconstant.impalaPlaintable);
		//Enc
		String encquery = "CREATE EXTERNAL TABLE "+Impalaconstant.impalaDatabase+"."+Impalaconstant.impalaEncryptedtable+" ( "+allcolumnsNames+" ) ROW FORMAT DELIMITED FIELDS TERMINATED BY ',' STORED AS TEXTFILE LOCATION "+encpath;
		
		//Plain
		String plainquery = "CREATE EXTERNAL TABLE "+Impalaconstant.impalaDatabase+"."+Impalaconstant.impalaPlaintable+" ( "+allcolumnsNames+" ) ROW FORMAT DELIMITED FIELDS TERMINATED BY ',' STORED AS TEXTFILE LOCATION "+plainpath;
		logger.debug(allcolumnsNames);
		//encrypted external table
		stmt.execute(encquery);
		//Plain external table
		stmt.execute(plainquery);
		//stmt.execute("LOAD DATA 'hdfs://192.168.0.184:/user/cloudera/nation1.txt' INTO TABLE exf.test123");
		//loadstmt.execute(sql);
		con.close();*/
		Impalaconstant.sparkimpala.close();
		logger.info("Migration Completed");
	}

}
