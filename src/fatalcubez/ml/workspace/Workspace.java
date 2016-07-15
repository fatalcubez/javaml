package fatalcubez.ml.workspace;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Workspace implements Runnable {

	private WorkspaceFormatter formatter;
	private Scanner scanner;
	private boolean listening;
	private Thread thread;
	private Matlab mLab;

	public Workspace(InputStream in) {
		scanner = new Scanner(in);
		formatter = new WorkspaceFormatter();
		mLab = new Matlab();
		startListening();
	}

	public void startListening() {
		if (thread == null) {
			listening = true;
			thread = new Thread(this);
			thread.start();
			System.out.print(">> ");
		}
	}

	public void stopListening() {
		listening = false;
	}

	private List<Object> getStatements(String input) {
		// Get rid of spaces
		int opening = 0;
		for (int i = 0; i < input.length(); i++) {
			char current = input.charAt(i);
			switch (current) {
			case '[':
				opening++;
				break;
			case ']':
				opening--;
				break;
			case ' ':
				if (opening == 0) {
					if (i + 1 > input.length() - 1)
						input = input.substring(0, i);
					else {
						input = input.substring(0, i) + input.substring(i + 1, input.length());
						i--;
					}
				}
				break;
			}
		}

		opening = 0;
		List<String> statements = new ArrayList<String>();
		List<Boolean> displayList = new ArrayList<Boolean>();
		int begin = 0;
		int end = 0;
		for (int i = 0; i < input.length(); i++) {
			end = i;
			char currentChar = input.charAt(i);
			if (i == input.length() - 1 && currentChar != ';' && currentChar != ',') {
				statements.add(input.substring(begin, input.length()));
				displayList.add(true);
			}
			switch (currentChar) {
			case '[':
			case '(':
				opening++;
				break;
			case ']':
			case ')':
				opening--;
				break;
			case ';':
				if (opening == 0) {
					statements.add(input.substring(begin, end));
					displayList.add(false);
					begin = end + 1;
				}
				break;
			case ',':
				if (opening == 0) {
					statements.add(input.substring(begin, end));
					displayList.add(true);
					begin = end + 1;
				}
			}
		}
		List<Object> ret = new ArrayList<Object>();
		ret.add(statements.toArray(new String[0]));
		ret.add(displayList.toArray(new Boolean[0]));
		return ret;
	}

	@Override
	public void run() {
		while (scanner.hasNext() && listening) {
			String input = scanner.nextLine();
			try {
				List<Object> list = getStatements(input);
				String[] statements = (String[]) list.get(0);
				Boolean[] displayValues = (Boolean[]) list.get(1);
				for (int i = 0; i < statements.length; i++) {
					mLab.evaluate(statements[i]);
					if (displayValues[i]) {
						String finalOutput = "";
						String patternCharacters = "(?<![<>~=])=(?!=)";
						Pattern pattern = Pattern.compile(patternCharacters);
						Matcher matcher = pattern.matcher(input);
						if (matcher.find()) {
							int index = matcher.start();
							String left = input.substring(0, index);
							int numOutputs = left.length() - left.replace(",", "").length() + 1;
							if(numOutputs > 1){
								String[] vars = left.substring(1, left.length() - 1).split(",");
								for(int j = 0; j < vars.length; j++){
									String name = vars[j];
									finalOutput = finalOutput + formatter.formatAssignment(name, mLab.getVariables().get(name));
								}
							}
							else{
								left = left.replace("[", "").replace("]", "");
								finalOutput = finalOutput + formatter.formatAssignment(left, mLab.getVariables().get(left));
							}
						} else {
							finalOutput = finalOutput + formatter.formatAssignment("ans", mLab.getVariables().get("ans"));
						}
						System.out.println(finalOutput);
					}
				}
			} catch (WorkspaceInputException e) {
				String formattedOutput = formatter.formatError(input, e);
				System.out.println(formattedOutput);
				System.out.print(">> ");
				continue;
			}
			System.out.print(">> ");
		}
		scanner.close();
	}
}