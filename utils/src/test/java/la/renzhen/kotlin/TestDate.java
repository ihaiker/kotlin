package la.renzhen.kotlin;

import la.renzhen.kotlin.utils.DatesKt;
import org.junit.Test;

import java.util.Calendar;
import java.util.Date;

/**
 * <p>
 *
 * @author <a href="mailto:zhouhaichao@2008.sina.com">haiker</a>
 * @version 13/09/2018 8:25 PM
 */
public class TestDate {

    @Test
    public void testDate(){
        System.out.println(DatesKt.isWeekFirst(new Date(), Calendar.MONDAY));
    }
}
