package com.TG.library.pojos;

public class FingerBean {
    //人员姓名
    private String name;
    //人员唯一ID
    private String personId;
    //指静脉特征唯一ID
    private String fingerDataId;
    //特征组号
    private int groupId;
    //特征3/6模板模式
    private int fingerModelType;
    //指静脉特征
    private byte[] fingerData;

    public FingerBean(String name, String personId, String fingerDataId, int groupId,
                      int fingerModelType, byte[] fingerData) {
        this.name = name;
        this.personId = personId;
        this.fingerDataId = fingerDataId;
        this.groupId = groupId;
        this.fingerModelType = fingerModelType;
        this.fingerData = fingerData;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPersonId() {
        return personId;
    }

    public void setPersonId(String personId) {
        this.personId = personId;
    }

    public String getFingerDataId() {
        return fingerDataId;
    }

    public void setFingerDataId(String fingerDataId) {
        this.fingerDataId = fingerDataId;
    }

    public int getGroupId() {
        return groupId;
    }

    public void setGroupId(int groupId) {
        this.groupId = groupId;
    }

    public int getFingerModelType() {
        return fingerModelType;
    }

    public void setFingerModelType(int fingerModelType) {
        this.fingerModelType = fingerModelType;
    }

    public byte[] getFingerData() {
        return fingerData;
    }

    public void setFingerData(byte[] fingerData) {
        this.fingerData = fingerData;
    }
}
