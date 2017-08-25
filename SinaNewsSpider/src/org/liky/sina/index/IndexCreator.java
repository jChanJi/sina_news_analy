/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.liky.sina.index;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jeasy.analysis.MMAnalyzer;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.FileInputFormat;
import org.apache.hadoop.mapred.FileOutputFormat;
import org.apache.hadoop.mapred.JobClient;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapred.MapFileOutputFormat;
import org.apache.hadoop.mapred.MapReduceBase;
import org.apache.hadoop.mapred.Mapper;
import org.apache.hadoop.mapred.OutputCollector;
import org.apache.hadoop.mapred.Reducer;
import org.apache.hadoop.mapred.Reporter;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;

public class IndexCreator extends Configured implements Tool {

	// 这里我们重点需要关注的是Mapper接口中后两个泛型参数，第一个表示返回后Map的key的类型，第二个表示返回后value的类型
	public static class MapClass extends MapReduceBase implements
			Mapper<LongWritable, Text, Text, Text> {
		private static Pattern p;
		static {
			System.out.println("开始Map操作...");
			p = Pattern.compile("\\d+");
		}

		private int id;
		private int line = 1;

		private static MMAnalyzer mm = new MMAnalyzer();

		// 输出的词
		private Text word = new Text();

		// map过程的核心方法。
		public void map(LongWritable key, Text value,
				OutputCollector<Text, Text> output, Reporter reporter)
				throws IOException {
			if (line == 1) {
				// 读取的是第一行，我们就需要将第一行的id保留下来
				line++;
				Matcher m = p.matcher(value.toString());
				if (m.find()) {
					id = Integer.parseInt(m.group());
				}
			} else {
				String tempStr = value.toString();
				// 按空格将单词拆分出来
				// StringTokenizer itr = new StringTokenizer(line);
				// 使用分词器来进行词组的拆分
				String[] results = mm.segment(tempStr, "|").split("\\|");
				// 每个单词记录出现了1次
				for (String temp : results) {
					word.set(temp.toLowerCase());
					output.collect(word, new Text(id + ""));
				}
			}
		}
	}

	// 对所有结果进行合并
	// Reducer中也有泛型，前两个表示Map过程输出的结果类型，后两个表示Reduce处理后输出的类型
	// 一般开发中建议一致。
	public static class Reduce extends MapReduceBase implements
			Reducer<Text, Text, Text, Text> {

		static {
			System.out.println("开始Reduce操作....");
		}

		public void reduce(Text key, Iterator<Text> values,
				OutputCollector<Text, Text> output, Reporter reporter)
				throws IOException {
			// 将所有key值相同的结果，求和
			StringBuilder result = new StringBuilder();
			while (values.hasNext()) {
				String temp = values.next().toString();
				if (!result.toString().contains(temp + ",")) {
					result.append(temp + ",");
				}
			}
			output.collect(key,
					new Text(result.substring(0, result.length() - 1)));
			System.out.println(key + " ---> " + result);
		}
	}

	static int printUsage() {
		System.out
				.println("wordcount [-m <maps>] [-r <reduces>] <input> <output>");
		ToolRunner.printGenericCommandUsage(System.out);
		return -1;
	}

	public int run(String[] args) throws Exception {
		JobConf conf = new JobConf(getConf(), IndexCreator.class);
		conf.setJobName("wordcount");

		// 输出结果的Map的key值类型
		conf.setOutputKeyClass(Text.class);
		// 输出结果的Map的value值类型
		conf.setOutputValueClass(Text.class);

		conf.setMapperClass(MapClass.class);
		conf.setCombinerClass(Reduce.class);
		conf.setReducerClass(Reduce.class);

		List<String> other_args = new ArrayList<String>();
		for (int i = 0; i < args.length; ++i) {
			try {
				if ("-m".equals(args[i])) {
					conf.setNumMapTasks(Integer.parseInt(args[++i]));
				} else if ("-r".equals(args[i])) {
					conf.setNumReduceTasks(Integer.parseInt(args[++i]));
				} else {
					other_args.add(args[i]);
				}
			} catch (NumberFormatException except) {
				System.out.println("ERROR: Integer expected instead of "
						+ args[i]);
				return printUsage();
			} catch (ArrayIndexOutOfBoundsException except) {
				System.out.println("ERROR: Required parameter missing from "
						+ args[i - 1]);
				return printUsage();
			}
		}
		// Make sure there are exactly 2 parameters left.
		if (other_args.size() != 2) {
			System.out.println("ERROR: Wrong number of parameters: "
					+ other_args.size() + " instead of 2.");
			return printUsage();
		}
		// 设置输出结果按照什么格式保存，以便后续使用。
		conf.setOutputFormat(MapFileOutputFormat.class);
		// 输入文件的HDFS路径
		FileInputFormat.setInputPaths(conf, other_args.get(0));
		// 输出结果的HDFS路径
		FileOutputFormat.setOutputPath(conf, new Path(other_args.get(1)));

		JobClient.runJob(conf);
		return 0;
	}

	public static void main(String[] args) throws Exception {
		int res = ToolRunner.run(new Configuration(), new IndexCreator(),
				new String[] { "hdfs://localhost:9000/sina_news_input/",
						"hdfs://localhost:9000/output_news_map/" });
		System.exit(res);
	}

}
