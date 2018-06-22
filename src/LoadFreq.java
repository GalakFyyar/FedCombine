import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.UIManager;
import java.io.File;
import java.util.Scanner;

public class LoadFreq{
	public static int[] getFreqs(){
		Scanner sc = null;
		try{
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			sc = new Scanner(new File("freq.csv"));
		}catch(Exception ignored){
			JOptionPane.showMessageDialog(new JFrame(), "File Not There", "Error", JOptionPane.ERROR_MESSAGE);
			return null;
		}

		sc.nextLine();//skip header
		int[] freqs = new int[120];
		for(int i = 0; i < 120; i++){
			freqs[i] = Integer.parseInt(sc.nextLine().split(",")[3]);
		}

		return freqs;
	}
}
