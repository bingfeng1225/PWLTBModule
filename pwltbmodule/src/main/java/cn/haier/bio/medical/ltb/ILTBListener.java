package cn.haier.bio.medical.ltb;

import cn.haier.bio.medical.ltb.entity.LTBDataEntity;

public interface ILTBListener {
    void onLTBReady();
    void onLTBConnected();
    void onLTBSwitchWriteModel();
    void onLTBSwitchReadModel();
    void onLTBPrint(String message);
    void onLTBSystemChanged(int type);
    byte[] packageLTBResponse(int type);
    void onLTBException(Throwable throwable);
    void onLTBDataChanged(LTBDataEntity entity);
}
