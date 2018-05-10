package xdt.quickpay.shyb.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.httpclient.methods.multipart.FilePart;
import org.apache.commons.httpclient.methods.multipart.Part;
import org.apache.commons.httpclient.methods.multipart.StringPart;

public class TransferPartsBuilder {
	private List<Part> parts = new ArrayList<Part>(26);

    public Part[] generateParams() {
        return parts.toArray(new Part[parts.size()]);
    }

    /**
     * @param mainCustomerNumber
     *            the mainCustomerNumber to set
     */
    public TransferPartsBuilder setMainCustomerNumber(String mainCustomerNumber) {
        this.parts.add(new StringPart("mainCustomerNumber",
                mainCustomerNumber == null ? "" : mainCustomerNumber, "UTF-8"));
        return this;
    }

    /**
     * @param customerNumber
     *            the customerNumber to set
     */
    public TransferPartsBuilder setCustomerNumber(String customerNumber) {
        this.parts.add(new StringPart("customerNumber", customerNumber == null ? ""
                : customerNumber, "UTF-8"));
        return this;
    }

    /**
     * @param externalNo
     *            the externalNo to set
     */
    public TransferPartsBuilder setExternalNo(String externalNo) {
        this.parts.add(new StringPart("externalNo", externalNo == null ? ""
                : externalNo, "UTF-8"));
        return this;
    }

    /**
     * @param transferWay
     *            the transferWay to set
     */
    public TransferPartsBuilder setTransferWay(String transferWay) {
        this.parts.add(new StringPart("transferWay",
                transferWay == null ? "" : transferWay, "UTF-8"));
        return this;
    }

    /**
     * @param amount
     *            the amount to set
     */
    public TransferPartsBuilder setAmount(String amount) {
        this.parts.add(new StringPart("amount", amount == null ? "" : amount,
                "UTF-8"));
        return this;
    }

    /**
     * @param callBackUrl
     *            the callBackUrl to set
     */
    public TransferPartsBuilder setCallBackUrl(String callBackUrl) {
        this.parts.add(new StringPart("callBackUrl", callBackUrl == null ? ""
                : callBackUrl, "UTF-8"));
        return this;
    }

    /**
     * @param hmac
     *            the hmac to set
     */
    public TransferPartsBuilder setHmac(String hmac) {
        this.parts.add(new StringPart("hmac",
                hmac == null ? "" : hmac, "UTF-8"));
        return this;
    }

}
