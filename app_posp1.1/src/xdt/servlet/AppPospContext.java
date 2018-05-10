package xdt.servlet;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

import xdt.dao.IViewKyChannelInfoDao;
import xdt.model.ViewKyChannelInfo;
/**
 * key 采用  channelNum+businessnum+oAgentNo    即    渠道编号 +业务编号+O单编号
 * @author wm
 */
@Component
public class AppPospContext implements InitializingBean{

	@Resource
	private IViewKyChannelInfoDao viewKyChannelInfoDao;//渠道编号 +业务编号   
	
	public static Map<String ,ViewKyChannelInfo > context =  new HashMap<String, ViewKyChannelInfo>();
	
	@Override
	public void afterPropertiesSet() throws Exception {
		
		List<ViewKyChannelInfo> viewKyChannelInfoList = viewKyChannelInfoDao.selectAllChannelInfo();
		
		for(ViewKyChannelInfo viewKyChannelInfo: viewKyChannelInfoList){
			//key 采用  channelNum+businessnum+oAgentNo    即    渠道编号 +业务编号
			context.put(viewKyChannelInfo.getChannelNum()+viewKyChannelInfo.getBusinessnum(), viewKyChannelInfo);
		}
	}

}
