package processmanagerstuff;
import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;

public class scanner {

	/**
	 * @param args
	 */
	public static void main(String[] args) throws IOException {

		System.out.print("\n Reading Lines \n");

		ArrayList<String> myArr = new ArrayList<String>();

		Scanner sc2 = new Scanner(System.in);

		while (sc2.hasNextLine()) {
			myArr.clear();
			Scanner s2 = new Scanner(sc2.nextLine());
			boolean b;

			// Scan the first word for process name or a different command
			if (s2.hasNext()) {
				String name = s2.next();
				if (name.equals("ps")) {
					if (s2.hasNext()) {
						System.out
								.println("Invalid command: ps does not take any arguments!");
					} else {
						System.out.println("Display all processes");
					}
				} else if (name.equals("quit")) {
					if (s2.hasNext()) {
						System.out
								.println("Invalid commant: quit does not take any arguments!");
					} else {
						System.out.println("Quit prorgram");
						break;
					}
				} else {
					// need to start a new process; parse arguments
					while (b = s2.hasNext()) {
						myArr.add(s2.next());
					}
					System.out
							.println("Started new process with following information: name: "
									+ name + ", arguments: " + myArr.toString());
				}
			}
		}
	}
}
