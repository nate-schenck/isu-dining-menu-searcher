package application;

import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

public class HTMLMenuGrabber {
	
	private String url;
	
	public HTMLMenuGrabber(String url) {
		this.url = url;
	}
	
	public String grab() throws IOException {
		Document doc = Jsoup.connect(url).ignoreContentType(true).get();
		Element body = doc.body();
		return body.text();
	}
	
	public boolean contains(String string) {
		try {
			if (this.grab().contains(string)) {
				return true;
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}

}
