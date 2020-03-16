package cn.haier.bio.medical.ltb;


import cn.haier.bio.medical.ltb.entity.LTBDataEntity;

public interface ILTBListener {
    void onLTBReady();
    void onLTBConnected();
    void onLTBException();
    void onLTBSwitchWriteModel();
    void onLTBSwitchReadModel();
    byte[] packageLTBResponse(int type);
    boolean onLTBSystemChanged(int type);
    void onLTBStateChanged(LTBDataEntity entity);
}
