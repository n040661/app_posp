package xdt.dto;

import xdt.model.NewsInfo;
import xdt.model.NewsInfoSub;

import java.util.List;

/**
 * 消息返回
 * User: Jeff
 * Date: 15-12-11
 * Time: 下午3:03
 * To change this template use File | Settings | File Templates.
 */
public class NewsInfoQueryResponseDto {

    private Integer retCode;// 信息编号

    private String retMessage;// 信息描述

    private int pageNum; //当前页

    private int pageSize; //每页显示数量

    private int recordCount;//总记录数

    private int pageCount;//总页数

    private List<NewsInfoSub> newsList;//消息

    public Integer getRetCode() {
        return retCode;
    }

    public void setRetCode(Integer retCode) {
        this.retCode = retCode;
    }

    public String getRetMessage() {
        return retMessage;
    }

    public void setRetMessage(String retMessage) {
        this.retMessage = retMessage;
    }

    public int getPageNum() {
        return pageNum;
    }

    public void setPageNum(int pageNum) {
        this.pageNum = pageNum;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public int getRecordCount() {
        return recordCount;
    }

    public void setRecordCount(int recordCount) {
        this.recordCount = recordCount;
    }

    public int getPageCount() {
        return pageCount;
    }

    public void setPageCount(int pageCount) {
        this.pageCount = pageCount;
    }

    public List<NewsInfoSub> getNewsList() {
        return newsList;
    }

    public void setNewsList(List<NewsInfoSub> newsList) {
        this.newsList = newsList;
    }
}
