package logging.logback;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Hello world!
 *
 */

public class LogbackHello {
	private static final Logger logger = LoggerFactory.getLogger(LogbackHello.class);
	public static void main(String[] args) {
		
	logger.trace("Hello World!");
	String name = "Marc Huang";
	logger.debug("Hi, {}", name);	
	logger.info("Welcome to the HelloWorld example of Logback.");
    logger.warn("Dummy warning message.");
    logger.error("Dummy error message.");
	}
}