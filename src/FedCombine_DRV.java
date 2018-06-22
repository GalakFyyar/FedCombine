import com.sun.istack.internal.NotNull;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.UIManager;
import java.io.File;
import java.io.FileNotFoundException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Scanner;

public class FedCombine_DRV{
	private static JFrame frame = new JFrame();
	private static String[] positions;//Prov, Gen, Age

	private static final int PROVINCE_GROUPS = 10;
	private static final int GENDER_GROUPS = 2;
	private static final int AGE_GROUPS = 6;

	private static final int PEI_INDEX = 84;
	private static final int NS_INDEX = 60;

	private static final int WEIGHT_PRECISION = 18;

	public static void main(String[] args){
		try{
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		}catch(Exception ignored){}
		
		String frequenciesFile;
		if(args.length >= 1){
			frequenciesFile = args[0];
		}else{
			JOptionPane.showMessageDialog(frame, "File Argument Not Provided", "Error", JOptionPane.ERROR_MESSAGE);
			frame.dispose();
			return;
		}
		
		int[] frequencies = getFrequencies(frequenciesFile);
		if(frequencies == null){
			JOptionPane.showMessageDialog(frame, "Could Not Find File", "Error", JOptionPane.ERROR_MESSAGE);
			frame.dispose();
			return;
		}
		
		ArrayList<Row> turnout = getTurnout();
		
		if(checkPEIEmpty(frequencies)){
			mergeParallel(turnout, PEI_INDEX, NS_INDEX, GENDER_GROUPS * AGE_GROUPS);
		}

		BigDecimal sum = getSumOfTotals(turnout);
		combine(turnout, frequencies);
		BigDecimal sumAfter = getSumOfTotals(turnout);
		
		if(!sum.equals(sumAfter)){
			JOptionPane.showMessageDialog(frame, "Sums don't match", "Error", JOptionPane.ERROR_MESSAGE);
			frame.dispose();
			return;
		}
		
		for(Row r : turnout){
			r.weight = new BigDecimal(r.total.toString()).divide(sum, WEIGHT_PRECISION, RoundingMode.HALF_EVEN);
			//System.out.println(r.province + "\t" + r.gender + "\t" + r.weight);
			System.out.println(r.weight);
		}
		
		Writer.write(frequenciesFile, turnout, positions);
	}

	private static boolean checkPEIEmpty(int[] freqs){
		int peiZero = getZeros(freqs, PEI_INDEX) + getZeros(freqs, PEI_INDEX + AGE_GROUPS);
		return peiZero == AGE_GROUPS * 2;
	}

	private static BigDecimal getSumOfTotals(ArrayList<Row> turnout){
		BigDecimal sum = new BigDecimal(0);
		for(Row r: turnout){
			sum = sum.add(r.total);
		}
		return sum;
	}

	private static int[] getFrequencies(@NotNull String filename){
		Scanner sc;
		try{
			sc = new Scanner(new File(filename));
		}catch(FileNotFoundException e){
			return null;
		}

		int[] frequencies = new int[120];
		positions = sc.nextLine().split(",");   //first line in this file specifies the positions to be used in the TXT file
		while(sc.hasNextLine()){
			String[] fields = sc.nextLine().split(",");

			//if other gender
			if(fields[1].charAt(0) == '3')
				continue;

			int index = 0;

			//Province, if BC, stays index stays zero
			switch(fields[0].charAt(0)){
				case 'V':		//Territories
				case 'Y':
					index = 12;
					break;

				case 'R':		//Manitoba
					index = 24;			//12*2
					break;

				case 'E':		//New Brunswick
					index = 36;			//12*3
					break;

				case 'A':		//Newfoundland
					index = 48;			//12*4
					break;

				case 'B':		//Nova Scotia
					index = 60;			//12*5
					break;

				case 'K':		//Ontario
				case 'L':
				case 'M':
				case 'N':
				case 'P':
					index = 72;			//12*6
					break;

				case 'C':		//PEI
					index = 84;			//12*7
					break;

				case 'G':		//Quebec
				case 'H':
				case 'J':
					index = 96;			//12*8
					break;

				case 'S':		//Saskatchewan
					index = 108;		//12*9
					break;
			}

			//if male
			if(fields[1].charAt(0) == '1')
				index += 6;

			index += fields[2].charAt(0) - 49;		//pro shortcut for grabbing the first digit of the age and then mapping 1 to 6 ascii to 0 to 5 int

			frequencies[index]++;		//update frequency
		}
		
		sc.close();
		return frequencies;
	}

	@SuppressWarnings("SpellCheckingInspection")
	private static ArrayList<Row> getTurnout(){
		//Province, Sex, Age, Total
		ArrayList<Row> turnout = new ArrayList<>();
		
		turnout.add(new Row("ABNTNU", "TX", 'F', 2, "18 - 24", 1, "113681.04837180"));
		turnout.add(new Row("ABNTNU", "TX", 'F', 2, "25 - 34", 2, "184224.79350700"));
		turnout.add(new Row("ABNTNU", "TX", 'F', 2, "35 - 44", 3, "178415.40358600"));
		turnout.add(new Row("ABNTNU", "TX", 'F', 2, "45 - 54", 4, "184103.71402200"));
		turnout.add(new Row("ABNTNU", "TX", 'F', 2, "55 - 64", 5, "172519.94258170"));
		turnout.add(new Row("ABNTNU", "TX", 'F', 2, "65+    ", 6, "180115.91248677"));
		turnout.add(new Row("ABNTNU", "TX", 'M', 1, "18 - 24", 1, "107647.04034960"));
		turnout.add(new Row("ABNTNU", "TX", 'M', 1, "25 - 34", 2, "176752.88880800"));
		turnout.add(new Row("ABNTNU", "TX", 'M', 1, "35 - 44", 3, "163113.64474000"));
		turnout.add(new Row("ABNTNU", "TX", 'M', 1, "45 - 54", 4, "172049.22168600"));
		turnout.add(new Row("ABNTNU", "TX", 'M', 1, "55 - 64", 5, "172803.61703180"));
		turnout.add(new Row("ABNTNU", "TX", 'M', 1, "65+    ", 6, "162900.77291029"));
		turnout.add(new Row("BCYT",   "VY", 'F', 2, "18 - 24", 1, "119855.09184090"));
		turnout.add(new Row("BCYT",   "VY", 'F', 2, "25 - 34", 2, "173692.40682600"));
		turnout.add(new Row("BCYT",   "VY", 'F', 2, "35 - 44", 3, "180474.00525100"));
		turnout.add(new Row("BCYT",   "VY", 'F', 2, "45 - 54", 4, "223089.61426200"));
		turnout.add(new Row("BCYT",   "VY", 'F', 2, "55 - 64", 5, "248739.07204800"));
		turnout.add(new Row("BCYT",   "VY", 'F', 2, "65+    ", 6, "309960.85742600"));
		turnout.add(new Row("BCYT",   "VY", 'M', 1, "18 - 24", 1, "115256.88533030"));
		turnout.add(new Row("BCYT",   "VY", 'M', 1, "25 - 34", 2, "154924.34630800"));
		turnout.add(new Row("BCYT",   "VY", 'M', 1, "35 - 44", 3, "161671.10902800"));
		turnout.add(new Row("BCYT",   "VY", 'M', 1, "45 - 54", 4, "200258.93716100"));
		turnout.add(new Row("BCYT",   "VY", 'M', 1, "55 - 64", 5, "220246.54655200"));
		turnout.add(new Row("BCYT",   "VY", 'M', 1, "65+    ", 6, "286533.12816700"));
		turnout.add(new Row("MB",      "R", 'F', 2, "18 - 24", 1, "33429.85437000"));
		turnout.add(new Row("MB",      "R", 'F', 2, "25 - 34", 2, "47572.03211000"));
		turnout.add(new Row("MB",      "R", 'F', 2, "35 - 44", 3, "46347.96392000"));
		turnout.add(new Row("MB",      "R", 'F', 2, "45 - 54", 4, "54569.66485000"));
		turnout.add(new Row("MB",      "R", 'F', 2, "55 - 64", 5, "58581.42758000"));
		turnout.add(new Row("MB",      "R", 'F', 2, "65+    ", 6, "78487.67457000"));
		turnout.add(new Row("MB",      "R", 'M', 1, "18 - 24", 1, "31035.76098000"));
		turnout.add(new Row("MB",      "R", 'M', 1, "25 - 34", 2, "40675.22436000"));
		turnout.add(new Row("MB",      "R", 'M', 1, "35 - 44", 3, "43620.90109000"));
		turnout.add(new Row("MB",      "R", 'M', 1, "45 - 54", 4, "49735.97192000"));
		turnout.add(new Row("MB",      "R", 'M', 1, "55 - 64", 5, "55244.00327000"));
		turnout.add(new Row("MB",      "R", 'M', 1, "65+    ", 6, "63939.52098000"));
		turnout.add(new Row("NB",      "E", 'F', 2, "18 - 24", 1, "19600.47825000"));
		turnout.add(new Row("NB",      "E", 'F', 2, "25 - 34", 2, "30138.51387000"));
		turnout.add(new Row("NB",      "E", 'F', 2, "35 - 44", 3, "33688.44638000"));
		turnout.add(new Row("NB",      "E", 'F', 2, "45 - 54", 4, "44392.87942000"));
		turnout.add(new Row("NB",      "E", 'F', 2, "55 - 64", 5, "50086.20425000"));
		turnout.add(new Row("NB",      "E", 'F', 2, "65+    ", 6, "59644.08819000"));
		turnout.add(new Row("NB",      "E", 'M', 1, "18 - 24", 1, "17612.30279000"));
		turnout.add(new Row("NB",      "E", 'M', 1, "25 - 34", 2, "24459.69684000"));
		turnout.add(new Row("NB",      "E", 'M', 1, "35 - 44", 3, "29624.23357000"));
		turnout.add(new Row("NB",      "E", 'M', 1, "45 - 54", 4, "40121.80455000"));
		turnout.add(new Row("NB",      "E", 'M', 1, "55 - 64", 5, "43207.80385000"));
		turnout.add(new Row("NB",      "E", 'M', 1, "65+    ", 6, "51882.54803000"));
		turnout.add(new Row("NL",      "A", 'F', 2, "18 - 24", 1, "9135.43994400"));
		turnout.add(new Row("NL",      "A", 'F', 2, "25 - 34", 2, "16273.80642000"));
		turnout.add(new Row("NL",      "A", 'F', 2, "35 - 44", 3, "18847.57922000"));
		turnout.add(new Row("NL",      "A", 'F', 2, "45 - 54", 4, "28047.96597000"));
		turnout.add(new Row("NL",      "A", 'F', 2, "55 - 64", 5, "30635.99632000"));
		turnout.add(new Row("NL",      "A", 'F', 2, "65+    ", 6, "33924.98284000"));
		turnout.add(new Row("NL",      "A", 'M', 1, "18 - 24", 1, "9505.18955900"));
		turnout.add(new Row("NL",      "A", 'M', 1, "25 - 34", 2, "12456.97837000"));
		turnout.add(new Row("NL",      "A", 'M', 1, "35 - 44", 3, "15431.24782000"));
		turnout.add(new Row("NL",      "A", 'M', 1, "45 - 54", 4, "23925.63016000"));
		turnout.add(new Row("NL",      "A", 'M', 1, "55 - 64", 5, "27378.51498000"));
		turnout.add(new Row("NL",      "A", 'M', 1, "65+    ", 6, "31825.66840000"));
		turnout.add(new Row("NS",      "B", 'F', 2, "18 - 24", 1, "23943.89204000"));
		turnout.add(new Row("NS",      "B", 'F', 2, "25 - 34", 2, "34893.24517000"));
		turnout.add(new Row("NS",      "B", 'F', 2, "35 - 44", 3, "39463.51200000"));
		turnout.add(new Row("NS",      "B", 'F', 2, "45 - 54", 4, "50681.35287000"));
		turnout.add(new Row("NS",      "B", 'F', 2, "55 - 64", 5, "60266.73962000"));
		turnout.add(new Row("NS",      "B", 'F', 2, "65+    ", 6, "72406.10242000"));
		turnout.add(new Row("NS",      "B", 'M', 1, "18 - 24", 1, "21596.24974000"));
		turnout.add(new Row("NS",      "B", 'M', 1, "25 - 34", 2, "28417.49941000"));
		turnout.add(new Row("NS",      "B", 'M', 1, "35 - 44", 3, "32106.14825000"));
		turnout.add(new Row("NS",      "B", 'M', 1, "45 - 54", 4, "45728.38562000"));
		turnout.add(new Row("NS",      "B", 'M', 1, "55 - 64", 5, "53048.24306000"));
		turnout.add(new Row("NS",      "B", 'M', 1, "65+    ", 6, "63517.62980000"));
		turnout.add(new Row("ON",  "KLMNP", 'F', 2, "18 - 24", 1, "338550.67950000"));
		turnout.add(new Row("ON",  "KLMNP", 'F', 2, "25 - 34", 2, "484176.18210000"));
		turnout.add(new Row("ON",  "KLMNP", 'F', 2, "35 - 44", 3, "513578.86260000"));
		turnout.add(new Row("ON",  "KLMNP", 'F', 2, "45 - 54", 4, "643706.70320000"));
		turnout.add(new Row("ON",  "KLMNP", 'F', 2, "55 - 64", 5, "651183.82070000"));
		turnout.add(new Row("ON",  "KLMNP", 'F', 2, "65+    ", 6, "805878.30930000"));
		turnout.add(new Row("ON",  "KLMNP", 'M', 1, "18 - 24", 1, "322363.11690000"));
		turnout.add(new Row("ON",  "KLMNP", 'M', 1, "25 - 34", 2, "433896.80330000"));
		turnout.add(new Row("ON",  "KLMNP", 'M', 1, "35 - 44", 3, "452887.08680000"));
		turnout.add(new Row("ON",  "KLMNP", 'M', 1, "45 - 54", 4, "597140.11390000"));
		turnout.add(new Row("ON",  "KLMNP", 'M', 1, "55 - 64", 5, "602686.89270000"));
		turnout.add(new Row("ON",  "KLMNP", 'M', 1, "65+    ", 6, "726329.42910000"));
		turnout.add(new Row("PE",      "C", 'F', 2, "18 - 24", 1, "3834.97687600"));
		turnout.add(new Row("PE",      "C", 'F', 2, "25 - 34", 2, "6330.24540000"));
		turnout.add(new Row("PE",      "C", 'F', 2, "35 - 44", 3, "5699.57842500"));
		turnout.add(new Row("PE",      "C", 'F', 2, "45 - 54", 4, "7402.23047400"));
		turnout.add(new Row("PE",      "C", 'F', 2, "55 - 64", 5, "10161.58809000"));
		turnout.add(new Row("PE",      "C", 'F', 2, "65+    ", 6, "13962.36304100"));
		turnout.add(new Row("PE",      "C", 'M', 1, "18 - 24", 1, "2589.03133300"));
		turnout.add(new Row("PE",      "C", 'M', 1, "25 - 34", 2, "4249.59412900"));
		turnout.add(new Row("PE",      "C", 'M', 1, "35 - 44", 3, "5796.12036000"));
		turnout.add(new Row("PE",      "C", 'M', 1, "45 - 54", 4, "8228.30989700"));
		turnout.add(new Row("PE",      "C", 'M', 1, "55 - 64", 5, "7782.50733300"));
		turnout.add(new Row("PE",      "C", 'M', 1, "65+    ", 6, "11831.45464400"));
		turnout.add(new Row("QC",    "GHJ", 'F', 2, "18 - 24", 1, "187943.56330000"));
		turnout.add(new Row("QC",    "GHJ", 'F', 2, "25 - 34", 2, "295898.47130000"));
		turnout.add(new Row("QC",    "GHJ", 'F', 2, "35 - 44", 3, "336589.39600000"));
		turnout.add(new Row("QC",    "GHJ", 'F', 2, "45 - 54", 4, "399264.38910000"));
		turnout.add(new Row("QC",    "GHJ", 'F', 2, "55 - 64", 5, "463960.75320000"));
		turnout.add(new Row("QC",    "GHJ", 'F', 2, "65+    ", 6, "571835.36410000"));
		turnout.add(new Row("QC",    "GHJ", 'M', 1, "18 - 24", 1, "168865.22560000"));
		turnout.add(new Row("QC",    "GHJ", 'M', 1, "25 - 34", 2, "266461.47840000"));
		turnout.add(new Row("QC",    "GHJ", 'M', 1, "35 - 44", 3, "310797.87760000"));
		turnout.add(new Row("QC",    "GHJ", 'M', 1, "45 - 54", 4, "368081.65820000"));
		turnout.add(new Row("QC",    "GHJ", 'M', 1, "55 - 64", 5, "431970.45760000"));
		turnout.add(new Row("QC",    "GHJ", 'M', 1, "65+    ", 6, "502089.36580000"));
		turnout.add(new Row("SK",      "S", 'F', 2, "18 - 24", 1, "30063.26640000"));
		turnout.add(new Row("SK",      "S", 'F', 2, "25 - 34", 2, "49661.78057000"));
		turnout.add(new Row("SK",      "S", 'F', 2, "35 - 44", 3, "42220.73172000"));
		turnout.add(new Row("SK",      "S", 'F', 2, "45 - 54", 4, "49114.89539000"));
		turnout.add(new Row("SK",      "S", 'F', 2, "55 - 64", 5, "53125.84979000"));
		turnout.add(new Row("SK",      "S", 'F', 2, "65+    ", 6, "65728.40574000"));
		turnout.add(new Row("SK",      "S", 'M', 1, "18 - 24", 1, "27461.42649000"));
		turnout.add(new Row("SK",      "S", 'M', 1, "25 - 34", 2, "46648.71610000"));
		turnout.add(new Row("SK",      "S", 'M', 1, "35 - 44", 3, "38227.63026000"));
		turnout.add(new Row("SK",      "S", 'M', 1, "45 - 54", 4, "44450.09161000"));
		turnout.add(new Row("SK",      "S", 'M', 1, "55 - 64", 5, "52377.26544000"));
		turnout.add(new Row("SK",      "S", 'M', 1, "65+    ", 6, "54711.94049000"));
		
		return turnout;
	}

	private static void combine(ArrayList<Row> turnout, int[] freqs){
		int length = PROVINCE_GROUPS * GENDER_GROUPS * AGE_GROUPS;
		int turnoutIndex = 0;
		for(int i = 0; i < length; i += GENDER_GROUPS * AGE_GROUPS){
			//System.out.println(turnout.get(turnoutIndex).province);
			//System.out.println("i:   " + i);
			//System.out.println("t:   " + turnoutIndex);
			
			int numZerosF = getZeros(freqs, i);						//females
			//System.out.println("nzf: " + numZerosF);

			int numZerosM = getZeros(freqs, i + AGE_GROUPS);		//males
			//System.out.println("nzm: " + numZerosM);
			
			if(numZerosF == AGE_GROUPS && numZerosM == AGE_GROUPS){			//entire province missing
				//System.out.println("whole province\n");
				if(i != PEI_INDEX){
					JOptionPane.showMessageDialog(frame, "A province other than PEI is missing\nThis should never happen", "Error", JOptionPane.ERROR_MESSAGE);
					frame.dispose();
					return;
				}
				continue;
			}
			
			boolean entireGenderMissing = false;
			
			if(numZerosF == AGE_GROUPS){										//femmes missing, merge with men
				//System.out.println("no f");
				mergeParallel(turnout, turnoutIndex, turnoutIndex + AGE_GROUPS, AGE_GROUPS);
				
				if(numZerosM > 0){												//at least one male
					//System.out.println("at least one man");
					merge(turnout, turnoutIndex, freqs, i + AGE_GROUPS);
				}
				
				entireGenderMissing = true;
			}else if(numZerosM == AGE_GROUPS){									//dudes missing, merge with gals
				//System.out.println("no m");
				mergeParallel(turnout, turnoutIndex + AGE_GROUPS, turnoutIndex, AGE_GROUPS);
				
				if(numZerosF > 0){												//at least one femme
					//System.out.println("at least one woman");
					merge(turnout, turnoutIndex, freqs, i);
				}
				
				entireGenderMissing = true;
			}
			
			if(!entireGenderMissing){
				if(numZerosF > 0){												//at least one femme
					//System.out.println("at least one woman");
					merge(turnout, turnoutIndex, freqs, i);
				}
				
				if(numZerosM > 0){												//at least one male
					//System.out.println("at least one man");
					merge(turnout, turnoutIndex + AGE_GROUPS - numZerosF, freqs, i + AGE_GROUPS);
				}
			}
			
			//System.out.println();
			turnoutIndex += AGE_GROUPS * GENDER_GROUPS - numZerosF - numZerosM;
		}
	}

	private static int getZeros(int[] freqs, int start){
		int numZeros = 0;
		for(int i = start; i < start + AGE_GROUPS; i++){
			if(freqs[i] == 0)
				numZeros++;
		}
		return numZeros;
	}

	private static void mergeParallel(ArrayList<Row> turnout, int sourceIndex, int destinationIndex, int length){
		assert destinationIndex >= sourceIndex + length || destinationIndex + length <= sourceIndex;				//no overlap

		for(int i = 0; i < length; i++){
			Row r = turnout.get(destinationIndex + i);
			r.total = r.total.add(turnout.get(sourceIndex + i).total);
		}

		for(int i = 0; i < length; i++){
			turnout.remove(sourceIndex);
		}
	}

	//If at least one group present
	private static void merge(ArrayList<Row> turnout, int turnoutIndex, int[] freqs, int freqIndex){
		//merge from bottom, until a non-zero hit
		//this guarantees there is a non-zero at the bottom
		//then merge from the top assuming a non-zero is at the end

		int i;
		for(i = AGE_GROUPS - 1; i > 0; i--){
			if(freqs[freqIndex + i] == 0){
				Row r = turnout.get(turnoutIndex + i - 1);
				r.total = r.total.add(turnout.get(turnoutIndex + i).total);
			}else
				break;
		}

		for(int j = 0; j < AGE_GROUPS; j++){
			if(j == i)
				break;
			if(freqs[freqIndex + j] == 0){
				Row r = turnout.get(turnoutIndex + j + 1);
				r.total = r.total.add(turnout.get(turnoutIndex + j).total);
			}
		}

		for(int k = AGE_GROUPS - 1; k >= 0; k--){
			if(freqs[freqIndex + k] == 0)
				turnout.remove(turnoutIndex + k);
		}
	}

	@SuppressWarnings("unused")
	private static void printTurnout(ArrayList<Row> turnout){
		for(Row r : turnout){
			System.out.println(r.province + " " + r.gender + " " + r.total);
		}
	}
}
