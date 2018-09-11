package com.exf.Utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.spark.sql.SparkSession;

public class Impalaconstant {

	public static String hdfsPath;
	public static String TextFile;
	public static String pathtype;
	public static String impalaDatabase;
	public static String impalaPlaintable;
	public static String impalaEncryptedtable;
	public static String impalaUser;
	public static String impalaPassword;
	public static String appID;
	public static String apiHost;
	public static String apiTabelname;
	public static SparkSession sparkimpala;
	public static FileSystem fileSystem;
	
	
	public static void loadProp(){
		
		try{
			String path = System.getProperty("user.dir")+"/impala.properties";
		InputStream in = new FileInputStream(new File(path));
				//"/home/exa1/Desktop/impala.properties"));
		Properties properties = new Properties();
		properties.load(in);
		
		hdfsPath = properties.getProperty("HDFS.Path").trim(); 
		TextFile=properties.getProperty("TextFilePath").trim();
		pathtype=properties.getProperty("Path.Type").trim().toLowerCase();
		impalaDatabase=properties.getProperty("Impala.Database").trim().toLowerCase();
		impalaPlaintable = properties.getProperty("ImpalaPlain.TableName").trim();
		impalaEncryptedtable = properties.getProperty("ImpalaEncrypted.TableName").trim();
		impalaUser = properties.getProperty("Impala.user").trim();
		impalaPassword = properties.getProperty("Impala.password").trim();
		appID = properties.getProperty("ApplicationID").trim();
		apiHost = properties.getProperty("APIJarIP").trim();
		apiTabelname = properties.getProperty("Migration.Tablename").trim();
		
		//SparkSession
		sparkimpala = SparkSession.builder().master("local").appName("Impala Connectivity").getOrCreate();
		
		//hdfs
		String impalaurl = hdfsPath.split("//")[1];
		String hdfs = impalaurl.split(":")[0];
		
		Configuration conf = new Configuration ();
		conf.set("fs.hdfs.impl", org.apache.hadoop.hdfs.DistributedFileSystem.class.getName());
        conf.set("fs.file.impl", org.apache.hadoop.fs.LocalFileSystem.class.getName());
        conf.addResource(new Path("/etc/hadoop/conf/core-site.xml"));
        conf.addResource(new Path("/etc/hadoop/conf/hdfs-site.xml"));
        conf.addResource(new Path("/etc/hadoop/conf/mapred-site.xml"));
        conf.set("fs.default.name", "hdfs://"+hdfs.trim()+":8020");
        conf.set("fs.defaultFS", "hdfs://"+hdfs.trim()+":8020");
        conf.set("hadoop.ssl.enabled", "false");

        fileSystem = FileSystem.get(conf);
			
		}catch (Exception e) {
			e.printStackTrace();
		}
	}

}
