package xdt.dao;

import java.util.List;

import xdt.model.PmsImage;

public interface IPmsImageDao extends IBaseDao<PmsImage>{

	/**
	 * 批量保存上传图片
	 * @param list
	 * @return
	 * @throws Exception
	 */
	public int saveUploadFiles(List<PmsImage> list,String crpIdNofo) throws Exception;
	
	/**
	 * 检索商户上传的文件
	 * @param mercId
	 * @return
	 * @throws Exception
	 */
	public List<PmsImage> searchUploadFiles(String mercId) throws Exception;
	
	/**
	 * 批量更新上传图片
	 * @param list
	 * @return
	 * @throws Exception
	 */
	public int updateUploadFiles(List<PmsImage> list) throws Exception;
	
	/**
	 * 插入上传文件的错误日志
	 * @param phone
	 * @param errorCode
	 */
	public void insertErrorLog(String phone,String errorCode)throws Exception;
}
