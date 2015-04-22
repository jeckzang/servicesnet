package com.jeckzang.servicesnet.processlauncher;

import java.io.IOException;

import javax.servlet.ServletException;

import org.apache.catalina.LifecycleException;
import org.apache.catalina.connector.Connector;
import org.apache.catalina.startup.Tomcat;
import org.apache.coyote.http11.Http11NioProtocol;
import org.apache.tomcat.jni.File;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TomcatLauncher
{
    private static Logger log = LoggerFactory.getLogger(TomcatLauncher.class);

    public static void main(String[] args)
    {

        TomcatLauncherCommondValues values = new TomcatLauncherCommondValues(args);
        if (!values.isErrorFree())
        {
            return;
        }
        log.info("Runtime paramenters:{}", values);
        // set env
        System.setProperty("uuid", values.getUuid());
        System.setProperty("type", values.getType());
        System.setProperty("name", values.getName());
        try
        {
            File baseDir = values.getWarBaseDir();
            if (!baseDir.exists())
            {
                baseDir.mkdirs();
            }
            File webApps = new File(baseDir, "webapps");
            if (!webApps.exists())
            {
                webApps.mkdirs();
            }
            Tomcat tomcat = new Tomcat();
            tomcat.setBaseDir(values.getWarBaseDir().getCanonicalPath());
            tomcat.setPort(values.getPort());
            tomcat.addWebapp(values.getContextPath(), values.getWarFileDir().getCanonicalPath());
            tomcat.setConnector(getConnector(values));
            tomcat.getService().addConnector(getConnector(values));
            tomcat.start();
            tomcat.getServer().await();
        }
        catch (ServletException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        catch (LifecycleException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        catch (IOException e)
        {
            log.error("Unexpected Exception", e);
        }
    }

    private static Connector getConnector(TomcatLauncherCommondValues values)
    {
        Connector connector = new Connector();
        connector.setProtocol("HTTP/1.1");
        connector.setPort(values.getPort());
        if (values.isSslEnable())
        {
            Http11NioProtocol protocolHandler = (Http11NioProtocol) connector.getProtocolHandler();
            protocolHandler.setSSLEnabled(true);
            protocolHandler.setKeystoreFile(values.getKeyStoreFile());
            protocolHandler.setKeystorePass(values.getKeystorePass());
            connector.setScheme("https");
        }
        return connector;
    }
}
