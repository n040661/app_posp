package xdt.dao.impl;

import org.springframework.stereotype.Repository;
import xdt.dao.INewsInfoDao;
import xdt.model.NewsInfo;
import xdt.util.PageView;

import java.util.List;

/**
 * 消息dao层
 * User: Jeff
 * Date: 15-12-11
 * Time: 下午4:26
 */
@Repository
public class NewsInfoDaoImpl extends BaseDaoImpl<NewsInfo> implements INewsInfoDao {


    @Override
    public PageView selectPageByOagentNo(PageView pageView) {
        return new PageView(Integer.parseInt(sqlSession.selectList("total", pageView).get(0).toString()),
                sqlSession.selectList("selectPage", pageView));
    }

    @Override
    public int countAllByOagentNo(String oagentNo) {

        Integer count = sqlSession.selectOne("totalCountByOagentNo", oagentNo);
        return count;
    }

    @Override
    public List<String> selectAllIdsByOagentNo(String oagentNo) {
        String sql = getStatementId("selectAllIds");
        List<String> result = sqlSession.selectList(sql,oagentNo);
        return result;
    }

    @Override
    public NewsInfo selectLoginRemind(String oAgentNo) {
        NewsInfo t = sqlSession.selectOne("selectLoginRemind",oAgentNo);
        return t;
    }
}
