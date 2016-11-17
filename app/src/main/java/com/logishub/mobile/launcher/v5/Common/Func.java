package com.logishub.mobile.launcher.v5.Common;

import android.app.ProgressDialog;
import android.content.Context;
import android.text.InputFilter;
import android.text.Spanned;
import android.util.Base64;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Func {

    public static ProgressDialog onCreateProgressDialog(Context context)
    {
        ProgressDialog progDialog = new ProgressDialog(context)
        {
            @Override
            public boolean onSearchRequested() {
                return false;
            }
        };

        progDialog.setCancelable(false);

        return progDialog;
    }

    /** Base64 EnCode */
    public static String getBase64Encode(String content) {
        return Base64.encodeToString(content.getBytes(), 0);
    }

    /** Base64 DeCode */
    public static String getBase64decode(String content){
        return new String(Base64.decode(content, 0));
    }

    /** 문자열에서 숫자만 출력 */
    public static String isValidNumber(String str) {
        if ( str == null ) return "";

        StringBuffer sb = new StringBuffer();
        for(int i = 0; i < str.length(); i++){
            if( Character.isDigit( str.charAt(i) ) ) {
                sb.append( str.charAt(i) );
            }
        }
        return sb.toString();
    }

    /** 문자열 확인 */
    public static boolean isValidString(String strVal) {
        if (strVal.isEmpty()) {
            return false;
        }

        return true;
    }

    /** 핸드폰 체크 */
    public static boolean isValidCellPhoneNumber(String cellphoneNumber) {
        boolean returnValue = false;
        String regex = "^\\s*(010|011|012|013|014|015|016|017|018|019)(-|\\)|\\s)*(\\d{3,4})(-|\\s)*(\\d{4})\\s*$";
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(cellphoneNumber);
        if (m.matches()) {
            returnValue = true;
        }
        return returnValue;
    }

    /** null check */
    public static String checkStringNull(String strVal) {
        if (strVal == null || strVal == "null" || strVal == "") {
            strVal = "";
        }

        return strVal;
    }

    /** edit text 빈값 체크 */
    public static String checkEditTextString(String strVal) {
        if (strVal == null || strVal.equals("")) {
            strVal = "0";
        }

        return strVal;
    }

    /** 영문만 허용 */
    public static InputFilter filterAlpha = new InputFilter() {
        public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {

            Pattern ps = Pattern.compile("^[a-zA-Z]+$");
            if (!ps.matcher(source).matches()) {
                return "";
            }
            return null;
        }
    };

    /** 영문 + 숫자 허용 */
    public static InputFilter filterAlphaNum = new InputFilter() {
        public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {

            Pattern ps = Pattern.compile("^[a-zA-Z0-9]+$");
            if (!ps.matcher(source).matches()) {
                return "";
            }
            return null;
        }
    };


    /** 한글만 허용 */
    public static InputFilter filterKor = new InputFilter() {
        public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {

            Pattern ps = Pattern.compile("^[ㄱ-ㅣ가-힣]*$");
            if (!ps.matcher(source).matches()) {
                return "";
            }
            return null;
        }
    };

    /** now Year */
    public static String nowYear(int year) {
        String returnValue = "";

        long now = System.currentTimeMillis();
        Date date = new Date(now);
        SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy");

        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.YEAR, year);

        returnValue = sdfDate.format(cal.getTime());

        return returnValue;
    }

    /** now Date */
    public static String nowDate(int month, int day) {
        String returnValue = "";

        long now = System.currentTimeMillis();
        Date date = new Date(now);
        SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd");

        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.MONTH, month);
        cal.add(Calendar.DATE, day);

        returnValue = sdfDate.format(cal.getTime());

        return returnValue;
    }

    /** now Quater Date */
    public static String nowQuarterDate(int quarter, int startEnd) {
        String returnValue = "";

        long now = System.currentTimeMillis();
        Date date = new Date(now);
        SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd");

        Calendar cal = Calendar.getInstance();
        cal.setTime(date);

        if (quarter < 0) {
            /** 지난분기*/
            if (startEnd < 0) {
                cal.add(Calendar.MONTH, (Integer.valueOf(Calendar.MONTH / 3) * 3) - 1);
                cal.add(Calendar.MONTH, - 3);
                cal.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.getActualMinimum(Calendar.DAY_OF_MONTH));
            } else {
                cal.add(Calendar.MONTH, (Integer.valueOf(Calendar.MONTH / 3) * 3) - 1);
                cal.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.getActualMinimum(Calendar.DAY_OF_MONTH));
                cal.add(Calendar.DATE, - 1);
            }
        } else {
            /** 이번분기*/
            if (startEnd < 0) {
                cal.add(Calendar.MONTH, (Integer.valueOf(Calendar.MONTH / 3) * 3) - 1);
                cal.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.getActualMinimum(Calendar.DAY_OF_MONTH));
            } else {
                cal.add(Calendar.MONTH, (Integer.valueOf(Calendar.MONTH / 3) * 3) - 1);
                cal.add(Calendar.MONTH, + 3);
                cal.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.getActualMinimum(Calendar.DAY_OF_MONTH));
                cal.add(Calendar.DATE, - 1);
            }
        }

        returnValue = sdfDate.format(cal.getTime());

        return returnValue;
    }

    /** now Date */
    public static String nowTime(int hour) {
        String returnValue = "";

        long now = System.currentTimeMillis();
        Date date = new Date(now);
        SimpleDateFormat sdfDate = new SimpleDateFormat("HH:mm");

        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.HOUR, hour);

        returnValue = sdfDate.format(cal.getTime());

        return returnValue;
    }

    /** 해당 주의 일요일*/
    public static String nowWeekSunDay() {
        String returnValue = "";

        SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd");

        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, (1 - cal.get(Calendar.DAY_OF_WEEK)));

        returnValue = sdfDate.format(cal.getTime());

        return returnValue;
    }

    /** 해당 주의 토요일*/
    public static String nowWeekSaturDay() {
        String returnValue = "";

        SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd");

        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, (7 - cal.get(Calendar.DAY_OF_WEEK)));

        returnValue = sdfDate.format(cal.getTime());

        return returnValue;
    }

    /** 지난 월의 첫째날*/
    public static String agoMonthFirstDay() {
        String returnValue = "";

        long now = System.currentTimeMillis();
        Date date = new Date(now);
        SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd");

        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.getActualMinimum(Calendar.DAY_OF_MONTH));
        cal.add(Calendar.MONTH, -1);

        returnValue = sdfDate.format(cal.getTime());

        return returnValue;
    }

    /** 지난 월의 마지막날*/
    public static String agoMonthLastDay() {
        String returnValue = "";

        long now = System.currentTimeMillis();
        Date date = new Date(now);
        SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd");

        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.MONTH, -1);
        cal.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.getActualMaximum(Calendar.DAY_OF_MONTH));

        returnValue = sdfDate.format(cal.getTime());

        return returnValue;
    }

    /** 해당 월의 첫째날*/
    public static String nowMonthFirstDay() {
        String returnValue = "";

        long now = System.currentTimeMillis();
        Date date = new Date(now);
        SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd");

        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.getActualMinimum(Calendar.DAY_OF_MONTH));

        returnValue = sdfDate.format(cal.getTime());

        return returnValue;
    }

    /** 해당 월의 마지막날*/
    public static String nowMonthLastDay() {
        String returnValue = "";

        long now = System.currentTimeMillis();
        Date date = new Date(now);
        SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd");

        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.getActualMaximum(Calendar.DAY_OF_MONTH));

        returnValue = sdfDate.format(cal.getTime());

        return returnValue;
    }

    public static String setPrice(String price, Boolean format) {
        String returnValue = "";
        double deliveryPrice = 0;

        if (checkStringNull(price).equals("") || checkStringNull(price).equals("0")) price = "0";

        if (Double.valueOf(price) > 0) {
            if (format) {
                deliveryPrice = Double.valueOf(price) * 0.0001;
            } else {
                deliveryPrice = Double.valueOf(price);
            }
            returnValue = (int)deliveryPrice + "만";
        } else {
            returnValue = "미정";
        }

        return returnValue;
    }

    public static int replaceString(String strVal) {
        int returnValue = 0;

        String clean = strVal.replaceAll("[^0-9]", "");

        if (clean.length() > 0) {
            returnValue = Integer.valueOf(clean);
        }

        return returnValue;
    }
}
