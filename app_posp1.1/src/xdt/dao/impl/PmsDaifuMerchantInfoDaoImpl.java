package xdt.dao.impl;

import java.util.List;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import xdt.dao.IPmsDaifuMerchantInfoDao;
import xdt.model.PmsDaifuMerchantInfo;
/**
 * @ClassName: PmsDaifuMerchantInfoDaoImpl
 * @Description:代付查询功能
 * @author YanChao.Shang
 * @date 2017年4月14日 下午4:15:11
 *
 */
@Repository
public class PmsDaifuMerchantInfoDaoImpl extends BaseDaoImpl<PmsDaifuMerchantInfo> implements IPmsDaifuMerchantInfoDao{



	public int insertSelective(PmsDaifuMerchantInfo record) {
		
		return 0;
	}

	public PmsDaifuMerchantInfo selectByDaifuMerchantInfo(PmsDaifuMerchantInfo model) {
	
		String sql = this.getStatementId("selectByDaifuMerchantInfo"); 
		return sqlSession.selectOne(sql,model);
	}
	public int insertSelectives(PmsDaifuMerchantInfo model) {
		
		String sql = this.getStatementId("insertSelectives"); 
		return sqlSession.insert(sql,model);
	}
	
	public List<PmsDaifuMerchantInfo> selectByDaifuMerchantInfos(PmsDaifuMerchantInfo model) {
		
		String sql = this.getStatementId("selectByDaifuMerchantInfo"); 
		return sqlSession.selectList(sql,model);
	}
	
	@Override
	public List<PmsDaifuMerchantInfo> selectPay() {
		String sql = this.getStatementId("selectPay");
		return sqlSession.selectList(sql);
	}

	@Override
	public List<PmsDaifuMerchantInfo> selectDaifu() {
		
		String sql = this.getStatementId("selectDaifu");
		return sqlSession.selectList(sql);
	}
	@Override
	public List<PmsDaifuMerchantInfo> selectDaifu1() {
		
		String sql = this.getStatementId("selectDaifu1");
		return sqlSession.selectList(sql);
	}
	@Override
	public List<PmsDaifuMerchantInfo> selectDaifu2() {
		
		String sql = this.getStatementId("selectDaifu2");
		return sqlSession.selectList(sql);
	}
	@Override
	public List<PmsDaifuMerchantInfo> selectDaifu3() {
		
		String sql = this.getStatementId("selectDaifu3");
		return sqlSession.selectList(sql);
	}
	//易联代付定时任务
	@Override
	public List<PmsDaifuMerchantInfo> selectYLDaifu() {
		
		String sql = this.getStatementId("selectYLDaifu");
		return sqlSession.selectList(sql);
	}
	
	@Override
	public List<PmsDaifuMerchantInfo> selectMerchantDaifu(String mercId) {
		
		String sql = this.getStatementId("selectMerchantDaifu");
		return sqlSession.selectList(sql,mercId);
	}
}
