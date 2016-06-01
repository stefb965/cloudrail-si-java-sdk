import java.util.Scanner;

import com.cloudrail.si.servicecode.commands.awaitCodeRedirect.RedirectReceiver;

/**
 * RedirectReceiver implementation that uses console interaction.
 * Use like CloudStorage cs = new Dropbox(new ConsoleRedirectReceiver(), "[clientID]", "[clientSecret]", "http://localhost:12345/auth", "someState");
 */
public class ConsoleRedirectReceiver implements RedirectReceiver {

	public String openAndAwait(String url, String currentState) {
		try {
			System.out.println("open URL: " + url);
			
			Scanner keyboard = new Scanner(System.in);
			
			System.out.println("Enter the URL:");
			String redirectUrlString = keyboard.nextLine();
			
			keyboard.close();
			
			return redirectUrlString;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

}
