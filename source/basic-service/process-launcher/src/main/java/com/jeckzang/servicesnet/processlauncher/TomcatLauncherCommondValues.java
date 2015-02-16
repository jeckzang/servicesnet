package com.polycom.ngma.processlauncher;

import java.io.File;

import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;

import com.polycom.ngma.core.shared.utils.CommonCommandValues;

public class TomcatLauncherCommondValues extends CommonCommandValues
{
    @Option(name = "-W", aliases = { "--warFileDir" }, required = true, usage = "input web app dir location")
    private File warFileDir;
    @Option(name = "-P", aliases = { "--port" }, required = true, usage = "input port for web app use")
    private int port;
    @Option(name = "-B", aliases = { "--warBaseDir" }, required = true, usage = "input web app dir location")
    private File warBaseDir;
    @Option(name = "-C", aliases = { "--contextPath" }, required = true, usage = "input web app context path")
    private String contextPath;
    @Option(name = "-S", aliases = { "--sslEnable" }, required = false, usage = "input web server enable SSL connection")
    private boolean sslEnable;
    @Option(name = "-K", aliases = { "--keyStoreFile" }, required = false, usage = "input key store file path")
    private String keyStoreFile;
    @Option(name = "-A", aliases = { "--keystorePass" }, required = false, usage = "input key store password")
    private String keystorePass;

    public TomcatLauncherCommondValues(String... args)
    {
        super(args);
        CmdLineParser parser = new CmdLineParser(this);
        parser.setUsageWidth(80);
        try
        {
            parser.parseArgument(args);
            if (!getWarFileDir().exists() || !getWarFileDir().isFile())
            {
                throw new CmdLineException(parser, "--warFileDir is no valid dir.");
            }
            if (!getWarBaseDir().exists() || !getWarBaseDir().isDirectory())
            {
                throw new CmdLineException(parser, "--warBaseDir is no valid dir.");
            }
            if (port == 0)
            {
                throw new CmdLineException(parser, "--port is no valid port value.");
            }
            errorFree = true;
        }
        catch (CmdLineException e)
        {
            System.err.println(e.getMessage());
            parser.printUsage(System.err);
        }
    }

    public int getPort()
    {
        return port;
    }

    public File getWarFileDir()
    {
        return warFileDir;
    }

    public File getWarBaseDir()
    {
        return warBaseDir;
    }

    public String getContextPath()
    {
        return contextPath;
    }

    public boolean isSslEnable()
    {
        return sslEnable;
    }

    public String getKeyStoreFile()
    {
        return keyStoreFile;
    }

    public String getKeystorePass()
    {
        return keystorePass;
    }

    @Override
    public String toString()
    {
        return super.toString() + " TomcatLauncherCommondValues [warFileDir=" + warFileDir + ", port=" + port
                + ", warBaseDir=" + warBaseDir + ", contextPath=" + contextPath + ", sslEnable=" + sslEnable
                + ", keyStoreFile=" + keyStoreFile + ", keystorePass=" + keystorePass + "]";
    }
}
