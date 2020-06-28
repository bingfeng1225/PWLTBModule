package cn.haier.bio.medical.ltb;

import cn.haier.bio.medical.ltb.entity.LTBDataEntity;

public interface ILTBListener {
    void onLTBReady();
    void onLTBConnected();
    void onLTBSwitchWriteModel();
    void onLTBSwitchReadModel();
    void onLTBPrint(String message);
    byte[] packageLTBResponse(int type);
    boolean onLTBSystemChanged(int type);
    void onLTBException(Throwable throwable);
    void onLTBStateChanged(LTBDataEntity entity);
}
