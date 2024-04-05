package com.bytedesk.ui.util;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.widget.GridView;
import android.widget.SimpleAdapter;

import com.bytedesk.ui.R;
import com.bytedesk.ui.activity.ChatIMActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EmotionMaps {

	private Context m_context;

	private ArrayList<GridView>		m_gridViewArrayList;
	private GridView				m_emotionViewPagerGridView1;
	private GridView				m_emotionViewPagerGridView2;
	private GridView				m_emotionViewPagerGridView3;
	private GridView				m_emotionViewPagerGridView4;
	private GridView				m_emotionViewPagerGridView5;

	private List<Map<String, Object>> emotionItemMapsForGridView1;
	private List<Map<String, Object>> emotionItemMapsForGridView2;
	private List<Map<String, Object>> emotionItemMapsForGridView3;
	private List<Map<String, Object>> emotionItemMapsForGridView4;
	private List<Map<String, Object>> emotionItemMapsForGridView5;

	public EmotionMaps(Context context) {
		m_context = context;
		initEmotionViewPager();
	}

	public List<Map<String, Object>> getEmotionItemMapsForGridView1() {

		if(emotionItemMapsForGridView1 == null)
		{
			emotionItemMapsForGridView1 = new ArrayList<>();

			for(int i = 0; i < 21; i++)
			{
				Map<String, Object> emotionItemMap = new HashMap<>();
				emotionItemMap.put("image", kfEmotionIdsForGridView[i]);
				emotionItemMapsForGridView1.add(emotionItemMap);
			}
		}

		return emotionItemMapsForGridView1;
	}

	public List<Map<String, Object>> getEmotionItemMapsForGridView2() {

		if(emotionItemMapsForGridView2 == null)
		{
			emotionItemMapsForGridView2 = new ArrayList<>();

			for(int i = 21; i < 42; i++)
			{
				Map<String, Object> emotionItemMap = new HashMap<>();
				emotionItemMap.put("image", kfEmotionIdsForGridView[i]);
				emotionItemMapsForGridView2.add(emotionItemMap);
			}
		}

		return emotionItemMapsForGridView2;
	}

	public List<Map<String, Object>> getEmotionItemMapsForGridView3() {

		if(emotionItemMapsForGridView3 == null)
		{
			emotionItemMapsForGridView3 = new ArrayList<>();

			for(int i = 42; i < 63; i++)
			{
				Map<String, Object> emotionItemMap = new HashMap<>();
				emotionItemMap.put("image", kfEmotionIdsForGridView[i]);
				emotionItemMapsForGridView3.add(emotionItemMap);
			}
		}

		return emotionItemMapsForGridView3;
	}

	public List<Map<String, Object>> getEmotionItemMapsForGridView4() {

		if(emotionItemMapsForGridView4 == null)
		{
			emotionItemMapsForGridView4 = new ArrayList<>();

			for(int i = 63; i < 84; i++)
			{
				Map<String, Object> emotionItemMap = new HashMap<>();
				emotionItemMap.put("image", kfEmotionIdsForGridView[i]);
				emotionItemMapsForGridView4.add(emotionItemMap);
			}
		}

		return emotionItemMapsForGridView4;
	}

	public List<Map<String, Object>> getEmotionItemMapsForGridView5() {

		if(emotionItemMapsForGridView5 == null)
		{
			emotionItemMapsForGridView5 = new ArrayList<>();

			for(int i = 84; i < 105; i++)
			{
				Map<String, Object> emotionItemMap = new HashMap<>();
				emotionItemMap.put("image", kfEmotionIdsForGridView[i]);
				emotionItemMapsForGridView5.add(emotionItemMap);
			}
		}

		return emotionItemMapsForGridView5;
	}

	public int[] kfEmotionIdsForGridView = new int[] {
		R.drawable.appkefu_f001,
		R.drawable.appkefu_f002,
		R.drawable.appkefu_f003,
		R.drawable.appkefu_f004,
		R.drawable.appkefu_f005,
		R.drawable.appkefu_f006,
		R.drawable.appkefu_f007,
		R.drawable.appkefu_f008,
		R.drawable.appkefu_f009,
		R.drawable.appkefu_f010,
		R.drawable.appkefu_f011,
		R.drawable.appkefu_f012,
		R.drawable.appkefu_f013,
		R.drawable.appkefu_f014,
		R.drawable.appkefu_f015,
		R.drawable.appkefu_f016,
		R.drawable.appkefu_f017,
		R.drawable.appkefu_f018,
		R.drawable.appkefu_f019,
		R.drawable.appkefu_f020,
		R.drawable.appkefu_del_btn_nor,

		R.drawable.appkefu_f022,
		R.drawable.appkefu_f023,
		R.drawable.appkefu_f024,
		R.drawable.appkefu_f025,
		R.drawable.appkefu_f026,
		R.drawable.appkefu_f027,
		R.drawable.appkefu_f028,
		R.drawable.appkefu_f029,
		R.drawable.appkefu_f030,
		R.drawable.appkefu_f031,
		R.drawable.appkefu_f032,
		R.drawable.appkefu_f033,
		R.drawable.appkefu_f034,
		R.drawable.appkefu_f035,
		R.drawable.appkefu_f036,
		R.drawable.appkefu_f037,
		R.drawable.appkefu_f038,
		R.drawable.appkefu_f039,
		R.drawable.appkefu_f040,
		R.drawable.appkefu_f041,
		R.drawable.appkefu_del_btn_nor,

		R.drawable.appkefu_f043,
		R.drawable.appkefu_f044,
		R.drawable.appkefu_f045,
		R.drawable.appkefu_f046,
		R.drawable.appkefu_f047,
		R.drawable.appkefu_f048,
		R.drawable.appkefu_f049,
		R.drawable.appkefu_f050,
		R.drawable.appkefu_f051,
		R.drawable.appkefu_f052,
		R.drawable.appkefu_f053,
		R.drawable.appkefu_f054,
		R.drawable.appkefu_f055,
		R.drawable.appkefu_f056,
		R.drawable.appkefu_f057,
		R.drawable.appkefu_f058,
		R.drawable.appkefu_f059,
		R.drawable.appkefu_f060,
		R.drawable.appkefu_f061,
		R.drawable.appkefu_f062,
		R.drawable.appkefu_del_btn_nor,

		R.drawable.appkefu_f064,
		R.drawable.appkefu_f065,
		R.drawable.appkefu_f066,
		R.drawable.appkefu_f067,
		R.drawable.appkefu_f068,
		R.drawable.appkefu_f069,
		R.drawable.appkefu_f070,
		R.drawable.appkefu_f071,
		R.drawable.appkefu_f072,
		R.drawable.appkefu_f073,
		R.drawable.appkefu_f074,
		R.drawable.appkefu_f075,
		R.drawable.appkefu_f076,
		R.drawable.appkefu_f077,
		R.drawable.appkefu_f078,
		R.drawable.appkefu_f079,
		R.drawable.appkefu_f080,
		R.drawable.appkefu_f081,
		R.drawable.appkefu_f082,
		R.drawable.appkefu_f083,
		R.drawable.appkefu_del_btn_nor,

		R.drawable.appkefu_f085,
		R.drawable.appkefu_f086,
		R.drawable.appkefu_f087,
		R.drawable.appkefu_f088,
		R.drawable.appkefu_f089,
		R.drawable.appkefu_f090,
		R.drawable.appkefu_f091,
		R.drawable.appkefu_f092,
		R.drawable.appkefu_f093,
		R.drawable.appkefu_f094,
		R.drawable.appkefu_f095,
		R.drawable.appkefu_f096,
		R.drawable.appkefu_f097,
		R.drawable.appkefu_f098,
		R.drawable.appkefu_f099,
		R.drawable.appkefu_f100,
		R.drawable.appkefu_f101,
		R.drawable.appkefu_f102,
		R.drawable.appkefu_f103,
		R.drawable.appkefu_f104,
		R.drawable.appkefu_del_btn_nor
	};


	public int[] kfEmotionStringResIdForIndex = new int[] {
		R.string.appkefu_f001,
		R.string.appkefu_f002,
		R.string.appkefu_f003,
		R.string.appkefu_f004,
		R.string.appkefu_f005,
		R.string.appkefu_f006,
		R.string.appkefu_f007,
		R.string.appkefu_f008,
		R.string.appkefu_f009,
		R.string.appkefu_f010,

		R.string.appkefu_f011,
		R.string.appkefu_f012,
		R.string.appkefu_f013,
		R.string.appkefu_f014,
		R.string.appkefu_f015,
		R.string.appkefu_f016,
		R.string.appkefu_f017,
		R.string.appkefu_f018,
		R.string.appkefu_f019,
		R.string.appkefu_f020,

		R.string.appkefu_f021,
		R.string.appkefu_f022,
		R.string.appkefu_f023,
		R.string.appkefu_f024,
		R.string.appkefu_f025,
		R.string.appkefu_f026,
		R.string.appkefu_f027,
		R.string.appkefu_f028,
		R.string.appkefu_f029,
		R.string.appkefu_f030,

		R.string.appkefu_f031,
		R.string.appkefu_f032,
		R.string.appkefu_f033,
		R.string.appkefu_f034,
		R.string.appkefu_f035,
		R.string.appkefu_f036,
		R.string.appkefu_f037,
		R.string.appkefu_f038,
		R.string.appkefu_f039,
		R.string.appkefu_f040,

		R.string.appkefu_f041,
		R.string.appkefu_f042,
		R.string.appkefu_f043,
		R.string.appkefu_f044,
		R.string.appkefu_f045,
		R.string.appkefu_f046,
		R.string.appkefu_f047,
		R.string.appkefu_f048,
		R.string.appkefu_f049,
		R.string.appkefu_f050,

		R.string.appkefu_f051,
		R.string.appkefu_f052,
		R.string.appkefu_f053,
		R.string.appkefu_f054,
		R.string.appkefu_f055,
		R.string.appkefu_f056,
		R.string.appkefu_f057,
		R.string.appkefu_f058,
		R.string.appkefu_f059,
		R.string.appkefu_f060,

		R.string.appkefu_f061,
		R.string.appkefu_f062,
		R.string.appkefu_f063,
		R.string.appkefu_f064,
		R.string.appkefu_f065,
		R.string.appkefu_f066,
		R.string.appkefu_f067,
		R.string.appkefu_f068,
		R.string.appkefu_f069,
		R.string.appkefu_f070,

		R.string.appkefu_f071,
		R.string.appkefu_f072,
		R.string.appkefu_f073,
		R.string.appkefu_f074,
		R.string.appkefu_f075,
		R.string.appkefu_f076,
		R.string.appkefu_f077,
		R.string.appkefu_f078,
		R.string.appkefu_f079,
		R.string.appkefu_f080,

		R.string.appkefu_f081,
		R.string.appkefu_f082,
		R.string.appkefu_f083,
		R.string.appkefu_f084,
		R.string.appkefu_f085,
		R.string.appkefu_f086,
		R.string.appkefu_f087,
		R.string.appkefu_f088,
		R.string.appkefu_f089,
		R.string.appkefu_f090,

		R.string.appkefu_f091,
		R.string.appkefu_f092,
		R.string.appkefu_f093,
		R.string.appkefu_f094,
		R.string.appkefu_f095,
		R.string.appkefu_f096,
		R.string.appkefu_f097,
		R.string.appkefu_f098,
		R.string.appkefu_f099,
		R.string.appkefu_f100,

		R.string.appkefu_f101,
		R.string.appkefu_f102,
		R.string.appkefu_f103,
		R.string.appkefu_f104,
		R.string.appkefu_f105

	};


	@SuppressWarnings("unchecked")
	public static Map faceMap = new HashMap(){{

		put("[微笑]","appkefu_f001");
		put("[撇嘴]","appkefu_f002");
		put("[色]","appkefu_f003");
		put("[发呆]","appkefu_f004");
		put("[得意]","appkefu_f005");
		put("[流泪]","appkefu_f006");
		put("[害羞]","appkefu_f007");
		put("[闭嘴]","appkefu_f008");
		put("[睡]","appkefu_f009");
		put("[大哭]","appkefu_f010");

		put("[尴尬]","appkefu_f011");
		put("[发怒]","appkefu_f012");
		put("[调皮]","appkefu_f013");
		put("[呲牙]","appkefu_f014");
		put("[惊讶]","appkefu_f015");
		put("[难过]","appkefu_f016");
		put("[酷]","appkefu_f017");
		put("[冷汗]","appkefu_f018");
		put("[抓狂]","appkefu_f019");
		put("[吐]","appkefu_f020");

		put("[偷笑]","appkefu_f021");
		put("[可爱]","appkefu_f022");
		put("[白眼]","appkefu_f023");
		put("[傲慢]","appkefu_f024");
		put("[饥饿]","appkefu_f025");
		put("[困]","appkefu_f026");
		put("[惊恐]","appkefu_f027");
		put("[流汗]","appkefu_f028");
		put("[憨笑]","appkefu_f029");
		put("[大兵]","appkefu_f030");

		put("[奋斗]","appkefu_f031");
		put("[咒骂]","appkefu_f032");
		put("[疑问]","appkefu_f033");
		put("[嘘]","appkefu_f034");
		put("[晕]","appkefu_f035");
		put("[折磨]","appkefu_f036");
		put("[衰]","appkefu_f037");
		put("[骷髅]","appkefu_f038");
		put("[敲打]","appkefu_f039");
		put("[再见]","appkefu_f040");

		put("[擦汗]","appkefu_f041");
		put("[抠鼻]","appkefu_f042");
		put("[鼓掌]","appkefu_f043");
		put("[糗大了]","appkefu_f044");
		put("[坏笑]","appkefu_f045");
		put("[左哼哼]","appkefu_f046");
		put("[右哼哼]","appkefu_f047");
		put("[哈欠]","appkefu_f048");
		put("[鄙视]","appkefu_f049");
		put("[委屈]","appkefu_f050");

		put("[快哭了]","appkefu_f051");
		put("[阴险]","appkefu_f052");
		put("[亲亲]","appkefu_f053");
		put("[吓]","appkefu_f054");
		put("[可怜]","appkefu_f055");
		put("[菜刀]","appkefu_f056");
		put("[西瓜]","appkefu_f057");
		put("[啤酒]","appkefu_f058");
		put("[篮球]","appkefu_f059");
		put("[乒乓球]","appkefu_f060");

		put("[喝茶]","appkefu_f061");
		put("[米饭]","appkefu_f062");
		put("[猪头]","appkefu_f063");
		put("[玫瑰]","appkefu_f064");
		put("[垂玫瑰]","appkefu_f065");
		put("[爱心]","appkefu_f066");
		put("[红爱心]","appkefu_f067");
		put("[伤心]","appkefu_f068");
		put("[蛋糕]","appkefu_f069");
		put("[闪电]","appkefu_f070");

		put("[地雷]","appkefu_f071");
		put("[匕首]","appkefu_f072");
		put("[足球]","appkefu_f073");
		put("[瓢虫]","appkefu_f074");
		put("[便便]","appkefu_f075");
		put("[月亮]","appkefu_f076");
		put("[太阳]","appkefu_f077");
		put("[礼物]","appkefu_f078");
		put("[抱抱]","appkefu_f079");
		put("[强]","appkefu_f080");

		put("[差]","appkefu_f081");
		put("[握手]","appkefu_f082");
		put("[胜利]","appkefu_f083");
		put("[抱拳]","appkefu_f084");
		put("[勾引]","appkefu_f085");
		put("[拳头]","appkefu_f086");
		put("[差劲]","appkefu_f087");
		put("[爱你]","appkefu_f088");
		put("[不]","appkefu_f089");
		put("[好]","appkefu_f090");

		put("[吻]","appkefu_f091");
		put("[飞心]","appkefu_f092");
		put("[歪歪]","appkefu_f093");
		put("[发抖]","appkefu_f094");
		put("[怒吼]","appkefu_f095");
		put("[单脚立]","appkefu_f096");
		put("[立正]","appkefu_f097");
		put("[背偷笑]","appkefu_f098");
		put("[跳]","appkefu_f099");
		put("[挥手]","appkefu_f100");

		put("[激动]","appkefu_f101");
		put("[双脚跳]","appkefu_f102");
		put("[飞吻]","appkefu_f103");
		put("[母企鹅]","appkefu_f104");
		put("[公企鹅]","appkefu_f105");
	}};

	private void initEmotionViewPager() {

		m_gridViewArrayList = new ArrayList<>();

		LayoutInflater inflater = LayoutInflater.from(m_context);

		m_emotionViewPagerGridView1 = (GridView) inflater.inflate(R.layout.bytedesk_emotion_gridview, null);
		m_emotionViewPagerGridView1.setSelector(new ColorDrawable(Color.TRANSPARENT));

		m_emotionViewPagerGridView2 = (GridView) inflater.inflate(R.layout.bytedesk_emotion_gridview, null);
		m_emotionViewPagerGridView2.setSelector(new ColorDrawable(Color.TRANSPARENT));

		m_emotionViewPagerGridView3 = (GridView) inflater.inflate(R.layout.bytedesk_emotion_gridview, null);
		m_emotionViewPagerGridView3.setSelector(new ColorDrawable(Color.TRANSPARENT));

		m_emotionViewPagerGridView4 = (GridView) inflater.inflate(R.layout.bytedesk_emotion_gridview, null);
		m_emotionViewPagerGridView4.setSelector(new ColorDrawable(Color.TRANSPARENT));

		m_emotionViewPagerGridView5 = (GridView) inflater.inflate(R.layout.bytedesk_emotion_gridview, null);
		m_emotionViewPagerGridView5.setSelector(new ColorDrawable(Color.TRANSPARENT));

		m_gridViewArrayList.add(m_emotionViewPagerGridView1);

		m_gridViewArrayList.add(m_emotionViewPagerGridView2);

		m_gridViewArrayList.add(m_emotionViewPagerGridView3);

		m_gridViewArrayList.add(m_emotionViewPagerGridView4);

		m_gridViewArrayList.add(m_emotionViewPagerGridView5);

		//////////////////////////////////////////////
		SimpleAdapter m_emotionViewPagerGridView1Adapter = new SimpleAdapter(m_context,
				getEmotionItemMapsForGridView1(),
				R.layout.bytedesk_emotion_singleview,
				new String[] { "image" },
				new int[] {R.id.appkefu_emotion_single_imageview });
		SimpleAdapter m_emotionViewPagerGridView2Adapter = new SimpleAdapter(m_context,
				getEmotionItemMapsForGridView2(),
				R.layout.bytedesk_emotion_singleview,
				new String[] { "image" },
				new int[] { R.id.appkefu_emotion_single_imageview });
		SimpleAdapter m_emotionViewPagerGridView3Adapter = new SimpleAdapter(m_context,
				getEmotionItemMapsForGridView3(),
				R.layout.bytedesk_emotion_singleview,
				new String[] { "image" },
				new int[] { R.id.appkefu_emotion_single_imageview });
		SimpleAdapter m_emotionViewPagerGridView4Adapter = new SimpleAdapter(m_context,
				getEmotionItemMapsForGridView4(),
				R.layout.bytedesk_emotion_singleview,
				new String[] { "image" },
				new int[] { R.id.appkefu_emotion_single_imageview });
		SimpleAdapter m_emotionViewPagerGridView5Adapter = new SimpleAdapter(m_context,
				getEmotionItemMapsForGridView5(),
				R.layout.bytedesk_emotion_singleview,
				new String[] { "image" },
				new int[] { R.id.appkefu_emotion_single_imageview });

		///////////////////////////////////////////////////////////////

		m_emotionViewPagerGridView1.setAdapter(m_emotionViewPagerGridView1Adapter);
		m_emotionViewPagerGridView1.setOnItemClickListener((ChatIMActivity)m_context);

		m_emotionViewPagerGridView2.setAdapter(m_emotionViewPagerGridView2Adapter);
		m_emotionViewPagerGridView2.setOnItemClickListener((ChatIMActivity)m_context);

		m_emotionViewPagerGridView3.setAdapter(m_emotionViewPagerGridView3Adapter);
		m_emotionViewPagerGridView3.setOnItemClickListener((ChatIMActivity)m_context);

		m_emotionViewPagerGridView4.setAdapter(m_emotionViewPagerGridView4Adapter);
		m_emotionViewPagerGridView4.setOnItemClickListener((ChatIMActivity)m_context);

		m_emotionViewPagerGridView5.setAdapter(m_emotionViewPagerGridView5Adapter);
		m_emotionViewPagerGridView5.setOnItemClickListener((ChatIMActivity)m_context);

	}

	public ArrayList<GridView> getGridViewArrayList() {

		return m_gridViewArrayList;
		
	}
	
}































