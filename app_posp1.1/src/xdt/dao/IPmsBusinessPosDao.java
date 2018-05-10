package xdt.dao;

import xdt.model.PmsBusinessPos;

public interface IPmsBusinessPosDao extends IBaseDao<PmsBusinessPos> {

	public PmsBusinessPos selectBusinessInfo(String id);
	
	public PmsBusinessPos selectBusinessposBusinessNum(String id) ;
}
