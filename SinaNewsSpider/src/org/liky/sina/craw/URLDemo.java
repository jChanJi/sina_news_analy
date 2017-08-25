package org.liky.sina.craw;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.liky.sina.dao.INewsDAO;
import org.liky.sina.dbc.DataBaseConnection;
import org.liky.sina.factory.DAOFactory;
import org.liky.sina.vo.News;
//爬取新闻标题ｕｒｌ内容放入数据库和hdfs
public class URLDemo {
	private static Configuration conf = new Configuration();

	private static int id = 1;

	private static FileSystem fs;
	private static Path path;

	// private static String savePath = "E:/sina_news_data/";
	private static String savePath = "E:/zhaopin_data/";

	// 等待爬取的url
	private static List<String> allWaitUrl = new ArrayList<>();

	// 已经爬过的url
	private static Set<String> allOverUrl = new HashSet<>();

	// 记录所有url的深度，以便判断
	private static Map<String, Integer> allUrlDepth = new HashMap<>();

	// 允许最大深度
	private static int maxDepth = 5;

	// 声明一个Object对象，用来帮助我们进行线程的等待操作
	private static Object obj = new Object();

	// 记录总线程数
	private static final int MAX_THREAD = 10;

	// 记录空闲的线程数
	private static int count = 0;

	private static INewsDAO dao;

	static {
		dao = DAOFactory.getINewsDAOInstance(new DataBaseConnection());
	}

	public static void main(String[] args) {
		// 确定爬取的网页地址
		String strUrl = "http://news.sina.com.cn/";
		// String strUrl =
		// "http://sou.zhaopin.com/jobs/searchresult.ashx?jl=%E5%8D%97%E4%BA%AC&kw=Java&sm=0&p=1";

		addUrl(strUrl, 0);

		// parseUrl(strUrl, 1);
		// 建立多个线程序
		for (int i = 0; i < MAX_THREAD; i++) {
			new URLDemo().new MyThread().start();
		}
	}

	public static void parseUrl(String strUrl, int depth) {
		// 先判断当前的URL，是否之前已经爬取过了。
		// 判断深度是否符合要求
		if (!(allOverUrl.contains(strUrl) || depth > maxDepth)) {
			System.out.println("当前执行的 " + Thread.currentThread().getName()
					+ " 爬虫线程处理爬取：" + strUrl);
			try {
				// 改为使用JSoup来进行数据的爬取
				Document doc = Jsoup.connect(strUrl).get();
				// 通过这个doc来接收返回的结果
				// 提取有效的title和description
				String title = doc.title();
				Element descE = doc.getElementsByAttributeValue("name",
						"description").first();
				String desc = descE.attr("content");

				// 如果有效，则进行保存
				if (title != null && desc != null && !title.trim().equals("")
						&& !desc.trim().equals("")) {

					// 还需要生成一个id，以便放到数据库中，然后把id也加入到HDFS里，便于后续构建索引。
				System.out.println("1:"+title);
					News news = new News();
					news.setId(id++);
					news.setTitle(title);
					news.setDescription(desc);
					news.setUrl(strUrl);
					
					dao.doCreate(news);

					// 需要向HDFS中保存数据
					path = new Path("hdfs://localhost:9000/sina_news_input/"
							+ System.currentTimeMillis() + ".txt");
					fs = path.getFileSystem(conf);
					FSDataOutputStream os = fs.create(path);
					// 进行内容输出
					os.writeUTF(id + "\r\n" + title + "\r\n" + desc);
					os.close();

					// 解析所有的超连接
					Elements aEs = doc.getElementsByTag("a");

					if (aEs != null && aEs.size() > 0) {
						for (Element aE : aEs) {
							String href = aE.attr("href");
							if ((href.startsWith("http:") || href
									.startsWith("https:"))
									&& href.contains("news.sina.com.cn")) {
								addUrl(href, depth + 1);
							}
						}
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			// 把当前已经爬完的url放入over中
			allOverUrl.add(strUrl);
			System.out.println(strUrl + " 爬取完成，已经爬取的内容量为：" + allOverUrl.size()
					+ " , 剩余要爬取量为：" + allWaitUrl.size());

			// 判断是否集合中还有其他的内容需要进行爬取，如果有，则进行线程的唤醒
			if (allWaitUrl.size() > 0) {
				synchronized (obj) {
					obj.notify();
				}
			} else {
				System.out.println("爬取结束...");
				System.exit(0);
			}

		}

	}

	public static synchronized void addUrl(String href, int depth) {
		// 将这些url地址放入到我们的队列中
		allWaitUrl.add(href);
		// 判断这个url之前是否已经放入过了。
		if (!allUrlDepth.containsKey(href)) {
			allUrlDepth.put(href, depth + 1);
		}
	}

	public static synchronized String getUrl() {
		String nextUrl = allWaitUrl.get(0);
		allWaitUrl.remove(0);
		return nextUrl;
	}

	public class MyThread extends Thread {

		@Override
		public void run() {
			// 有一个死循环，以便让线程能够一直存在
			while (true) {
				// 判断是否有新的连接，如果有，则将连接取得，并进行操作
				if (allWaitUrl.size() > 0) {
					// 还有url可以进行操作
					// 取得这个url来处理
					String url = getUrl();
					// System.out.println("当前线程序 " + this.getName()
					// + " 开始爬取，爬取的页面地址为： " + url);
					parseUrl(url, allUrlDepth.get(url));
				} else {
					System.out.println("当前线程准备就绪，等待连接爬取：" + this.getName());
					count++;
					// 需要建立一个对象，来帮助我们这个线程进入等待状态（wait()）
					synchronized (obj) {
						try {
							obj.wait();
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
					count--;
				}
			}
		}
	}

}
