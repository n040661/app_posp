package xdt.model;

import java.math.BigDecimal;

public class PospRouteInfo {
    private BigDecimal id;

    private String channelCode;

    private BigDecimal merchantId;

    private BigDecimal posId;

    private BigDecimal status;

    private String effectFrom;

    private String effectTo;

    private BigDecimal priority;

    private BigDecimal ownerId;

    private String priorityMer;

    private String rounttype;

    public BigDecimal getId() {
        return id;
    }

    public void setId(BigDecimal id) {
        this.id = id;
    }

    public String getChannelCode() {
        return channelCode;
    }

    public void setChannelCode(String channelCode) {
        this.channelCode = channelCode == null ? null : channelCode.trim();
    }

    public BigDecimal getMerchantId() {
        return merchantId;
    }

    public void setMerchantId(BigDecimal merchantId) {
        this.merchantId = merchantId;
    }

    public BigDecimal getPosId() {
        return posId;
    }

    public void setPosId(BigDecimal posId) {
        this.posId = posId;
    }

    public BigDecimal getStatus() {
        return status;
    }

    public void setStatus(BigDecimal status) {
        this.status = status;
    }

    public String getEffectFrom() {
        return effectFrom;
    }

    public void setEffectFrom(String effectFrom) {
        this.effectFrom = effectFrom == null ? null : effectFrom.trim();
    }

    public String getEffectTo() {
        return effectTo;
    }

    public void setEffectTo(String effectTo) {
        this.effectTo = effectTo == null ? null : effectTo.trim();
    }

    public BigDecimal getPriority() {
        return priority;
    }

    public void setPriority(BigDecimal priority) {
        this.priority = priority;
    }

    public BigDecimal getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(BigDecimal ownerId) {
        this.ownerId = ownerId;
    }

    public String getPriorityMer() {
        return priorityMer;
    }

    public void setPriorityMer(String priorityMer) {
        this.priorityMer = priorityMer == null ? null : priorityMer.trim();
    }

    public String getRounttype() {
        return rounttype;
    }

    public void setRounttype(String rounttype) {
        this.rounttype = rounttype == null ? null : rounttype.trim();
    }
}