package xdt.dao;

import xdt.model.PayCmmtufit;

import java.util.List;

public interface IPayCmmtufitDao extends IBaseDao<PayCmmtufit>{
	
	/**
	 * 根据前6位数字检索银行卡信息
	 * @param beforeSixCardNumber
	 * @return
	 * @throws Exception
	 */
	public List<PayCmmtufit> searchCardInfoByBeforeSix(String beforeSixCardNumber)throws Exception;

    /**
     * 根据银行卡号检索银行卡信息
     * @param cardNum
     * @return
     */
    public PayCmmtufit selectByCardNum(String cardNum) throws Exception ;
}
