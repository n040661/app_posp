package xdt.dao.impl;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.util.List;
import java.util.Properties;

import javax.annotation.Resource;

import org.springframework.stereotype.Repository;

import xdt.controller.BaseAction;
import xdt.dao.IPmsImageDao;
import xdt.model.PmsImage;

import com.alibaba.druid.pool.DruidDataSource;

@Repository
public class PmsImageDaoImpl extends BaseDaoImpl<PmsImage> implements IPmsImageDao {

	@Resource
	private DruidDataSource dataSource;
	private static final String SELECTLIST = "searchImageList";//检索商户上传图片
	
	/**
	 * 批量保存上传图片
	 * @param list
	 * @return
	 */
	public int saveUploadFiles(List<PmsImage> list,String crpIdNofo)throws Exception{
		Connection conn = null;
		PreparedStatement pstmt = null;
		int result = 0;
		try {
			String insert = "insert into PMS_IMAGE (PATH,MERCHANT_NUM,CREATION_NAME,CREATIONDATE,FLAG) values (?,?,?,?,?)";
			//从池中获取连接 
	        conn = dataSource.getConnection(); 
	        pstmt = conn.prepareStatement(insert);
	        conn.setAutoCommit(false); // 开始事务
	        for (int i = 0; i< list.size(); i++) {
	        	PmsImage image = list.get(i);
				pstmt.setString(1,image.getPath());
	        	pstmt.setString(2,crpIdNofo);
	        	pstmt.setString(3,image.getCreationName());
	        	pstmt.setString(4,image.getCreationdate());
	        	pstmt.setInt(5, Integer.parseInt(image.getFlag().toString()));
	        	pstmt.addBatch();
			}
	        pstmt.executeBatch();
	        conn.commit();
		} catch (Exception e) {
			try {
				if(conn!=null){
					conn.close();
				}
				if(pstmt!=null){
					pstmt.close();
				}
			} catch (Exception e2) {
				throw e2;
			}
			throw e;
		}finally{
			try {
				if(conn!=null){
					conn.close();
				}
				if(pstmt!=null){
					pstmt.close();
				}
				result = list.size();
			} catch (Exception e2) {
				throw e2;
			}
		}
		return result;
	}

	/**
	 * 检索商户上传的文件
	 */
	@Override
	public List<PmsImage> searchUploadFiles(String mercId) throws Exception {
		String sql = getStatementId(SELECTLIST);
		return sqlSession.selectList(sql, mercId);
	}

	/**
	 * 批量更新上传文件
	 */
	@Override
	public int updateUploadFiles(List<PmsImage> list) throws Exception {
		Connection conn = null;
		PreparedStatement pstmt = null;
		int result = 0;
		try {
			String update = "update PMS_IMAGE t set t.removetag = 1 where t.id = ?";
			//从池中获取连接 
	        conn = dataSource.getConnection(); 
	        pstmt = conn.prepareStatement(update);
	        conn.setAutoCommit(false); // 开始事务
	        for (int i = 0; i< list.size(); i++) {
	        	String id = list.get(i).getId();
				pstmt.setString(1,id);
	        	pstmt.addBatch();
			}
	        pstmt.executeBatch();
	        conn.commit();
		} catch (Exception e) {
			try {
				if(conn!=null){
					conn.close();
				}
				if(pstmt!=null){
					pstmt.close();
				}
			} catch (Exception e2) {
				throw e2;
			}
			throw e;
		}finally{
			try {
				if(conn!=null){
					conn.close();
				}
				if(pstmt!=null){
					pstmt.close();
				}
				result = list.size();
			} catch (Exception e2) {
				throw e2;
			}
		}
		return result;
	}
	
	/**
	 * 插入上传文件的错误日志
	 */
	public void insertErrorLog(String phone,String errorCode) throws Exception{
		Connection conn = null;
		PreparedStatement pstmt = null;
		try {
			InputStream in = BaseAction.class.getResourceAsStream("/config/config.properties");
			Properties p = new Properties();
			p.load(in);
			String driverClassName = p.getProperty("driverClassName");
			String jdbc_url = p.getProperty("jdbc_url");
			String jdbc_username = p.getProperty("jdbc_username");
			String jdbc_password = p.getProperty("jdbc_password");
			String validationQuery = p.getProperty("validationQuery");
			String insert = "insert into ERROR_LOG (PHONE_NO,ERROR_DATE,ERROR_NO) values (?,?,?)";
			//从池中获取连接 
			dataSource = new DruidDataSource();
			dataSource.setDriverClassName(driverClassName); 
			dataSource.setUsername(jdbc_username);
			dataSource.setPassword(jdbc_password);
			dataSource.setUrl(jdbc_url);
			dataSource.setInitialSize(1);
			dataSource.setMinIdle(0);
			dataSource.setMaxActive(1);
			dataSource.setValidationQuery(validationQuery);
			dataSource.setTestOnBorrow(false);
			dataSource.setTestOnReturn(false);
			dataSource.setTestWhileIdle(true);
	        conn = dataSource.getConnection(); 
	        pstmt = conn.prepareStatement(insert);
	        conn.setAutoCommit(false); // 开始事务
	        pstmt.setString(1,phone);
	        pstmt.setTimestamp(2,new Timestamp(new java.util.Date().getTime()));
	        pstmt.setString(3,errorCode);
	        pstmt.execute();
	        conn.commit();
		} catch (Exception e) {
			try {
				if(conn!=null){
					conn.close();
				}
				if(pstmt!=null){
					pstmt.close();
				}
			} catch (Exception e2) {
				throw e2;
			}
			throw e;
		}finally{
			try {
				if(conn!=null){
					conn.close();
				}
				if(pstmt!=null){
					pstmt.close();
				}
			} catch (Exception e2) {
				throw e2;
			}
		}
	}
}