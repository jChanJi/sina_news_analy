package org.liky.sina.utils;
import java.io.IOException;
import java.util.Set;
import java.util.TreeSet;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.MapFile.Reader;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.MapFileOutputFormat;

public class HDFSUtils {

	private static Configuration conf = new Configuration();
	private static Path path = new Path(
			"hdfs://localhost:9000/output_news_map");
	private static FileSystem fs = null;
	static {
		try {
			fs = path.getFileSystem(conf);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static Integer[] getIdsByKeyword(String keyword) throws Exception {
		Reader reader = MapFileOutputFormat.getReaders(fs, path, conf)[0];
	
		Text value = (Text) reader.get(new Text(keyword), new Text());
	
		Set<Integer> set = new TreeSet<Integer>();
		String[] strs = value.toString().split(",");

		for (String str : strs) {
			set.add(Integer.parseInt(str));
		}
		
		System.out.println(set);
		
		return set.toArray(new Integer[0]);
	}

}
