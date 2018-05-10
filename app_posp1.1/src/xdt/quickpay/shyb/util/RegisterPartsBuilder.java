package xdt.quickpay.shyb.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.httpclient.methods.multipart.FilePart;
import org.apache.commons.httpclient.methods.multipart.Part;
import org.apache.commons.httpclient.methods.multipart.StringPart;
public class RegisterPartsBuilder {


    private List<Part> parts = new ArrayList<Part>(26);

    public Part[] generateParams() {
        return parts.toArray(new Part[parts.size()]);
    }

    /**
     * @param mainCustomerNumber
     *            the mainCustomerNumber to set
     */
    public RegisterPartsBuilder setMainCustomerNumber(String mainCustomerNumber) {
        this.parts.add(new StringPart("mainCustomerNumber",
                mainCustomerNumber == null ? "" : mainCustomerNumber, "UTF-8"));
        return this;
    }

    /**
     * @param bindMobile
     *            the bindMobile to set
     */
    public RegisterPartsBuilder setBindMobile(String bindMobile) {
        this.parts.add(new StringPart("bindMobile", bindMobile == null ? ""
                : bindMobile, "UTF-8"));
        return this;
    }

    /**
     * @param signedName
     *            the signedName to set
     */
    public RegisterPartsBuilder setSignedName(String signedName) {
        this.parts.add(new StringPart("signedName", signedName == null ? ""
                : signedName, "UTF-8"));
        return this;
    }

    /**
     * @param linkMan
     *            the linkMan to set
     */
    public RegisterPartsBuilder setLinkMan(String linkMan) {
        this.parts.add(new StringPart("linkMan",
                linkMan == null ? "" : linkMan, "UTF-8"));
        return this;
    }

    /**
     * @param idCard
     *            the idCard to set
     */
    public RegisterPartsBuilder setIdCard(String idCard) {
        this.parts.add(new StringPart("idCard", idCard == null ? "" : idCard,
                "UTF-8"));
        return this;
    }

    /**
     * @param legalPerson
     *            the legalPerson to set
     */
    public RegisterPartsBuilder setLegalPerson(String legalPerson) {
        this.parts.add(new StringPart("legalPerson", legalPerson == null ? ""
                : legalPerson, "UTF-8"));
        return this;
    }

    /**
     * @param minSettleAmount
     *            the minSettleAmount to set
     */
    public RegisterPartsBuilder setMinSettleAmount(String minSettleAmount) {
        this.parts.add(new StringPart("minSettleAmount",
                minSettleAmount == null ? "" : minSettleAmount, "UTF-8"));
        return this;
    }

    /**
     * @param riskReserveDay
     *            the riskReserveDay to set
     */
    public RegisterPartsBuilder setRiskReserveDay(String riskReserveDay) {
        this.parts.add(new StringPart("riskReserveDay",
                riskReserveDay == null ? "" : riskReserveDay, "UTF-8"));
        return this;
    }

    /**
     * @param bankAccountNumber
     *            the bankAccountNumber to set
     */
    public RegisterPartsBuilder setBankAccountNumber(String bankAccountNumber) {
        this.parts.add(new StringPart("bankAccountNumber",
                bankAccountNumber == null ? "" : bankAccountNumber, "UTF-8"));
        return this;
    }

    /**
     * @param bankName
     *            the bankName to set
     */
    public RegisterPartsBuilder setBankName(String bankName) {

        this.parts.add(new StringPart("bankName", bankName == null ? ""
                : bankName, "UTF-8"));
        return this;
    }

    /**
     * @param accountName
     *            the cacountName to set
     */
    public RegisterPartsBuilder setAccountName(String accountName) {
        this.parts.add(new StringPart("accountName", accountName == null ? ""
                : accountName, "UTF-8"));
        return this;
    }

    /**
     * @param requestId
     *            the requestId to set
     */
    public RegisterPartsBuilder setRequestId(String requestId) {
        this.parts.add(new StringPart("requestId", requestId == null ? ""
                : requestId, "UTF-8"));
        return this;
    }

    /**
     * @param customerType
     *            the customerType to set
     */
    public RegisterPartsBuilder setCustomerType(String customerType) {
        this.parts.add(new StringPart("customerType", customerType == null ? ""
                : customerType, "UTF-8"));
        return this;
    }

    /**
     * @param hmac
     *            the hmac to set
     */
    public RegisterPartsBuilder setHmac(String hmac) {
        this.parts
                .add(new StringPart("hmac", hmac == null ? "" : hmac, "UTF-8"));
        return this;
    }

    /**
     * setter
     *
     * @param mailstr
     */
    public RegisterPartsBuilder setMailStr(String mailstr) {
        this.parts.add(new StringPart("mailstr",
                mailstr == null ? "" : mailstr, "UTF-8"));
        return this;
    }



    public RegisterPartsBuilder setBankaccounttype(String bankaccounttype) {
        this.parts.add(new StringPart("bankAccountType",
                bankaccounttype == null ? "" : bankaccounttype, "UTF-8"));
        return this;
    }

    /**
     * @param businessLicence
     *            the businessLicence to set
     */
    public RegisterPartsBuilder setBusinessLicence(String businessLicence) {
        this.parts.add(new StringPart("businessLicence",
                businessLicence == null ? "" : businessLicence, "UTF-8"));
        return this;
    }

    public RegisterPartsBuilder setAuditStatus(String auditStatus) {
        this.parts.add(new StringPart("auditStatus",
                auditStatus == null ? "" : auditStatus, "UTF-8"));
        return this;
    }

    // [end] jun.lin 2015-03-30 这里是普通入参

    // [start] jun.lin 2015-03-20 这里是文件入参

    private void configFilePart(File f, FilePart fp) {
        String fileName = f.getName();
        fp.setContentType("image/"
                + fileName.substring(fileName.lastIndexOf('.') + 1));
        fp.setCharSet("UTF-8");

        System.out.println(fp.getContentType());
    }

    private void configPdfFilePart(File f, FilePart fp) {
        String fileName = f.getName();
        fp.setContentType("application/"
                + fileName.substring(fileName.lastIndexOf('.') + 1));
        fp.setCharSet("UTF-8");

        System.out.println(fp.getContentType());
    }

    /**
     * @param idCardPhoto
     *            the idCardPhoto to set
     * @throws FileNotFoundException
     */
    public RegisterPartsBuilder setIdCardPhoto(File idCardPhoto)
            throws FileNotFoundException {
        FilePart fp = new FilePart("idCardPhoto", idCardPhoto);
        configFilePart(idCardPhoto, fp);
        this.parts.add(fp);

        return this;
    }

    // 目前非必传
    public RegisterPartsBuilder setIdCardBackPhoto(File idCardBackPhoto)
            throws FileNotFoundException {
        FilePart fp = new FilePart("idCardBackPhoto", idCardBackPhoto);
        configFilePart(idCardBackPhoto, fp);
        this.parts.add(fp);

        return this;
    }

    /**
     * @param bankCardPhoto
     *            the bankCardPhoto to set
     * @throws FileNotFoundException
     */
    public RegisterPartsBuilder setBankCardPhoto(File bankCardPhoto)
            throws FileNotFoundException {
        FilePart fp = new FilePart("bankCardPhoto", bankCardPhoto);
        configFilePart(bankCardPhoto, fp);
        this.parts.add(fp);
        return this;
    }

    /**
     * @param personPhoto
     *            the personPhoto to set
     * @throws FileNotFoundException
     */
    public RegisterPartsBuilder setPersonPhoto(File personPhoto)
            throws FileNotFoundException {
        FilePart fp = new FilePart("personPhoto", personPhoto);
        configFilePart(personPhoto, fp);
        this.parts.add(fp);
        return this;
    }

    /**
     * @param businessLicensePhoto
     *            the businessLicensePhoto to set
     * @throws FileNotFoundException
     */
    public RegisterPartsBuilder setBusinessLicensePhoto(
            File businessLicensePhoto) throws FileNotFoundException {
        FilePart fp = new FilePart("businessLicensePhoto", businessLicensePhoto);
        configFilePart(businessLicensePhoto, fp);
        this.parts.add(fp);
        return this;
    }


    public RegisterPartsBuilder setManualSettle(String manualSettle) {
        this.parts.add(new StringPart("manualSettle", manualSettle == null ? ""
                : manualSettle, "UTF-8"));
        return this;
    }

    public RegisterPartsBuilder setElectronicAgreement(File electronicAgreement)
            throws FileNotFoundException {
        FilePart fp = new FilePart("electronicAgreement", electronicAgreement);
        configPdfFilePart(electronicAgreement, fp);
        this.parts.add(fp);
        return this;
    }


}
