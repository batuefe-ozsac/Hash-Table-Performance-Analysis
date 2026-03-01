package assignment;

public class Article {
	String Id;
	String author;
	String date;
	String category;
	String section;
	String url;
	String headline;
	String description;
	String keywords;
	String secondHeadline;
	String text;
	
	public Article(String Id, String author, String date, String category, String section, String url, String headline,
			String description, String keywords, String secondHeadline, String text) {
		super();
		this.Id = Id;
		this.author = author;
		this.date = date;
		this.category = category;
		this.section = section;
		this.url = url;
		this.headline = headline;
		this.description = description;
		this.keywords = keywords;
		this.secondHeadline = secondHeadline;
		this.text = text;
	}

	public String getId() {
		return Id;
	}

	public String getHeadline() {
		return headline;
	}

	public String getText() {
		return text;
	}

	public void setId(String id) {
		Id = id;
	}

	public void setHeadline(String headline) {
		this.headline = headline;
	}

	public void setText(String text) {
		this.text = text;
	}

	@Override
	public String toString() {
		return "<------------------------------------->\n" +
	           "ID: " + Id + "\n" +
			   "Author: " + author + "\n" +
	           "HeadLine: " + headline + " || Date: " + date + "\n" +
			   "Category: " + category + " || Section: " + section + "\n" +
	           "URL: " + url + "\n" +
			   "<------------------------------------->\n" +
	           "Text of Content: " + text + "\n";
	}
}
