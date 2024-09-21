package com.friendly.services.infrastructure.base;

import com.ftacs.*;

import javax.xml.ws.WebServiceException;
import java.util.List;

public class ACSWebServiceMock implements ACSWebService {

    @Override
    public void createUpdateGroup(UpdateGroupWS updateGroup, String user) throws Exception_Exception {
        throw new WebServiceException();
    }

    @Override
    public void stopEvent(Integer parentEventId, boolean needReset) throws Exception_Exception {
        throw new WebServiceException();
    }

    @Override
    public void addCPE(CpeList cpeList, Integer locationId, String user) throws Exception_Exception {
        throw new WebServiceException();
    }

    @Override
    public void tracerouteCPE(Integer cpe) throws Exception_Exception {
        throw new WebServiceException();
    }

    @Override
    public void downloadFilesWithoutTask(IntegerArrayWS cpeList, FileListWS fileList, Integer priority, String user,
                                         Integer locationId) throws Exception_Exception {
        throw new WebServiceException();
    }

    @Override
    public void deleteCPEFromWhiteList(IntegerArrayWS cpwWhiteListIds) throws Exception_Exception {
        throw new WebServiceException();
    }

    @Override
    public void updateMonitoring(ParentMonitoringWS parentMonitoring, Integer parentMonitoringId, String user)
            throws Exception_Exception {
        throw new WebServiceException();
    }

    @Override
    public ParameterListWS retrieveLicenseDecrypted() {
        throw new WebServiceException();
    }

    @Override
    public void modifyProductClassGroupParameter(IntegerArrayWS productClassGroupList, StringArrayWS parameterList)
            throws Exception_Exception {
        throw new WebServiceException();
    }

    @Override
    public void deleteEvent(IntegerArrayWS ids) throws Exception_Exception {
        throw new WebServiceException();
    }

    @Override
    public Boolean removeIsps(IntegerArrayWS ispsList) throws Exception_Exception {
        throw new WebServiceException();
    }

    @Override
    public void stopSOAPTracingForUnknownCPEs(CpesoapTracingListWS cpeList) throws Exception_Exception {
        throw new WebServiceException();
    }

    @Override
    public BooleanResponse push(Integer cpe, Integer timeout) throws Exception_Exception {
        throw new WebServiceException();
    }

    @Override
    public IspWS updateIsp(IspWS ispsList) throws Exception_Exception {
        throw new WebServiceException();
    }

    @Override
    public ConfigurationParameterList getConfigurationParameters(ProtocolId arg0) {
        throw new WebServiceException();
    }

    @Override
    public void deleteCPEFromBlackList(CpeBlackListWS cpeBlackList) throws Exception_Exception {
        throw new WebServiceException();
    }

    @Override
    public void addRetrieveMethod(IntegerArrayWS groups) throws Exception_Exception {
        throw new WebServiceException();
    }

    @Override
    public void createReceiverUrls(EventReceiverUrlListWS eventReceiverUrlList) throws Exception_Exception {
        throw new WebServiceException();
    }

    @Override
    public void updateEvent(ParentEventWS parentEvent, Integer parentEventId, String user) throws Exception_Exception {
        throw new WebServiceException();
    }

    @Override
    public List<SecurityConfigurationWs> getSecurityConfigurations() {
        throw new WebServiceException();
    }

    @Override
    public ConfigEntryListWS getAcsConfiguration() {
        throw new WebServiceException();
    }

    @Override
    public void deleteUpdateGroups(IntegerArrayWS ids) throws Exception_Exception {
        throw new WebServiceException();
    }

    @Override
    public void updateUpdateGroup(UpdateGroupWS updateGroup, Integer updateGroupId, String user)
            throws Exception_Exception {
        throw new WebServiceException();
    }

    @Override
    public void removeRetrieveMethod(IntegerArrayWS groups) {
        throw new WebServiceException();
    }

    @Override
    public TransactionIdResponse addCPEObject(IntegerArrayWS cpeList, CpeObjectListWS cpeObjectList, boolean group,
                                              Integer priority, boolean push, String user) throws Exception_Exception {
        throw new WebServiceException();
    }

    @Override
    public void deleteLWM2MResourceDefinitions(String user, IntegerArrayWS objectDbIds, IntegerArrayWS resourceDbIds, Integer locationId) throws Exception_Exception {
        throw new WebServiceException();
    }

    @Override
    public TransactionIdResponse getParameterDataListFromCPE(IntegerArrayWS cpeList, ParameterDataListWS paramList,
                                                             Integer priority, boolean push, String user,
                                                             Integer cpeStatusCheckTimeout) throws Exception_Exception {
        throw new WebServiceException();
    }

    @Override
    public void startQoeMonitor(Integer monitoringId) throws Exception_Exception {
        throw new WebServiceException();
    }

    @Override
    public TransactionIdResponse downloadFiles(IntegerArrayWS cpeList, FileListWS fileList, Integer priority,
                                               boolean push, Long transactionId, String user, Integer locationId)
            throws Exception_Exception {
        throw new WebServiceException();
    }

    @Override
    public void addManufacturer(ManufacturerListWS manufacturerList) throws Exception_Exception {
        throw new WebServiceException();
    }

    @Override
    public void pauseUpdateGroup(Integer updateGroupId) throws Exception_Exception {
        throw new WebServiceException();
    }

    @Override
    public void activateXMPPOnCpe(Integer cpeId, Integer priority, Long transactionId, String user,
                                  Boolean editCurrentIfExists) {
        throw new WebServiceException();
    }

    @Override
    public void disableEncryption(StringArrayWS arg0) {

    }

    @Override
    public void importWhiteListFile(String creator, String description, String type, String base64FileContent,
                                    Boolean onlyCreated) throws Exception_Exception {
        throw new WebServiceException();
    }

    @Override
    public TransactionIdResponse factoryReset(IntegerArrayWS cpeList, Integer priority, boolean push,
                                              Long transactionId, String user) throws Exception_Exception {
        throw new WebServiceException();
    }

    @Override
    public void stopQoeMonitor(Integer monitoringId) throws Exception_Exception {
        throw new WebServiceException();
    }

    @Override
    public void checkLicense() {
        throw new WebServiceException();
    }

    @Override
    public TransactionIdResponse setCPEParams(IntegerArrayWS cpeList, CpeParamListWS cpeParamList, Integer priority,
                                              boolean group, boolean push, boolean reset, Long transactionId,
                                              String user) throws Exception_Exception {
        throw new WebServiceException();
    }

    @Override
    public StringResponse checkCPEWhiteList(Integer cpeId, String cpeSerial, String type) throws Exception_Exception {
        throw new WebServiceException();
    }

    @Override
    public void deleteSecurityConfigurationDetails(List<Integer> arg0) {
        throw new WebServiceException();
    }

    @Override
    public String getACSParam(String name) throws Exception_Exception {
        throw new WebServiceException();
    }

    @Override
    public void stopUpdateGroup(Integer updateGroupId) throws Exception_Exception {
        throw new WebServiceException();
    }

    @Override
    public void createAndStartEvent(ParentEventWS parentEvent, String user) throws Exception_Exception {
        throw new WebServiceException();
    }

    @Override
    public void createMonitoring(ParentMonitoringWS parentMonitoring, String user) throws Exception_Exception {
        throw new WebServiceException();
    }

    @Override
    public void deleteProfiles(IntegerArrayWS profileList) throws Exception_Exception {
        throw new WebServiceException();
    }

    @Override
    public void updateAccountInfo(CustDeviceWS accountInfo) throws Exception_Exception {
        throw new WebServiceException();
    }

    @Override
    public void createAndStartUpdateGroup(UpdateGroupWS updateGroup, String user) throws Exception_Exception {
        throw new WebServiceException();
    }

    @Override
    public void saveOrUpdateConfigurationParameter(ConfigurationParameter arg0) {
        throw new WebServiceException();
    }

    @Override
    public void createCustomView(CustomView customView) throws Exception_Exception {
        throw new WebServiceException();
    }

    @Override
    public void startSOAPTracingForUnknownCPEs(CpesoapTracingListWS cpeList, Integer duration)
            throws Exception_Exception {
        throw new WebServiceException();
    }

    @Override
    public ParameterListWS retrieveLicense() {
        throw new WebServiceException();
    }

    @Override
    public TransactionIdResponse deleteCPEObject(IntegerArrayWS cpeList, StringArrayWS cpeObjectList, Integer priority,
                                                 boolean push, Long transactionId, String user)
            throws Exception_Exception {
        throw new WebServiceException();
    }

    @Override
    public void deleteQoeMonitor(Integer monitoringId) throws Exception_Exception {
        throw new WebServiceException();
    }

    @Override
    public IspListWS addIsps(IspListWS ispsList) throws Exception_Exception {
        throw new WebServiceException();
    }

    @Override
    public void deleteCPEObjectWithoutTask(IntegerArrayWS cpeList, StringArrayWS cpeObjectList, Integer priority,
                                           String user) throws Exception_Exception {
        throw new WebServiceException();
    }

    @Override
    public void updateCustomView(CustomView customView) throws Exception_Exception {
        throw new WebServiceException();
    }

    @Override
    public TransactionIdResponse addCPEDiag(IntegerArrayWS cpeList, CpeDiagnosticWS cpeDiag, Integer priority,
                                            boolean push, String user) throws Exception_Exception {
        throw new WebServiceException();
    }

    @Override
    public void deleteManufacturerAndModel(ManufacturerAndModelListWS manufacturerAndModelList) throws Exception_Exception {

    }

    @Override
    public void deleteCPE(IntegerArrayWS cpeList, boolean deleteFromCpeSerial, boolean totalDelete)
            throws Exception_Exception {
        throw new WebServiceException();
    }

    @Override
    public InvokeRPCResponse invokeRPCMethod(IntegerArrayWS cpeList, RpcMethodListWS rpcMethodList, Integer priority,
                                             boolean push, Long transactionId, String user)
            throws Exception_Exception {
        throw new WebServiceException();
    }

    @Override
    public void saveOrUpdateSecurityConfiguration(SecurityConfigurationWs arg0) {
        throw new WebServiceException();
    }

    @Override
    public void createProfile(ProfileListWS profileList, String user) throws Exception_Exception {
        throw new WebServiceException();
    }

    @Override
    public void cleanCacheForCPE(IntegerArrayWS cpeList) throws Exception_Exception {
        throw new WebServiceException();
    }

    @Override
    public void deleteSecurityConfiguration(List<Integer> arg0) {
        throw new WebServiceException();
    }

    @Override
    public TransactionIdResponse getParameterNameAndValueFromCPE(IntegerArrayWS cpeList, String paramName,
                                                                 Integer priority, boolean push, String user)
            throws Exception_Exception {
        throw new WebServiceException();
    }

    @Override
    public void startEvent(Integer parentEventId) throws Exception_Exception {
        throw new WebServiceException();
    }

    @Override
    public void setCPEParamsWithoutTask(IntegerArrayWS cpeList, CpeParamListWS cpeParamList, Integer priority,
                                        boolean group, boolean reset, String user) throws Exception_Exception {
        throw new WebServiceException();
    }

    @Override
    public void createOrUpdateCpeSubDevice(String serial, String mac, CpeParamListWS cpeParamList)
            throws Exception_Exception {
        throw new WebServiceException();
    }

    @Override
    public TransactionIdResponse rebootCPE(IntegerArrayWS cpeList, Integer priority, boolean push,
                                           Long transactionId, String user) throws Exception_Exception {
        throw new WebServiceException();
    }

    @Override
    public TransactionIdResponse reprovisionCPE(IntegerArrayWS cpeList, boolean sendProfile, boolean sendCPEProvision,
                                                boolean sendCPEProvisionAttribute, boolean customRPC,
                                                boolean cpeProvisionObject, boolean cpeFile, Long transactionId,
                                                String user) throws Exception_Exception {
        throw new WebServiceException();
    }

    @Override
    public String getPingCPEResult(Integer cpe) throws Exception_Exception {
        throw new WebServiceException();
    }

    @Override
    public void setACSParam(String name, String value) throws Exception_Exception {
        throw new WebServiceException();
    }

    @Override
    public void stopMonitoring(Integer parentMonitoringId, boolean needReset) throws Exception_Exception {
        throw new WebServiceException();
    }

    @Override
    public void clearAccountInfo(String serial) throws Exception_Exception {
        throw new WebServiceException();
    }

    @Override
    public InvokeRPCResponse invokeRPCMethodWithoutTask(IntegerArrayWS cpeList, RpcMethodListWS rpcMethodList,
                                                        Integer priority, String user) throws Exception_Exception {
        throw new WebServiceException();
    }

    @Override
    public Integer dumpSNMPCpe(SnmpFilterStruct filterStruct) throws Exception_Exception {
        throw new WebServiceException();
    }

    @Override
    public TransactionIdResponse changeDUState(IntegerArrayWS cpeList, InstallOpListWS installOperations,
                                               UpdateOpListWS updateOperations, UnInstallOpListWS unInstallOperations,
                                               Integer priority, boolean push, boolean reset, Long transactionId,
                                               String user) throws Exception_Exception {
        throw new WebServiceException();
    }

    @Override
    public BooleanResponse deleteCPESFromWhiteListBySerials(StringArrayWS serialsList, String type)
            throws Exception_Exception {
        throw new WebServiceException();
    }

    @Override
    public TransactionIdResponse invokeCpeMethod(IntegerArrayWS cpeList, Integer cpeMethodNameId, Integer priority,
                                                 boolean push, Long transactionId, String user,
                                                 ParameterListWS parameters) throws Exception_Exception {
        throw new WebServiceException();
    }

    @Override
    public ParameterListWS getLicenseDataForChecking() {
        throw new WebServiceException();
    }

    @Override
    public void createAndStartMonitoring(ParentMonitoringWS parentMonitoring, String user) throws Exception_Exception {
        throw new WebServiceException();
    }

    @Override
    public void enableEncryption(StringArrayWS arg0) {

    }

    @Override
    public TransactionIdResponse discoverParameterDataListFromCPE(IntegerArrayWS cpeList, ParameterDataListWS paramList,
                                                                  Integer priority, boolean push, String user,
                                                                  Integer cpeStatusCheckTimeout)
            throws Exception_Exception {
        throw new WebServiceException();
    }

    @Override
    public TransactionIdResponse getParameterDataFromCPE(IntegerArrayWS cpeList, ParameterDataWS paramList,
                                                         Integer priority, boolean push, String user)
            throws Exception_Exception {
        throw new WebServiceException();
    }

    @Override
    public void updateProfile(ProfileWithIdListWS profileList, String user) throws Exception_Exception {
        throw new WebServiceException();
    }

    @Override
    public String getTracerouteCPEResult(Integer cpe) throws Exception_Exception {
        throw new WebServiceException();
    }

    @Override
    public void setCPEParamAttribsWithoutTask(IntegerArrayWS cpeList, CpeParamAttribListWS cpeParameterAttribList,
                                              Integer priority, boolean group, boolean reset, String user)
            throws Exception_Exception {
        throw new WebServiceException();
    }

    @Override
    public void deleteCPEForProductClass(String manufacturer, String endpoint) throws Exception_Exception {
        throw new WebServiceException();
    }

    @Override
    public void removeCPEProvisionData(IntegerArrayWS cpeList, RemoveCPEProvisionDataFlags flags)
            throws Exception_Exception {
        throw new WebServiceException();
    }

    @Override
    public void addLicense(String newLicense) throws Exception_Exception {
        throw new WebServiceException();
    }

    @Override
    public TransactionIdResponse setCPEParamAttribs(IntegerArrayWS cpeList, CpeParamAttribListWS cpeParameterAttribList,
                                                    Integer priority, boolean group, boolean push, boolean reset,
                                                    Long transactionId, String user) throws Exception_Exception {
        throw new WebServiceException();
    }

    @Override
    public void stopSOAPTracing(IntegerArrayWS cpeList) throws Exception_Exception {
        throw new WebServiceException();
    }

    @Override
    public void updateReceiverUrls(EventReceiverUrlListWS eventReceiverUrlList) throws Exception_Exception {
        throw new WebServiceException();
    }

    @Override
    public void startUpdateGroup(Integer updateGroupId) throws Exception_Exception {
        throw new WebServiceException();
    }

    @Override
    public void discoverSNMPCpe(SnmpFilterStruct filterStruct) throws Exception_Exception {
        throw new WebServiceException();
    }

    @Override
    public void removeSpecificCPEProvisionData(RemoveCPEProvisionDataList arg0) throws Exception_Exception {
        throw new WebServiceException();
    }

    @Override
    public void deleteCustomView(Integer id) throws Exception_Exception {
        throw new WebServiceException();
    }

    @Override
    public void addExtensionForGroup(ProductClassGroupExtension arg0) throws Exception_Exception {

    }

    @Override
    public StringResponse getACSVersion() {
        throw new WebServiceException();
    }

    @Override
    public void createEvent(ParentEventWS parentEvent, String user) throws Exception_Exception {
        throw new WebServiceException();
    }

    @Override
    public void removeReceiverUrls(IntegerArrayWS eventReceiverUrlIdsList) throws Exception_Exception {
        throw new WebServiceException();
    }

    @Override
    public DateResponse getServerDate() {
        throw new WebServiceException();
    }

    @Override
    public void pingCPE(Integer cpe) throws Exception_Exception {
        throw new WebServiceException();
    }

    @Override
    public void startMonitoring(Integer parentMonitoringId) throws Exception_Exception {
        throw new WebServiceException();
    }

    @Override
    public void addCPEObjectWithoutTask(IntegerArrayWS cpeList, CpeObjectListWS cpeObjectList, boolean group,
                                        Integer priority, String user) throws Exception_Exception {
        throw new WebServiceException();
    }

    @Override
    public TransactionIdResponse uploadFiles(IntegerArrayWS cpeList, CpeUploadFileArrayWS fileList, Integer priority,
                                             boolean push, Long transactionId, String user)
            throws Exception_Exception {
        throw new WebServiceException();
    }

    @Override
    public void changeHardcodedEvent(EventHardcodedListWS eventHardcodedList) throws Exception_Exception {
        throw new WebServiceException();
    }

    @Override
    public void startSOAPTracing(IntegerArrayWS cpeList, Integer duration) throws Exception_Exception {
        throw new WebServiceException();
    }

    @Override
    public void addCPEToBlackList(CpeBlackListWS cpeBlackList, String user) throws Exception_Exception {
        throw new WebServiceException();
    }

    @Override
    public void deleteMonitoring(IntegerArrayWS ids) throws Exception_Exception {
        throw new WebServiceException();
    }

    @Override
    public void addCPEToWhiteList(CpeWhiteListWS cpeWhiteList, String user) throws Exception_Exception {
        throw new WebServiceException();
    }

    @Override
    public void removeExtensionForGroup(ProductClassGroupId arg0) throws Exception_Exception {

    }

    @Override
    public void deleteCPESFromWhiteListByFileId(Integer cpeWhiteListId) throws Exception_Exception {
        throw new WebServiceException();
    }

    @Override
    public AcsPropertiesListWS getAcsProperties(String fileName) {
        return null;
    }

    @Override
    public Integer addNonTRHTTPCPE(Cpe cpews, Integer locationId, String url, String username, String password,
                                   String configFile) throws Exception_Exception {
        throw new WebServiceException();
    }

    @Override
    public void loadObjectDefinition(String arg0, String arg1) {
        throw new WebServiceException();
    }
}
