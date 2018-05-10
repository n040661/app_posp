package xdt.dao.impl;

import com.alibaba.druid.pool.DruidDataSource;
import xdt.dao.IPmsMerchantBussinessDao;
import xdt.dao.impl.BaseDaoImpl;
import xdt.model.PmsMerchantBussiness;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.List;

@Repository
public class PmsMerchantBussinessDaoImpl extends BaseDaoImpl<PmsMerchantBussiness> implements IPmsMerchantBussinessDao {
	@Resource
	private DruidDataSource dataSource; //连接池
	
	/**
	 * 批量保存->给注册成功的商户分配的基本业务
	 * @param list
	 * @return
	 */
	public int saveMerchantBussinessInfo(List<PmsMerchantBussiness> list) throws Exception{
		Connection conn = null;
		PreparedStatement pstmt = null;
		int result = 0;
		try {
			String insert = "insert into PMS_MERCHANT_BUSSINESS (id,BUSINESSCODE,merc_id,STATUS,OAGENTNO) values (SEQ_PMS_MERCHANT_BUSSINESS.nextval,?,?,?,?)";
			//从池中获取连接 
	        conn = dataSource.getConnection(); 
	        pstmt = conn.prepareStatement(insert);
	        conn.setAutoCommit(false); // 开始事务
	        String businessCode = "";
	        String mercId = "";
	        String status = "";
	        String oAgentNo = "";
	        for (int i = 0; i< list.size(); i++) {
	        	PmsMerchantBussiness pmsMerchantBussiness = list.get(i);
	        	businessCode = pmsMerchantBussiness.getBusinessCode();
	        	mercId = pmsMerchantBussiness.getMercId();
	        	status = pmsMerchantBussiness.getStatus();
	        	oAgentNo = pmsMerchantBussiness.getoAgentNo();
				pstmt.setString(1,businessCode);
	        	pstmt.setString(2,mercId);
	        	pstmt.setString(3,status);
	        	pstmt.setString(4,oAgentNo);
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
}
