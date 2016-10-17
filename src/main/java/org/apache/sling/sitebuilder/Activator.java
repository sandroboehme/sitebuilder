package org.apache.sling.sitebuilder;

import java.util.Dictionary;
import java.util.Hashtable;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;

public class Activator implements BundleActivator {
    /** The fs resource provider factory. */  
    private static final String FS_FACTORY = "org.apache.sling.fsprovider.internal.FsResourceProvider";
    
	private ConfigurationAdmin configurationAdmin;
	private Configuration fsProviderConfig;

	@Override
	public void start(BundleContext context) throws Exception {
//		configurationAdmin = (ConfigurationAdmin) context.getService(context.getServiceReference("org.osgi.service.cm.ConfigurationAdmin"));
//		fsProviderConfig = configurationAdmin.createFactoryConfiguration(FS_FACTORY, null);
//		Dictionary<String, String> fsProviderProps = new Hashtable<>();
//		fsProviderProps.put("provider.roots", "/jspm2");
//		fsProviderProps.put("provider.file", "jspm");
//        
//		fsProviderConfig.update(fsProviderProps);
	}

	@Override
	public void stop(BundleContext context) throws Exception {
//		fsProviderConfig.delete();
//		fsProviderConfig = null;
//		configurationAdmin = null;
	}
}
