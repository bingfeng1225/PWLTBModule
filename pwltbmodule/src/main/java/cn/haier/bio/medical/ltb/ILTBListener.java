package cn.haier.bio.medical.ltb;


import cn.haier.bio.medical.ltb.entity.LTBDataEntity;

public interface ILTBListener {
    void onLTBReady();
    void onLTBConnected();
    void onLTBException();
    void onLTBSystemChanged(int type);
    byte[] packageLTBResponse(int type);
    void onLTBStateChanged(LTBDataEntity entity);
}
