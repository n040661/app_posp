package xdt.quickpay.shyb.util;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.httpclient.methods.multipart.Part;
import org.apache.commons.httpclient.methods.multipart.StringPart;

public class ReceviePartsBuiler {
	


    private List<Part> parts = new ArrayList<Part>(11);

    public Part[] generateParams() {
        return parts.toArray(new Part[parts.size()]);
    }

    public ReceviePartsBuiler setSource(String source) {
        this.parts.add(new StringPart("source", source == null ? "" : source,
                "UTF-8"));
        return this;
    }

    public ReceviePartsBuiler setMainCustomerNumber(String mainCustomerNumber) {
        this.parts.add(new StringPart("mainCustomerNumber",
                mainCustomerNumber == null ? "" : mainCustomerNumber, "UTF-8"));
        return this;
    }

    public ReceviePartsBuiler setCustomerNumber(String customerNumber) {
        this.parts.add(new StringPart("customerNumber",
                customerNumber == null ? "" : customerNumber, "UTF-8"));
        return this;
    }

    public ReceviePartsBuiler setRequestId(String requestId) {
        this.parts.add(new StringPart("requestId", requestId == null ? ""
                : requestId, "UTF-8"));
        return this;
    }

    public ReceviePartsBuiler setAmount(String amout) {
        this.parts.add(new StringPart("amount", amout == null ? "" : amout,
                "UTF-8"));
        return this;
    }

    public ReceviePartsBuiler setMcc(String mcc) {
        this.parts.add(new StringPart("mcc", mcc == null ? "" : mcc, "UTF-8"));
        return this;
    }

    public ReceviePartsBuiler setMobileNumber(String mobileNumber) {
        this.parts.add(new StringPart("mobileNumber", mobileNumber == null ? ""
                : mobileNumber, "UTF-8"));
        return this;
    }

    public ReceviePartsBuiler setCallBackUrl(String callBackUrl) {
        this.parts.add(new StringPart("callBackUrl", callBackUrl == null ? ""
                : callBackUrl, "UTF-8"));
        return this;
    }

    public ReceviePartsBuiler setWebCallBackUrl(String webCallBackUrl) {
        this.parts.add(new StringPart("webCallBackUrl",
                webCallBackUrl == null ? "" : webCallBackUrl, "UTF-8"));
        return this;
    }

    public ReceviePartsBuiler setSmgCallBackUrl(String smgCallBackUrl) {
        this.parts.add(new StringPart("smgCallBackUrl",
                smgCallBackUrl == null ? "" : smgCallBackUrl, "UTF-8"));
        return this;
    }

    public ReceviePartsBuiler setPayerBankAccountNo(String payerBankAccountNo) {
        this.parts.add(new StringPart("payerBankAccountNo",
                payerBankAccountNo == null ? "" : payerBankAccountNo, "UTF-8"));
        return this;
    }

    public ReceviePartsBuiler setHamc(String hmac) {
        this.parts
                .add(new StringPart("hmac", hmac == null ? "" : hmac, "UTF-8"));
        return this;
    }

    public ReceviePartsBuiler setCfca(String cfca) {
        this.parts
                .add(new StringPart("cfca", cfca == null ? "" : cfca, "UTF-8"));
        return this;
    }

    public ReceviePartsBuiler setDescription(String description) {
        this.parts.add(new StringPart("description", description == null ? ""
                : description, "UTF-8"));
        return this;
    }


    public ReceviePartsBuiler setAutoWithdraw(String autoWithdraw) {
        this.parts.add(new StringPart("autoWithdraw",
                autoWithdraw == null ? "" : autoWithdraw, "UTF-8"));
        return this;
    }

    public ReceviePartsBuiler setWithdrawCardNo(String withdrawCardNo) {
        this.parts.add(new StringPart("withdrawCardNo",
                withdrawCardNo == null ? "" : withdrawCardNo, "UTF-8"));
        return this;
    }


    public ReceviePartsBuiler setWithdrawCallBackUrl(String withdrawCallBackUrl) {
        this.parts.add(new StringPart("withdrawCallBackUrl",
                withdrawCallBackUrl == null ? "" : withdrawCallBackUrl, "UTF-8"));
        return this;
    }



}
