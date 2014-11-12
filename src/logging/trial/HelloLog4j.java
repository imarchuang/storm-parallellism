package logging.trial;

import org.apache.log4j.Logger;
 
public class HelloLog4j{
 
	final static Logger logger = Logger.getLogger(HelloLog4j.class);
 
	public static void main(String[] args) {
 
		HelloLog4j logObj = new HelloLog4j();
 
		try{
			logObj.divide();
		}catch(ArithmeticException ex){
			logger.error("Sorry, something wrong!", ex);
		}

		logObj.runLog4j("just for testing ...");
 
 
	}
 
	private void divide(){
 
		int i = 10 /0;
 
	}

	private void runLog4j(String parameter){
 
		if(logger.isDebugEnabled()){
			logger.debug("This is debug : " + parameter);
		}
 
		if(logger.isInfoEnabled()){
			logger.info("This is info : " + parameter);
		}
 
		logger.warn("This is warn : " + parameter);
		logger.error("This is error : " + parameter);
		logger.fatal("This is fatal : " + parameter);
 
	}
 
}
