package nodework;

import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;

import lib.ConstantsParser;
import interfaces.Reducer;
import interfaces.Writable;

public class ReduceWork {

	public static void main(String[] args) {
		preReduce(1);
	}

	public static void preReduce(int reducerNumber) {
		ConstantsParser cp = new ConstantsParser();

		String files;
		File folder = new File("src/partition/reducer_" + reducerNumber);
		File[] listOfFiles = folder.listFiles();

		if (listOfFiles == null) {
			System.out.println("NULL");
		}

		Class<?> reducerClass = cp.getReducerClass();
		try {
			Reducer<Writable<?>, Writable<?>, Writable<?>, Writable<?>> reducer = (Reducer<Writable<?>, Writable<?>, Writable<?>, Writable<?>>) reducerClass
					.newInstance();
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Context<Writable<?>, Writable<?>> cx = new Context<Writable<?>, Writable<?>>();

		for (int i = 0; i < listOfFiles.length; i++) {

			if (listOfFiles[i].isFile()) {
				files = listOfFiles[i].getName();
				// System.out.println(files);
				String[] keyarray = files.split("_");
				if (keyarray.length >= 2)
					System.out.println(keyarray[1]);
				try {

					FileInputStream fis = new FileInputStream(
							"src/partition/reducer_" + reducerNumber + "/"
									+ files);
					ObjectInputStream in = new ObjectInputStream(fis);
					Object obj;
					System.out.println("Print a ");
					while (true) {
						obj = in.readObject();
						// do stuff with obj
						System.out.println("Print b ");
						System.out.println(" VALUE = " + obj);
						System.out.println("Print c ");
					}
				} catch (EOFException e) {
					System.out.println(" DONE WITH FILEZZZ");
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (ClassNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
}
