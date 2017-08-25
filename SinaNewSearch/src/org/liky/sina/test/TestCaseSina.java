package org.liky.sina.test;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.MapFile.Reader;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.MapFileOutputFormat;
import org.junit.Test;

public class TestCaseSina {
	@Test
	public void test() throws Exception {
		Configuration conf = new Configuration();
		Path path = new Path("hdfs://localhost:9000/output_news_map");
		FileSystem fs = path.getFileSystem(conf);
		Reader reader = MapFileOutputFormat.getReaders(fs, path, conf)[0];
		Text value = (Text) reader.get(new Text("印度"), new Text());
		System.out.println(value);
	}
}
