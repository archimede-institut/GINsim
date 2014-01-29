package org.ginsim.common.utils;

import org.ginsim.common.application.LogManager;
import org.ginsim.core.service.Alias;
import org.ginsim.gui.service.ServiceStatus;

public class ServiceClassInfo {


    public final Class cl;
    public final String alias;
    public final int status;


    public ServiceClassInfo(Class cl) {
        this.cl = cl;

        Alias alias = (Alias)cl.getAnnotation(Alias.class);
        if (alias != null) {
            this.alias = alias.value();
        } else {
            this.alias = null;
        }

        ServiceStatus service_status = (ServiceStatus)cl.getAnnotation( ServiceStatus.class);
        if (service_status != null) {
            this.status = service_status.value();
        } else {
            this.status = ServiceStatus.UNKNOWN;
        }
    }

    public String toString() {
        String s = "";
        if (alias != null) {
           s = alias;
        }

        s += "\t" + status;
        s += "\t" + cl.getPackage().getName();
        s += "\t" + cl.getSimpleName();

        return s;
    }
}
