package Page1_schedule;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

class Page1_Util {
    // 날짜 포멧
    static String todayDate(){
        Date today = new Date();
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
        return format.format(today);
    }

    //시간 포멧
    static String parselDate(String date){
        String time = date.substring(8,12);
        return time.substring(0,2)+":"+time.substring(2,4);
    }

    //지난 시간인지 체크
    static Boolean lastTime(String date){
//        String time = date.substring(8,12);
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY,Integer.parseInt(date.substring(0,2)));
        calendar.set(Calendar.MINUTE,Integer.parseInt(date.substring(3,5)));
        return calendar.getTimeInMillis() < Calendar.getInstance().getTimeInMillis();
    }
}
