package assignment;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.File;
import java.util.Scanner;
import java.io.IOException;

public class Google2M {
	HashTable<String, Article> articleMap; // K: ID, V: Article
	HashTable<String, HashTable<String, Integer>> indexMap; // K: Words, V: [K: ID, V: Count]
	HashTable<String, String> stopwords;

	private static final String DELIMITERS = "[-+=" +

		        " " +        //space

		        "\r\n " +    //carriage return line fit

				"1234567890" + //numbers

				"’'\"" +       // apostrophe

				"(){}<>\\[\\]" + // brackets

				":" +        // colon

				"," +        // comma

				"‒–—―" +     // dashes

				"…" +        // ellipsis

				"!" +        // exclamation mark

				"." +        // full stop/period

				"«»" +       // guillemets

				"-‐" +       // hyphen

				"?" +        // question mark

				"‘’“”" +     // quotation marks

				";" +        // semicolon

				"/" +        // slash/stroke

				"⁄" +        // solidus

				"␠" +        // space?   

				"·" +        // interpunct

				"&" +        // ampersand

				"@" +        // at sign

				"*" +        // asterisk

				"\\" +       // backslash

				"•" +        // bullet

				"^" +        // caret

				"¤¢$€£¥₩₪" + // currency

				"†‡" +       // dagger

				"°" +        // degree

				"¡" +        // inverted exclamation point

				"¿" +        // inverted question mark

				"¬" +        // negation

				"#" +        // number sign (hashtag)

				"№" +        // numero sign ()

				"%‰‱" +      // percent and related signs

				"¶" +        // pilcrow

				"′" +        // prime

				"§" +        // section sign

				"~" +        // tilde/swung dash

				"¨" +        // umlaut/diaeresis

				"_" +        // underscore/understrike

				"|¦" +       // vertical/pipe/broken bar

				"⁂" +        // asterism

				"☞" +        // index/fist

				"∴" +        // therefore sign

				"‽" +        // interrobang

				"※" +          // reference mark

		        "]";
	
	private static final String regex = ",(?=([^\"]*\"[^\"]*\")*[^\"]*$)";
	
	private boolean currentSSF; 
    private boolean currentDH;
    private double currentLF;
	
	private long elapsedTime = 0;
	
	private class ScoreResults {
		String Id;
		int scores;
		public ScoreResults(String Id, int scores) {
			super();
			this.Id = Id;
			this.scores = scores;
		}		
	}
	
	public Google2M(boolean isSSF, boolean isDH, double loadFactor) {
		currentSSF = isSSF;
		currentDH = isDH;
		currentLF = loadFactor;
		
		articleMap = new HashTable<>(60000, loadFactor, isSSF, isDH);
		indexMap = new HashTable<>(10000, loadFactor, isSSF, isDH);
		stopwords = new HashTable<>(300, loadFactor, isSSF, isDH);
	}
	
	public static void main(String[] args) {
		Scanner sc = new Scanner(System.in);
		
		System.out.println("--------------------------------------");
		System.out.println("         WELCOME TO GOOGLE 2M !       ");
		System.out.println("--------------------------------------");
		
		System.out.println("Firstly, Choose your Hash Function:");
		System.out.println("A - Simple Summation Function (SSF)");
		System.out.println("B - Polynomial Accumulation Function (PAF)");
		String hash = sc.nextLine().toUpperCase();
		boolean isSSF = true;
		
		if(hash.equals("B")) isSSF = false;
		
		System.out.println("\nChoose your Collision Handling:");
		System.out.println("A - Linear Probing");
		System.out.println("B - Double Hashing");
		String collis = sc.nextLine().toUpperCase();
		boolean isDH = true;
		
		if(collis.equals("A")) isDH = false;
		
		System.out.println("\nChoose your Load Factor");
		System.out.println("A - 0.5");
		System.out.println("B - 0.8");
		String loadF = sc.nextLine();
		double loadFactor = 0.5;
		
		if(loadF.equals("B")) loadFactor = 0.8;
		else loadFactor = 0.5;
		
		Google2M g = new Google2M(isSSF, isDH, loadFactor);
		
		g.readStopwords("stop_words_en.txt");
		g.readArticles("CNN_Articels.csv");
		
		System.out.println("Loading process is completed.");
		
		boolean loop = true;
		
		while(loop) {
			System.out.println("\n--------------------------------------");
			System.out.println("             GOOGLE 2M                ");
			System.out.println("--------------------------------------");
			System.out.println("A - Search Article by ID");
			System.out.println("B - Search Article by Text");
			System.out.println("C - Run Search Test");
			System.out.println("D - Exit the sytem");
			System.out.print("Enter your input: ");
			
			String input = sc.nextLine().toUpperCase();
			
			switch(input) {
			case "A":
				System.out.print("Enter Article ID: ");
				String id = sc.nextLine();
				g.searchId(id);
				break;
			case "B":
				System.out.println("Enter Search Query: ");
				String query = sc.nextLine();
				g.searchText(query);
				break;
			case "C":
				g.readSearchwords("search.txt");
				break;
			case "D":
				System.out.println("Exiting the system..");
				loop = false;
				break;
			default:
				System.out.println("Wrong input ! Try again.");
			}
		}
		
		sc.close();
        
	}
	
	public void searchId(String Id) { // browses the article map with the ID entered by the user and if it finds an article with that ID, it prints the information about the article.
		Article article = articleMap.get(Id);
		
		if(article != null) {
			System.out.println(article.toString());
		} else {
			System.out.println("Article number " + Id + " not found.");
		}
	}
	
	public void searchText(String query) { // Breaks down the text or word entered by the user, navigates through the index map, finds and prints the 5 most relevant articles.
		System.out.println("\nSearched query: " + query);
		
		String[] words = query.split(DELIMITERS);
		
		HashTable<String, Integer> scoreTable = new HashTable<>(100,currentLF,currentSSF,currentDH);
		
		for(int i = 0; i < words.length; i++) {
			String word = words[i].trim().toLowerCase();
			
			if(word.length() < 2) continue;
			if(stopwords.get(word) != null) continue;
			
			HashTable<String, Integer> countTable = indexMap.get(word);
			
			if(countTable != null) {
				for(int j = 0; j < countTable.getCapacity(); j++) {
					String Id = countTable.getKeyAt(j);
					
					if(Id != null) {
						int scores = countTable.get(Id);
						
						Integer current = scoreTable.get(Id);
						if(current == null) current = 0;
						
						scoreTable.add(Id, current + scores);
					}
				}
			}
		}
		
		int resultCount = scoreTable.getSize();
		if(resultCount == 0) return;
		
		ScoreResults[] results = new ScoreResults[resultCount]; 
		
		int counter = 0;
		for(int i = 0; i < scoreTable.getCapacity(); i++) {
			String Id = scoreTable.getKeyAt(i);
			if(Id != null) {
				int scores = scoreTable.get(Id);
				results[counter] = new ScoreResults(Id, scores);
				counter++;
			}
		}
		
		for(int i = 0; i < results.length - 1; i++) { // bubble sort algorithm
			for(int j = 0; j < results.length - i - 1; j++) {
				if(results[j].scores < results[j + 1].scores) {
					ScoreResults temp = results[j];
					results[j] = results[j + 1];
					results[j + 1] = temp;
				}
			}
		}
		
		System.out.println("\n-- TOP 5 MOST RELEVANT RESULTS --");
		
		int max = (results.length < 5) ? results.length : 5;
		if(max == 0) return;
		
		for(int i = 0; i < max; i++) {
			String Id = results[i].Id;
			Article article = articleMap.get(Id);
			
			if(article != null) {
				System.out.println((i+1) + ". Score: " + results[i].scores + " Headline of article: " + article.getHeadline());
			}
		}
		
	}
	
	public void indexProcess(String Id, String text) { // Performs indexing based on the incoming ID and text.
		text = text.toLowerCase();
		
		String[] words = text.split(DELIMITERS);
		
		for(int i = 0; i < words.length; i++) {
			String word = words[i].trim();
			
			if(word.length() < 2) continue; // a, 1, 2 ... words and numbers that lenght below 2
			if(stopwords.get(word) != null) continue;
			
			HashTable<String, Integer> countTable = indexMap.get(word);
			
			if(countTable != null) {
				Integer counter = countTable.get(Id);
				
				if(counter != null) {
					countTable.add(Id, counter + 1);
				} else {
					countTable.add(Id, 1);
				}
			} else {
				countTable = new HashTable<>(15, currentLF,currentSSF,currentDH);
				
				countTable.add(Id, 1);
				
				indexMap.add(word, countTable);
			}
		}
	}
	
	public void readSearchwords(String filepath) {  // Reads 1000 words and gives performance test results.
		System.out.println("\nPerformance monitoring begins... ( " + filepath + " )");
		
		long start = System.nanoTime();
		
		int countF = 0;
		int countT = 0;
		
		try {
			File file = new File(filepath);
			Scanner sc = new Scanner(file);
			
			while(sc.hasNextLine()) {
				String word = sc.nextLine().trim().toLowerCase();
				
				if(word.isEmpty()) continue;
				
				countT++;
				
				if(indexMap.get(word) != null) {
					countF++;
				}
			}
			sc.close();
			
			long end = System.nanoTime();
			long timeT = end - start;
			
	        System.out.println("\n<-- SEARCH PERFORMANCE TEST RESULTS -->");
	        System.out.println("Total Words Searched: " + countT);
	        System.out.println("Total Words Found: " + countF);
	        System.out.println("Total Search Time: " + timeT + " ns");
	        System.out.println("Avg. Search Time: " + (timeT / countT) + " ns");
	        System.out.println("Total collision count: " + indexMap.getCollisinCount());
	        System.out.println("Total Elapsed Time: " + elapsedTime);
		} catch(IOException e) {
			System.out.println(e.getMessage());
		}
	}
	
	public void readStopwords(String filepath) { // Reads the stop words and add to the stop words hash table.
		try {
			File file = new File(filepath);
			Scanner sc = new Scanner(file);
			
			while(sc.hasNextLine()) {
				String line = sc.nextLine().trim().toLowerCase();
				
				if(!line.isEmpty()) {
					stopwords.add(line, "33mersin33");
				}
			}
			
			sc.close();
		} catch(IOException e) {
			System.out.println(e.getMessage());
		}
		
	}
	
	public void readArticles(String file) { // Reads the article file and indexes all the articles.
		String line = "";
		int counter = 0;
		
		long start = System.currentTimeMillis();
		
		try {
			BufferedReader br = new BufferedReader(new FileReader(file));
			
			br.readLine();

			while((line = br.readLine()) != null) {
				String[] data = line.split(regex);
				
				if(data.length < 11) continue;
				
				String Id = quoteErase(data[0]);
                String author = quoteErase(data[1]);
                String date = quoteErase(data[2]);
                String category = quoteErase(data[3]);
                String section = quoteErase(data[4]);
                String url = quoteErase(data[5]);
                String headline = quoteErase(data[6]);
                String description = quoteErase(data[7]);
                String keywords = quoteErase(data[8]);
                String secondHeadline = quoteErase(data[9]);
                String text = quoteErase(data[10]);
                
                Article article = new Article(Id, author, date, category, section, url, headline, description, keywords, secondHeadline, text);
                
                articleMap.add(Id, article);
                
                indexProcess(Id, text);
                
                if(counter % 1000 == 0 && counter != 0) {
                	System.out.println(counter + " articles were processed.");
                }
                counter++;
			}
			br.close();
			
			long end = System.currentTimeMillis();
			long total = end - start;
			
			elapsedTime = total;
			
			System.out.println("Total Elapsed Time: " + total);
			
		} catch(IOException e) {
			System.out.println(e.getMessage());
		}
	}
	
	public String quoteErase(String s) {  // For erasing the quotes.
		if(s != null && s.startsWith("\"") && s.endsWith("\"")) {
			return s.substring(1, s.length() - 1);
		}
		
		return s;
	}
}
