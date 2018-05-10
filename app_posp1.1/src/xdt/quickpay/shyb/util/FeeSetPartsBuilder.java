package xdt.quickpay.shyb.util;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.httpclient.methods.multipart.Part;
import org.apache.commons.httpclient.methods.multipart.StringPart;

public class FeeSetPartsBuilder {
	


    private List<Part> parts = new ArrayList<Part>(11);

    public Part[] generateParams() {
        return parts.toArray(new Part[parts.size()]);
    }


    public FeeSetPartsBuilder setCustomerNumber(String customerNumber) {
        this.parts.add(new StringPart("customerNumber", customerNumber == null ? "" : customerNumber, "UTF-8"));
        return this;
    }

    public FeeSetPartsBuilder setGroupCustomerNumber(String groupCustomerNumber) {
        this.parts.add(new StringPart("mainCustomerNumber", groupCustomerNumber == null ? ""
                : groupCustomerNumber, "UTF-8"));
        return this;
    }

    public FeeSetPartsBuilder setProductType(String productType) {
        this.parts.add(new StringPart("productType", productType == null ? ""
                : productType, "UTF-8"));
        return this;
    }

    public FeeSetPartsBuilder setRate(String rate) {
        this.parts.add(new StringPart("rate",
                rate == null ? "" : rate, "UTF-8"));
        return this;
    }

    public FeeSetPartsBuilder setCfca(String cfca) {
        this.parts
                .add(new StringPart("cfca", cfca == null ? "" : cfca, "UTF-8"));
        return this;
    }

    public FeeSetPartsBuilder setHmac(String hmac) {
        this.parts
                .add(new StringPart("hmac", hmac == null ? "" : hmac, "UTF-8"));
        return this;
    }


}
