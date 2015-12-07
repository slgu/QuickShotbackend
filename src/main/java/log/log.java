package log;
import org.apache.log4j.FileAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;


/**
 * Created by slgu1 on 11/21/15.
 */
public class log {
    static FileAppender fa = new FileAppender();
    public static Logger logger = Logger.getRootLogger();
    static {
        fa.setName("FileLogger");
        fa.setFile("/home/slgu/slgu.log");
        fa.setLayout(new PatternLayout("%d %-5p [%c{1}] %m%n"));
        fa.setThreshold(Level.DEBUG);
        fa.setAppend(true);
        fa.activateOptions();
        logger.addAppender(fa);
    }
    public static void debug(String text) {
        logger.debug(text);
    }
}
