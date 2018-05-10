package xdt.dao.impl;

import xdt.dao.IErrorLogDao;
import xdt.dao.impl.BaseDaoImpl;
import xdt.model.ErrorLog;
import org.springframework.stereotype.Repository;

@Repository
public class ErrorLogDaoImpl extends BaseDaoImpl<ErrorLog> implements IErrorLogDao {
	
}
