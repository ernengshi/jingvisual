package cn.com.jingcloud.test;

//import com.squareup.tape2.QueueFile;
import cn.com.jingcloud.utils.Charsets;
import cn.com.jingcloud.utils.Utils;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.input.ReversedLinesFileReader;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author liyong
 */
public class CommonsUtilsTest {

    private final static Logger LOG = LoggerFactory.getLogger(CommonsUtilsTest.class);

    // 统计行数
    private static int getLineNumber(String fileName) {
        long start = System.currentTimeMillis();
        int lineNum = 0;
        LineNumberReader lnr = null;
        String str = null;
        String lastLine = "";
        try {
            lnr = new LineNumberReader(new InputStreamReader(
                    new FileInputStream(fileName)));
            while ((str = lnr.readLine()) != null) {
//                System.out.println(str + " : " + lnr.getLineNumber());
//                lastLine = str;
            }

            lineNum = lnr.getLineNumber();
            System.out.println("____last____" + lastLine);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (null != lnr) {
                    lnr.close();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }

        long end = System.currentTimeMillis();

        System.out.println("Use Time: " + (end - start) + " Line Num: "
                + lineNum);

        return lineNum;
    }

    /**
     * 逆序读取文件
     *
     * @throws IOException
     */
    public static void test() throws IOException {
        long begin = System.currentTimeMillis();
        String strpath = "C:\\Users\\ly\\Desktop\\aa\\test.log";
        ReversedLinesFileReader fr = new ReversedLinesFileReader(new File(strpath), 2048, "utf-8");
        String str;
        while ((str = fr.readLine()) != null) {
//            logger.info(str);
        }
        fr.close();
        long end = System.currentTimeMillis();
        System.out.println("total time " + (end - begin));
    }

    public static void testTime() {

        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);

//        long t = 1503458880158L;
        long t = cal.getTimeInMillis();
        System.out.println(t + "&&&&&&&&&" + System.currentTimeMillis());
        Date d = new Date(t);
        String time = DateFormatUtils.format(d, "yyyy-MM-dd HH:mm:ss");
        System.out.println("+++++++++++" + time);
    }

    /**
     * https://github.com/square/tape
     */
//    public static void testFileQueue() throws IOException {
//
//        File file = new File("C:\\Users\\ly\\Desktop\\aa\\test.log");
//        QueueFile queueFile = new QueueFile.Builder(file).build();
//        queueFile.add("data".getBytes());
//    }
    
    
    static void replaceFileEndLine(String path, String add) {
        try {
            File file = new File(path);
            List<String> list = FileUtils.readLines(file, Charsets.UTF_8);
            if (!Utils.isNullOrEmpty(list)) {
                String last = list.get(list.size() - 1);
                System.out.println("---last---"+last);
                Collections.replaceAll(list, last, add);
                FileUtils.writeLines(file, Charsets.UTF_8_NAME, list, false);
            }
        } catch (IOException ex) {
            LOG.error("replaceFileEndLine: " + ex.getMessage());
        }
    }
    
    public static void main(String[] args) throws IOException, ParseException {
//        getLineNumber("C:\\Users\\liyong\\Desktop\\aa\\index\\host\\5e221f04-3a7d-4a51-9962-601f33a90a49.index");
//        FileUtils.byteCountToDisplaySize(12121212);
//        test();
//        testTime();
//        testFileQueue();
//        System.out.println("******************" + FileUtils.byteCountToDisplaySize(11111));
//        AtomicInteger test = new AtomicInteger(0);
//        System.out.println(test.incrementAndGet());
//        String s = StringUtils.substringBeforeLast("1.11%", "%");
//        System.out.println(s);

        String date = DateFormatUtils.format(new Date(), "yyyy-MM-dd HH:mm:ss");
        System.out.println("******" + date);
        Date date1 = DateUtils.parseDate("2017-11-17 23:59:59", "yyyy-MM-dd HH:mm:ss");
        Date date2 = DateUtils.parseDate("2017-11-18 00:00:01", "yyyy-MM-dd HH:mm:ss");
        boolean b = DateUtils.isSameDay(date1, date2);
        System.out.println("---------------------" + b);

        List<String> list = new ArrayList<String>();
        list.add("sdf");
        list.add("ewrwe");
        String last = list.get(list.size() - 1);
        Collections.replaceAll(list, last, "aaa");
        System.out.println("-------"+list.toString());
        
        replaceFileEndLine("C:\\Users\\liyong\\Desktop\\aa\\test.index", "mmm2");
        
        FileObject o1 = new FileObject();
        o1.setFileLineNum(1);
        FileObject o2 = new FileObject();
        o2.setFileLineNum(2);
        System.out.println(o1.equals(o2));
        System.out.println(o2.getFileLineNum()+"--------"+o1.getFileLineNum());
    }
    
    private static final class FileObject {

        int fileLineNum;
        String lastLine;

        public int getFileLineNum() {
            return fileLineNum;
        }

        public void setFileLineNum(int fileLineNum) {
            this.fileLineNum = fileLineNum;
        }

        public String getLastLine() {
            return lastLine;
        }

        public void setLastLine(String lastLine) {
            this.lastLine = lastLine;
        }

    }
}
