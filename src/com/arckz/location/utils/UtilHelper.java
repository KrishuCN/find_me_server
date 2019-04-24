package com.arckz.location.utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

public class UtilHelper {
    public String video_domain = "http://res.sevenglish.com";
    String ak = "I5rekBtV0VEowU2K0gPRInwCohnYZL4-2Q2YMHPO";
    String sk = "R_1V-Hfe9-unZ3sPgK1klk879lB5N93V-pEfTyVi";

    public static String GetNameRelBaby(String relType)
    {
        //学生与家长关系：未提供，爸爸，妈妈，爷爷，奶奶，t=老师
        //父子，父女，母子，母女，祖孙
        if (relType.equals("父子"))
        {
            relType = "爸爸";
        } else if (relType.equals("父女"))
        {
            relType = "爸爸";
        } else if (relType.equals("母子"))
        {
            relType = "妈妈";
        } else if (relType.equals("母女"))
        {
            relType = "妈妈";
        } else if (relType.equals("祖孙"))
        {
            relType = "爷爷/奶奶";
        } else if (relType.equals("未提供"))
        {
            relType = "家长";
        } else if (relType.equals("t"))
        {
            relType = "老师";
        }
        return relType;
    }

    public static JSONObject GetPostJSONObject(HttpServletRequest request)
    {
        JSONObject jsonObject = null;

        StringBuffer responseStrBuffer = new StringBuffer();
        String line = null;
        try
        {
            BufferedReader streamReader = new BufferedReader(new InputStreamReader(request.getInputStream(), "UTF-8"));
            while ((line = streamReader.readLine()) != null)
            {
                responseStrBuffer.append(line);
            }
        } catch (Exception e)
        {
        }
        if (responseStrBuffer.length() > 0)
        {
            try
            {
                jsonObject = new JSONObject(responseStrBuffer.toString());
            } catch (JSONException e)
            {
                // e.printStackTrace();
            }
        }
        if (jsonObject == null)
        {
            jsonObject = new JSONObject();
        }
        return jsonObject;
    }


    public static String getUUID32()
    {
        return UUID.randomUUID().toString().replace("-", "").toLowerCase();
    }

    public static String GetSomeStr(String tmp, int len)
    {
        if (IsEmpty(tmp))
        {
            return "";
        } else
        {
            if (tmp.length() >= len)
            {
                return tmp.substring(0, len);
            } else
            {
                return tmp;
            }
        }
    }

    public static String getIP(HttpServletRequest request)
    {
        String result = "";
        String ip = request.getHeader("X-Forwarded-For");
        if (!IsEmpty(ip) && !"unKnown".equalsIgnoreCase(ip))
        {
            int index = ip.indexOf(",");
            if (index != -1)
            {
                result = ip.substring(0, index);
            } else
            {
                result = ip;
            }
        }
        ip = request.getHeader("X-Real-IP");
        if (!IsEmpty(ip) && !"unKnown".equalsIgnoreCase(ip))
        {
            result = ip;
        }
        result = request.getRemoteAddr();
        return ReplaceString(result, "[^0-9\\.]");
    }


    /*
     * 将时间转换为时间戳
     */
    public static String dateToStamp(String s)
    {
        if (!IsEmpty(s))
        {
            try
            {
                String res;
                SimpleDateFormat simpleDateFormat = null;
                if (s.contains(" "))
                {
                    simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                } else
                {
                    simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
                }
                Date date = simpleDateFormat.parse(s);
                long ts = date.getTime();
                res = String.valueOf(ts);
                return res;
            } catch (ParseException ex)
            {
                return "";
            }
        } else
        {
            return "";
        }
    }

    /*
     * 将时间戳转换为时间
     */
    public static String stampToDate(String s, String fmt)
    {
        if (!IsEmpty(s))
        {
            try
            {
                if (s.contains("."))
                {
                    s = s.split(".")[0];
                }
                if (IsEmpty(fmt))
                {
                    fmt = "yyyy-MM-dd HH:mm:ss";
                }
                String res;
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat(fmt);

                long lt = new Long(s);
                Date date = new Date(lt);
                res = simpleDateFormat.format(date);
                return res;
            } catch (Exception ex)
            {
                return "";
            }
        } else
        {
            return "";
        }
    }

    /*
     *按格式将时间校验后转格式字符串yyyy-MM-dd HH:mm:ss
     *System.out.println(UtilHelper.FormatDateString("2012-05-06","yyyy-MM-dd"));
     **/
    public static String FormatDateString(String dateStr, String fmt)
    {

        if (IsEmpty(dateStr))
        {
            return "";
        } else
        {
            if (IsEmpty(fmt))
            {
                fmt = "yyyy-MM-dd";
            }
            DateFormat df1 = new SimpleDateFormat(fmt);//"yyyy-MM-dd HH:mm:ss" 2016-05-19 10:21:02
            try
            {
                Date date = df1.parse(dateStr);
                return df1.format(date);
            } catch (Exception ex)
            {
                return df1.format(new Date());
            }
        }
    }

    /*
     *按格式将时间对象转格式字符串yyyy-MM-dd HH:mm:ss
     *Date dt=UtilHelper.parseStringToDateTime("2012-05-06 22:22:33","yyyy-MM-dd HH:mm:ss");
        System.out.println(UtilHelper.DateTimeFormatToString(dt,"HH:mm:ss"));
     **/
    public static String DateTimeFormatToString(Date dt, String fmt)
    {
        if (IsEmpty(fmt))
        {
            fmt = "yyyy-MM-dd";
        }
        DateFormat df1 = new SimpleDateFormat(fmt);//"yyyy-MM-dd HH:mm:ss" 2016-05-19 10:21:02
        try
        {
            return df1.format(dt);
        } catch (Exception ex)
        {
            return df1.format(new Date());
        }
    }

    /*
     * 将时间字符串转时间格式
     * Date dt=UtilHelper.parseStringToDateTime("2012-05-06 22:22:33","yyyy-MM-dd HH:mm:ss");
        System.out.println(UtilHelper.DateTimeFormatToString(dt,"HH:mm:ss"));
     * */
    public static Date parseStringToDateTime(String dateString, String fmt)
    {

        if (IsEmpty(dateString))
        {
            return new Date();
        } else
        {
            if (IsEmpty(fmt))
            {
                fmt = "yyyy-MM-dd";
            }
            DateFormat df1 = new SimpleDateFormat(fmt);//"yyyy-MM-dd HH:mm:ss"2016-05-19 10:21:02
            try
            {
                return df1.parse(dateString);
            } catch (Exception ex)
            {
                return new Date();
            }
        }
    }

    /*获取随机码*/
    public static String getRandNum(int charCount)
    {
        String charValue = "";
        for (int i = 0; i < charCount; i++)
        {
            char c = (char) (randomInt(0, 10) + '0');
            charValue += String.valueOf(c);
        }
        return charValue;
    }

    public static int randomInt(int from, int to)
    {
        Random r = new Random();
        return from + r.nextInt(to - from);
    }


    public static String getURLEncoderString(String str, String charset)
    {
        String result = "";
        if (IsEmpty(str))
        {
            return "";
        }
        try
        {
            result = java.net.URLEncoder.encode(str, charset);
        } catch (UnsupportedEncodingException e)
        {
            // e.printStackTrace();
        }
        return result;
    }

    public static String URLDecoderString(String str, String charset)
    {
        String result = "";
        if (IsEmpty(str))
        {
            return "";
        }
        try
        {
            result = java.net.URLDecoder.decode(str, charset);
        } catch (UnsupportedEncodingException e)
        {
            // e.printStackTrace();
        }
        return result;
    }

    /*通过正则表达式过滤其字符为空*/
    public static String ReplaceString(String input, String pattern)
    {
        if (IsEmpty(input))
        {
            return "";
        } else
        {
            if (IsEmpty(pattern))
            {
                return input;
            } else
            {
                Pattern r = Pattern.compile(pattern);//"[^a-zA-Z0-9\u4e00-\u9fa5\\|\\&%#\\/）（@【】:，。、：;；,=\\.！？\\[\\]\\(\\)\\s\\n\\r_-]");
                Matcher m = r.matcher(input);
                return m.replaceAll("");
            }
        }
    }

    public static BigDecimal getBigDecimal(Object value)
    {
        try
        {
            BigDecimal ret = null;
            if (value != null)
            {
                if (value instanceof BigDecimal)
                {
                    ret = (BigDecimal) value;
                } else if (value instanceof String)
                {
                    ret = new BigDecimal((String) value);
                } else if (value instanceof BigInteger)
                {
                    ret = new BigDecimal((BigInteger) value);
                } else if (value instanceof Number)
                {
                    ret = new BigDecimal(((Number) value).doubleValue());
                } else
                {
                    ret = new BigDecimal("0");
                }
                //			else {
                //				throw new ClassCastException("Not possible to coerce [" + value + "] from class " + value.getClass() + " into a BigDecimal.");
                //			}
            }
            if (ret == null)
            {
                return new BigDecimal("0");
            }
            return ret;
        } catch (NumberFormatException e)
        {
            return new BigDecimal("0");
        }
    }

    public static Boolean IsEmpty(Object obj)
    {
        if (obj == null || obj.equals(""))
        {
            return true;
        }
        return false;

    }

    public static String GetString(Object obj)
    {
        if (IsEmpty(obj))
        {
            return "";
        } else
        {
            if (obj.equals("null") || obj.equals("NULL"))
            {
                return "";
            }
        }
        return obj.toString();
    }

    public static Double GetDouble(String str)
    {
        try
        {
            return Double.parseDouble(str);
        } catch (NumberFormatException e)
        {
            return 0d;
        }
    }

    public static int GetInt(String str)
    {
        try
        {
            return Integer.parseInt(str);
        } catch (NumberFormatException e)
        {
            return 0;
        }
    }




	/*
 public String sendhtmlemail(String st,String msag,String title){
		String subject = title;
		String host = "smtp.exmail.qq.com";
     String from = "sale@680.com";
		Properties props = System.getProperties();
		props.put("mail.smtp.host",host);
		MyAuthenticator myauth = new MyAuthenticator("sale@680.com", "123987gu**");
		javax.mail.Session se = javax.mail.Session.getDefaultInstance(props, myauth);
		MimeMessage message = new MimeMessage(se);
		try
     {
         message.setFrom(new InternetAddress(from));
         message.addRecipient(javax.mail.Message.RecipientType.TO, new InternetAddress(st));
         message.setSubject(subject);
         BodyPart mdp = new MimeBodyPart();
         mdp.setContent(msag, "text/html;charset=gb2312");
         Multipart mm = new MimeMultipart();
         mm.addBodyPart(mdp);
         message.setContent(mm);
         message.saveChanges();
         Transport.send(message);
         System.out.println("   ͳɹ ");
         return st;
     }
     catch(AddressException e)
     {
         e.printStackTrace();
     }
     catch(MessagingException e)
     {
         e.printStackTrace();
     }
     return "";
	}
	 */

}
