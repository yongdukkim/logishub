package com.logishub.mobile.launcher.v5.DATA;

import java.io.Serializable;
import java.util.List;

/**
 * Created by SPICE on 2016-10-26.
 */

public class IntegrationFNSListData implements Serializable {
    private IntegrationMainFnsData integrationMainFNS;
    private List<IntegrationSubFnsData> integrationSubFNSList;

    public IntegrationMainFnsData getIntegrationMainFNS() {
        return integrationMainFNS;
    }

    public void setIntegrationMainFNS(IntegrationMainFnsData integrationMainFNS) {
        this.integrationMainFNS = integrationMainFNS;
    }

    public List<IntegrationSubFnsData> getIntegrationSubFNSList() {
        return integrationSubFNSList;
    }

    public void setIntegrationSubFNSList(List<IntegrationSubFnsData> integrationSubFNSList) {
        this.integrationSubFNSList = integrationSubFNSList;
    }
}
