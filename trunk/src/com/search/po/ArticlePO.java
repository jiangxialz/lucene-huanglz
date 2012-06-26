package com.search.po;

import org.codehaus.jackson.annotate.JsonProperty;

public class ArticlePO 
{
	@JsonProperty("ID")
	private String id;
//	private String ID;
//	public String getID() {
//		return ID;
//	}
//	public void setID(String ID) {
//		this.ID = ID;
//	}
	private String title;
	private String content;
	private String author;
	private String create_time;
	private String intCreateTime;
	private int type;
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	public String getIntCreateTime() {
		return intCreateTime;
	}
	public void setIntCreateTime(String intCreateTime) {
		this.intCreateTime = intCreateTime;
	}
	private String update_time;
	

	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getAuthor() {
		return author;
	}
	public void setAuthor(String author) {
		this.author = author;
	}
	public String getCreate_time() {
		return create_time;
	}
	public void setCreate_time(String create_time) {
		this.create_time = create_time;
	}
	public String getUpdate_time() {
		return update_time;
	}
	public void setUpdate_time(String update_time) {
		this.update_time = update_time;
	}

}
