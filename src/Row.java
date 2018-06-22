import java.math.BigDecimal;

public class Row{
	String province;
	String provinceCodes;
	char gender;
	int genderInt;
	String ageGroup;
	int ageGroupInt;
	BigDecimal total;
	BigDecimal weight;

	Row(String province, String provinceCodes, char gender, int genderInt, String ageGroup, int ageGroupInt, String total){
		this.province = province;
		this.provinceCodes = provinceCodes;
		this.gender = gender;
		this.genderInt = genderInt;
		this.ageGroup = ageGroup;
		this.ageGroupInt = ageGroupInt;
		this.total = new BigDecimal(total);
	}
}
