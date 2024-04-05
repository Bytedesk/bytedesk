package com.bytedesk.ui.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ImageSpan;
import android.util.Log;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ExpressionUtil {
	
	/**
	 * 对spanableString进行正则判断，如果符合要求，则以表情图片代替
	 */
    public static void dealExpression(Context context, SpannableString spannableString, Pattern patten, int start) throws Exception {
    	Matcher matcher = patten.matcher(spannableString);
        while (matcher.find()) {
            String key = matcher.group();
            //Log.d("Key", key);
            if (matcher.start() < start) {
                continue;
            }
			int resId = KFResUtil.getResofR(context).getDrawable(key);

			if (resId != 0) {
                Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), resId);
                ImageSpan imageSpan = new ImageSpan(context,bitmap);
                int end = matcher.start() + key.length();
                spannableString.setSpan(imageSpan, matcher.start(), end, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
                //Log.d(TAG, end+":"+spannableString.toString());
                if (end < spannableString.length()) {
                    dealExpression(context,spannableString,  patten, end);
                }
                break;
            }
        }
    }
    
    public static SpannableString getExpressionString(Context context,String str,String zhengze){
    	//System.out.println("进来的内容 = " + str);
    	SpannableString spannableString = new SpannableString(str);
        Pattern sinaPatten = Pattern.compile(zhengze, Pattern.CASE_INSENSITIVE);		//通过传入的正则表达式来生成一个pattern
        try {
            dealExpression(context,spannableString, sinaPatten, 0);
        } catch (Exception e) {
            Log.e("dealExpression", e.getMessage());
        }
        return spannableString;
    }
    
    public static String faceToCN(Context context, String face) {

		String pattern = "appkefu_f0[0-9]{2}|appkefu_f10[0-5]";
		Pattern facePattern = Pattern.compile(pattern, Pattern.CASE_INSENSITIVE);
		Matcher faceMatcher = facePattern.matcher(face);
		
		EmotionMaps kfEmotionMap = new EmotionMaps(context);

		while (faceMatcher.find()) {
			
			String key = faceMatcher.group();
			String newkey = "";
			
			//KFLog.d("faceToCN: " + key);
			
			if (faceMatcher.start() < 0) {
				continue;
			}

			if (key.length() > 0) {
				newkey = key.substring(9);
			}

			int num = Integer.parseInt(newkey);
			
			String cnStr = context.getString(kfEmotionMap.kfEmotionStringResIdForIndex[num - 1]);
			
			face = face.replace(key, cnStr);
			
			//KFLog.d(cnStr +":"+ face);
		}

		return face;
	}
	
	public static String CNtoFace(Context context, String word) {
		
		String pattern = "\\[[\u4E00-\u9FA5]+\\]";
		Pattern facePattern = Pattern.compile(pattern, Pattern.CASE_INSENSITIVE);
		Matcher faceMatcher = facePattern.matcher(word);
				
		String face = "";
		while (faceMatcher.find()) {
						
			String key = faceMatcher.group();
			
			if(faceMatcher.start() < 0)
			{
				continue;
			}
			
			face = (String) EmotionMaps.faceMap.get(key);
			if (face != null) {
				word = word.replace(key, face);	
			}

			//KFLog.d(key +":" + face+ ":"+ word);
		}
		
		return word;
	}
}








