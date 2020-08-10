package cn.haier.bio.medical.ltb;

public interface ILTBListener {
    void onLTBReady();
    void onLTBConnected();
    void onLTBSwitchWriteModel();
    void onLTBSwitchReadModel();
    void onLTBPrint(String message);
    void onLTBSystemChanged(int type);
    void onLTBDataChanged(byte[] data);
    byte[] packageLTBResponse(int type);
    void onLTBException(Throwable throwable);
}
