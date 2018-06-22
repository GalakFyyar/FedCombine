import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;

public class Writer{
	private static final String FILENAME = "Table805.txt";
	
	public static void write(String fileName, ArrayList<Row> turnout, String[] positions){
		PrintWriter pw;
		try{
			String folder = new File(fileName).getParentFile().toString();
			pw = new PrintWriter(folder + "\\" + FILENAME);
		}catch(FileNotFoundException ignored){
			return;
		}
		
		for(Row r : turnout){
			String province = positions[0];
			String gender = positions[1];
			String ageGroup = positions[2];
			
			switch(r.provinceCodes.length()){
				case 1:
					pw.format("R %s %s %s; (%s'%c')(%s-%d)(%s-%d); v%s\n", r.province, r.gender, r.ageGroup, province, r.provinceCodes.charAt(0), gender, r.genderInt, ageGroup, r.ageGroupInt, r.weight);
					break;
				case 2:
					pw.format("R %s %s %s; (%s'%c' OR %s'%c')(%s-%d)(%s-%d); v%s\n", r.province, r.gender, r.ageGroup, province, r.provinceCodes.charAt(0), province, r.provinceCodes.charAt(1), gender, r.genderInt, ageGroup, r.ageGroupInt, r.weight);
					break;
				case 3:
					pw.format("R %s %s %s; (%s'%c' OR %s'%c' OR %s'%c')(%s-%d)(%s-%d); v%s\n", r.province, r.gender, r.ageGroup, province, r.provinceCodes.charAt(0), province, r.provinceCodes.charAt(1), province, r.provinceCodes.charAt(2), gender, r.genderInt, ageGroup, r.ageGroupInt, r.weight);
					break;
				case 5:
					pw.format("R %s %s %s; (%s'%c' OR %s'%c' OR %s'%c' OR %s'%c' OR %s'%c')(%s-%d)(%s-%d); v%s\n", r.province, r.gender, r.ageGroup, province, r.provinceCodes.charAt(0), province, r.provinceCodes.charAt(1), province, r.provinceCodes.charAt(2), province, r.provinceCodes.charAt(3), province, r.provinceCodes.charAt(4), gender, r.genderInt, ageGroup, r.ageGroupInt, r.weight);
					break;
				default:
					return;
			}
		}
		
		pw.close();
	}
}
