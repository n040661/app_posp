package xdt.dao;

import xdt.model.PmsBusinessInfo;

/**
 * Created with IntelliJ IDEA.
 * User: Jeff
 * Date: 15-8-29
 * Time: 下午5:09
 * To change this template use File | Settings | File Templates.
 */
public interface IPmsBusinessInfoDao extends IBaseDao<PmsBusinessInfo> {
	
	public PmsBusinessInfo selectBusinessInfo(String id);
	public PmsBusinessInfo selectBusinessInfoBusinessNum(String businessNum) ;
}
