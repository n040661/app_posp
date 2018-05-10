package xdt.model;

public class PayCmmtufit {
    private String fitTrk;

    private String fitOfs;

    private String fitCtt;

    private String fitLen;

    private String crdTrk;

    private String crdOfs;

    private String crdLen;

    private String crdFlg; //00-借记卡\01-贷记卡\02-准贷记卡\03预付费卡


    private String expDtFlg;

    private String crdNm;

    private String intMod;

    private String bnkTyp;

    private String fitNo;

    private String tmSmp;

    private String bnkidFk;

    private String fitCon;

    private String bnkCode;

    private String bnkName;

    public String getFitTrk() {
        return fitTrk;
    }

    public void setFitTrk(String fitTrk) {
        this.fitTrk = fitTrk == null ? null : fitTrk.trim();
    }

    public String getFitOfs() {
        return fitOfs;
    }

    public void setFitOfs(String fitOfs) {
        this.fitOfs = fitOfs == null ? null : fitOfs.trim();
    }

    public String getFitCtt() {
        return fitCtt;
    }

    public void setFitCtt(String fitCtt) {
        this.fitCtt = fitCtt == null ? null : fitCtt.trim();
    }

    public String getFitLen() {
        return fitLen;
    }

    public void setFitLen(String fitLen) {
        this.fitLen = fitLen == null ? null : fitLen.trim();
    }

    public String getCrdTrk() {
        return crdTrk;
    }

    public void setCrdTrk(String crdTrk) {
        this.crdTrk = crdTrk == null ? null : crdTrk.trim();
    }

    public String getCrdOfs() {
        return crdOfs;
    }

    public void setCrdOfs(String crdOfs) {
        this.crdOfs = crdOfs == null ? null : crdOfs.trim();
    }

    public String getCrdLen() {
        return crdLen;
    }

    public void setCrdLen(String crdLen) {
        this.crdLen = crdLen == null ? null : crdLen.trim();
    }

    public String getCrdFlg() {
        return crdFlg;
    }

    public void setCrdFlg(String crdFlg) {
        this.crdFlg = crdFlg == null ? null : crdFlg.trim();
    }

    public String getExpDtFlg() {
        return expDtFlg;
    }

    public void setExpDtFlg(String expDtFlg) {
        this.expDtFlg = expDtFlg == null ? null : expDtFlg.trim();
    }

    public String getCrdNm() {
        return crdNm;
    }

    public void setCrdNm(String crdNm) {
        this.crdNm = crdNm == null ? null : crdNm.trim();
    }

    public String getIntMod() {
        return intMod;
    }

    public void setIntMod(String intMod) {
        this.intMod = intMod == null ? null : intMod.trim();
    }

    public String getBnkTyp() {
        return bnkTyp;
    }

    public void setBnkTyp(String bnkTyp) {
        this.bnkTyp = bnkTyp == null ? null : bnkTyp.trim();
    }

    public String getFitNo() {
        return fitNo;
    }

    public void setFitNo(String fitNo) {
        this.fitNo = fitNo == null ? null : fitNo.trim();
    }

    public String getTmSmp() {
        return tmSmp;
    }

    public void setTmSmp(String tmSmp) {
        this.tmSmp = tmSmp == null ? null : tmSmp.trim();
    }

    public String getBnkidFk() {
        return bnkidFk;
    }

    public void setBnkidFk(String bnkidFk) {
        this.bnkidFk = bnkidFk == null ? null : bnkidFk.trim();
    }

    public String getFitCon() {
        return fitCon;
    }

    public void setFitCon(String fitCon) {
        this.fitCon = fitCon == null ? null : fitCon.trim();
    }

    public String getBnkCode() {
        return bnkCode;
    }

    public void setBnkCode(String bnkCode) {
        this.bnkCode = bnkCode == null ? null : bnkCode.trim();
    }

    public String getBnkName() {
        return bnkName;
    }

    public void setBnkName(String bnkName) {
        this.bnkName = bnkName == null ? null : bnkName.trim();
    }
}