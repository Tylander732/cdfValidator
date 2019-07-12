import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.io.File;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.Scanner;
import java.util.regex.Pattern;
import java.util.stream.Stream;
import java.util.Vector;

public class cdfValidator
{
	public Vector<String> readFilesForFolder(final File folder, String record, int element, String delimeter){
		BufferedReader reader = null;
		Vector<String> lookupValuesList = new Vector<>();

		for(final File fileEntry : folder.listFiles()){
			if(fileEntry.isDirectory()){
				readFilesForFolder(fileEntry, record, element, delimeter);
			} else {
				System.out.println(fileEntry.getName());
				try {
					reader = new BufferedReader(new FileReader(fileEntry));
					String text = null;
					while((text = reader.readLine()) != null) {
						if (text.substring(0,record.length()).equals(record)){
							System.out.println("Found record");

							String[] currentStringElements = text.split(Pattern.quote(delimeter));
							String lookupValue = currentStringElements[element];

							if(!lookupValuesList.contains(lookupValue)) {
								lookupValuesList.add(lookupValue);
							}
						}
					}
				} catch	(FileNotFoundException e) {
					e.printStackTrace();
				} catch (IOException e){
					e.printStackTrace();
				} finally {
					try {
						if(reader != null) {
							reader.close();
						}
					} catch (IOException e) {
					}
				}
			}
		}
		return lookupValuesList;
	}

	public Vector<String> matchLookupValuesToCdf(Vector<String> lookupKeys, final File cdf){
		Vector<String> valuesNotFoundInCdf = new Vector<>();
		Vector<String> cdfKeyList = new Vector<>();
		BufferedReader reader = null;

		try
		{
			reader = new BufferedReader(new FileReader(cdf));

			String cdfLine  = null;
			String[] cdfKey = null;

			//build list of all keys found in current CDF
			while ((cdfLine = reader.readLine()) != null)
			{
				cdfKey = cdfLine.split(",");
				if (!cdfKeyList.contains(cdfKey[0]))
				{
					cdfKeyList.add(cdfKey[0]);
				}
			}
		} catch	(FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e){
			e.printStackTrace();
		} finally {
			try {
				if(reader != null) {
					reader.close();
				}
			} catch (IOException e) {
			}
		}

		Iterator<String> iterateValue = lookupKeys.iterator();
		while (iterateValue.hasNext()){
			String currentIteratorValue = iterateValue.next();
			if(!cdfKeyList.contains(currentIteratorValue)){
				System.out.println("key not found"+currentIteratorValue);
			}
		}

		return valuesNotFoundInCdf;
}

	public static void main(String[] args)
	{
		Scanner scanner = new Scanner(System.in);
		String userFileDirectory;
		String userRecord;
		String userElement;
		String userDelimeter;

		//gather user inputs for expin field to use
		System.out.println("List directory of files you want to check");
		userFileDirectory = scanner.nextLine();
		System.out.println("Record you want to find");
		userRecord = scanner.nextLine();
		System.out.println("Which element of the record do you want?");
		userElement = scanner.nextLine();
		int userElementInt = Integer.parseInt(userElement);
		System.out.println("What is your delimeter?");
		userDelimeter = scanner.nextLine();

		File fileDirectory = new File(userFileDirectory);
		cdfValidator validator = new cdfValidator();

		System.out.println("What CDF do you want to check?");

		File userCdf = new File(scanner.nextLine());

		Vector<String> lookupValues = validator.readFilesForFolder(fileDirectory, userRecord, userElementInt, userDelimeter);
		validator.matchLookupValuesToCdf(lookupValues, userCdf);

	}


}
