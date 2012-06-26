package self.wonder.search;

import org.codehaus.jackson.annotate.JsonProperty;

public class PostBO {

	private String name; //  
	@JsonProperty("ID")
	private String ID; //  
	private String corpCrawlCode; //  
	private String jobType; //  
	private String area; //   
	private String workYear; //  
	private String diploma; //  
	private String description; //   
	private String status; //  1:正常 2:失效 
	private String lastCrawlDate; //  
//	private java.sql.Timestamp lastCrawlDate; //  
	
	private String intLastCrawlDate;
	
	public String getIntLastCrawlDate() {
		return intLastCrawlDate;
	}
	public void setIntLastCrawlDate(String intLastCrawlDate) {
		this.intLastCrawlDate = intLastCrawlDate;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getID() {
		return ID;
	}
	public void setID(String ID) {
		this.ID = ID;
	}
	public String getCorpCrawlCode() {
		return corpCrawlCode;
	}
	public void setCorpCrawlCode(String corpCrawlCode) {
		this.corpCrawlCode = corpCrawlCode;
	}
	public String getJobType() {
		return jobType;
	}
	public void setJobType(String jobType) {
		this.jobType = jobType;
	}
	public String getArea() {
		return area;
	}
	public void setArea(String area) {
		this.area = area;
	}
	public String getWorkYear() {
		return workYear;
	}
	public void setWorkYear(String workYear) {
		this.workYear = workYear;
	}
	public String getDiploma() {
		return diploma;
	}
	public void setDiploma(String diploma) {
		this.diploma = diploma;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getLastCrawlDate() {
		return lastCrawlDate;
	}
	public void setLastCrawlDate(String lastCrawlDate) {
		this.lastCrawlDate = lastCrawlDate;
	}
}
