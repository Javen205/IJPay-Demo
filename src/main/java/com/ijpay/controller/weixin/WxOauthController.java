package com.ijpay.controller.weixin;

import com.jfinal.kit.PropKit;
import com.jfinal.log.Log;
import com.jfinal.weixin.sdk.api.ApiConfigKit;
import com.jfinal.weixin.sdk.api.SnsAccessToken;
import com.jfinal.weixin.sdk.api.SnsAccessTokenApi;
import com.jfinal.weixin.sdk.jfinal.ApiController;

/**
 * @author Javen
 */
public class WxOauthController extends ApiController{
	static Log log = Log.getLog(WxOauthController.class);
	
	public void index() {
		int  subscribe=1;
		//用户同意授权，获取code
		String code=getPara("code");
		String state=getPara("state");
		if (code!=null) {
			String appId=ApiConfigKit.getApiConfig().getAppId();
			String secret=ApiConfigKit.getApiConfig().getAppSecret();
			//通过code换取网页授权access_token
			SnsAccessToken snsAccessToken=SnsAccessTokenApi.getSnsAccessToken(appId,secret,code);
//			String json=snsAccessToken.getJson();
//			String token=snsAccessToken.getAccessToken();
			String openId=snsAccessToken.getOpenid();
			System.out.println("openId>"+openId);
			//拉取用户信息(需scope为 snsapi_userinfo)
//			ApiResult apiResult=SnsApi.getUserInfo(token, openId);
//			
//			log.warn("getUserInfo:"+apiResult.getJson());
//			if (apiResult.isSucceed()) {
//				JSONObject jsonObject=JSON.parseObject(apiResult.getJson());
//				String nickName=jsonObject.getString("nickname");
//				//用户的性别，值为1时是男性，值为2时是女性，值为0时是未知
//				int sex=jsonObject.getIntValue("sex");
//				String city=jsonObject.getString("city");//城市
//				String province=jsonObject.getString("province");//省份
//				String country=jsonObject.getString("country");//国家
//				String headimgurl=jsonObject.getString("headimgurl");
//				String unionid=jsonObject.getString("unionid");
//				//获取用户信息判断是否关注
//				ApiResult userInfo = UserApi.getUserInfo(openId);
//				log.warn(JsonKit.toJson("is subsribe>>"+userInfo));
//				if (userInfo.isSucceed()) {
//					String userStr = userInfo.toString();
//					subscribe=JSON.parseObject(userStr).getIntValue("subscribe");
//				}
//				Users.me.save(openId, WeiXinUtils.filterWeixinEmoji(nickName), unionid, headimgurl, country, city, province, sex);
//			}
			
			setSessionAttr("openId", openId);
			if (subscribe==0) {
				redirect(PropKit.get("subscribe_rul"));
			}else {
				//根据state 跳转到不同的页面
				if (state.equals("2222")) {
					redirect("http://www.cnblogs.com/zyw-205520/");
				}else if(state.equals("wxsubpay")){
					redirect("/towxsubpay");
				}else if(state.equals("wxpay")){
					redirect("/towxpay");
				}else{
					redirect("/oneqrpay/toPage?partnerId="+state);
				}
			}
			
			
		}else {
			renderText("code is  null");
		}
	}
	
}