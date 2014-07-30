package java.nio.file;
import java.nio.file.spi.FileSystemProvider;
import java.net.URI;
import java.io.IOException;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.*;
import java.lang.reflect.Constructor;
/** 
 * Factory methods for file systems. This class defines the {@link #getDefaultgetDefault} method to get the default file system and factory methods to
 * construct other types of file systems.
 * <p> The first invocation of any of the methods defined by this class causes
 * the default {@link FileSystemProvider provider} to be loaded. The default
 * provider, identified by the URI scheme "file", creates the {@link FileSystem}that provides access to the file systems accessible to the Java virtual
 * machine. If the process of loading or initializing the default provider fails
 * then an unspecified error is thrown.
 * <p> The first invocation of the {@link FileSystemProvider#installedProvidersinstalledProviders} method, by way of invoking any of the {@codenewFileSystem} methods defined by this class, locates and loads all
 * installed file system providers. Installed providers are loaded using the
 * service-provider loading facility defined by the {@link ServiceLoader} class.
 * Installed providers are loaded using the system class loader. If the
 * system class loader cannot be found then the extension class loader is used;
 * if there is no extension class loader then the bootstrap class loader is used.
 * Providers are typically installed by placing them in a JAR file on the
 * application class path or in the extension directory, the JAR file contains a
 * provider-configuration file named {@code java.nio.file.spi.FileSystemProvider}in the resource directory {@code META-INF/services}, and the file lists one or
 * more fully-qualified names of concrete subclass of {@link FileSystemProvider}that have a zero argument constructor.
 * The ordering that installed providers are located is implementation specific.
 * If a provider is instantiated and its {@link FileSystemProvider#getScheme()getScheme} returns the same URI scheme of a provider that was previously
 * instantiated then the most recently instantiated duplicate is discarded. URI
 * schemes are compared without regard to case. During construction a provider
 * may safely access files associated with the default provider but care needs
 * to be taken to avoid circular loading of other installed providers. If
 * circular loading of installed providers is detected then an unspecified error
 * is thrown.
 * <p> This class also defines factory methods that allow a {@link ClassLoader}to be specified when locating a provider. As with installed providers, the
 * provider classes are identified by placing the provider configuration file
 * in the resource directory {@code META-INF/services}.
 * <p> If a thread initiates the loading of the installed file system providers
 * and another thread invokes a method that also attempts to load the providers
 * then the method will block until the loading completes.
 * @since 1.7
 */
public final class FileSystems {
  private FileSystems(){
  }
private static class DefaultFileSystemHolder {
    static final FileSystem defaultFileSystem=defaultFileSystem();
    private static FileSystem defaultFileSystem(){
      FileSystemProvider provider=AccessController.doPrivileged(new PrivilegedAction<FileSystemProvider>(){
        public FileSystemProvider run(){
          return getDefaultProvider();
        }
      }
);
      return provider.getFileSystem(URI.create("file:///"));
    }
    private static FileSystemProvider getDefaultProvider(){
      FileSystemProvider provider=sun.nio.fs.DefaultFileSystemProvider.create();
      String propValue=System.getProperty("java.nio.file.spi.DefaultFileSystemProvider");
      if (propValue != null) {
        for (        String cn : propValue.split(",")) {
          try {
            Class<?> c=Class.forName(cn,true,ClassLoader.getSystemClassLoader());
            Constructor<?> ctor=c.getDeclaredConstructor(FileSystemProvider.class);
            provider=(FileSystemProvider)ctor.newInstance(provider);
            if (!provider.getScheme().equals("file"))             throw new Error("Default provider must use scheme 'file'");
          }
 catch (          Exception x) {
            throw new Error(x);
          }
        }
      }
      return provider;
    }
  }
  /** 
 * Returns the default {@code FileSystem}. The default file system creates
 * objects that provide access to the file systems accessible to the Java
 * virtual machine. The <em>working directory</em> of the file system is
 * the current user directory, named by the system property {@code user.dir}.
 * This allows for interoperability with the {@link java.io.File java.io.File}class.
 * <p> The first invocation of any of the methods defined by this class
 * locates the default {@link FileSystemProvider provider} object. Where the
 * system property {@code java.nio.file.spi.DefaultFileSystemProvider} is
 * not defined then the default provider is a system-default provider that
 * is invoked to create the default file system.
 * <p> If the system property {@code java.nio.file.spi.DefaultFileSystemProvider}is defined then it is taken to be a list of one or more fully-qualified
 * names of concrete provider classes identified by the URI scheme{@code "file"}. Where the property is a list of more than one name then
 * the names are separated by a comma. Each class is loaded, using the system
 * class loader, and instantiated by invoking a one argument constructor
 * whose formal parameter type is {@code FileSystemProvider}. The providers
 * are loaded and instantiated in the order they are listed in the property.
 * If this process fails or a provider's scheme is not equal to {@code "file"}then an unspecified error is thrown. URI schemes are normally compared
 * without regard to case but for the default provider, the scheme is
 * required to be {@code "file"}. The first provider class is instantiated
 * by invoking it with a reference to the system-default provider.
 * The second provider class is instantiated by invoking it with a reference
 * to the first provider instance. The third provider class is instantiated
 * by invoking it with a reference to the second instance, and so on. The
 * last provider to be instantiated becomes the default provider; its {@codegetFileSystem} method is invoked with the URI {@code "file:///"} to
 * get a reference to the default file system.
 * <p> Subsequent invocations of this method return the file system that was
 * returned by the first invocation.
 * @return  the default file system
 */
  public static FileSystem getDefault(){
    return DefaultFileSystemHolder.defaultFileSystem;
  }
  /** 
 * Returns a reference to an existing {@code FileSystem}.
 * <p> This method iterates over the {@link FileSystemProvider#installedProviders()installed} providers to locate the provider that is identified by the URI{@link URI#getScheme scheme} of the given URI. URI schemes are compared
 * without regard to case. The exact form of the URI is highly provider
 * dependent. If found, the provider's {@link FileSystemProvider#getFileSystemgetFileSystem} method is invoked to obtain a reference to the {@codeFileSystem}.
 * <p> Once a file system created by this provider is {@link FileSystem#closeclosed} it is provider-dependent if this method returns a reference to
 * the closed file system or throws {@link FileSystemNotFoundException}.
 * If the provider allows a new file system to be created with the same URI
 * as a file system it previously created then this method throws the
 * exception if invoked after the file system is closed (and before a new
 * instance is created by the {@link #newFileSystem newFileSystem} method).
 * <p> If a security manager is installed then a provider implementation
 * may require to check a permission before returning a reference to an
 * existing file system. In the case of the {@link FileSystems#getDefaultdefault} file system, no permission check is required.
 * @throws IllegalArgumentExceptionif the pre-conditions for the {@code uri} parameter are not met
 * @throws FileSystemNotFoundExceptionif the file system, identified by the URI, does not exist
 * @throws ProviderNotFoundExceptionif a provider supporting the URI scheme is not installed
 * @throws SecurityExceptionif a security manager is installed and it denies an unspecified
 * permission
 */
  public static FileSystem getFileSystem(  URI uri){
    String scheme=uri.getScheme();
    for (    FileSystemProvider provider : FileSystemProvider.installedProviders()) {
      if (scheme.equalsIgnoreCase(provider.getScheme())) {
        return provider.getFileSystem(uri);
      }
    }
    throw new ProviderNotFoundException("Provider \"" + scheme + "\" not found");
  }
  /** 
 * Constructs a new file system that is identified by a {@link URI}<p> This method iterates over the {@link FileSystemProvider#installedProviders()installed} providers to locate the provider that is identified by the URI{@link URI#getScheme scheme} of the given URI. URI schemes are compared
 * without regard to case. The exact form of the URI is highly provider
 * dependent. If found, the provider's {@link FileSystemProvider#newFileSystem(URI,Map)newFileSystem(URI,Map)} method is invoked to construct the new file system.
 * <p> Once a file system is {@link FileSystem#close closed} it is
 * provider-dependent if the provider allows a new file system to be created
 * with the same URI as a file system it previously created.
 * <p> <b>Usage Example:</b>
 * Suppose there is a provider identified by the scheme {@code "memory"}installed:
 * <pre>
 * Map&lt;String,String&gt; env = new HashMap&lt;&gt;();
 * env.put("capacity", "16G");
 * env.put("blockSize", "4k");
 * FileSystem fs = FileSystems.newFileSystem(URI.create("memory:///?name=logfs"), env);
 * </pre>
 * @param urithe URI identifying the file system
 * @param enva map of provider specific properties to configure the file system;
 * may be empty
 * @return  a new file system
 * @throws IllegalArgumentExceptionif the pre-conditions for the {@code uri} parameter are not met,
 * or the {@code env} parameter does not contain properties required
 * by the provider, or a property value is invalid
 * @throws FileSystemAlreadyExistsExceptionif the file system has already been created
 * @throws ProviderNotFoundExceptionif a provider supporting the URI scheme is not installed
 * @throws IOExceptionif an I/O error occurs creating the file system
 * @throws SecurityExceptionif a security manager is installed and it denies an unspecified
 * permission required by the file system provider implementation
 */
  public static FileSystem newFileSystem(  URI uri,  Map<String,?> env) throws IOException {
    return newFileSystem(uri,env,null);
  }
  /** 
 * Constructs a new file system that is identified by a {@link URI}<p> This method first attempts to locate an installed provider in exactly
 * the same manner as the {@link #newFileSystem(URI,Map) newFileSystem(URI,Map)}method. If none of the installed providers support the URI scheme then an
 * attempt is made to locate the provider using the given class loader. If a
 * provider supporting the URI scheme is located then its {@link FileSystemProvider#newFileSystem(URI,Map) newFileSystem(URI,Map)} is
 * invoked to construct the new file system.
 * @param urithe URI identifying the file system
 * @param enva map of provider specific properties to configure the file system;
 * may be empty
 * @param loaderthe class loader to locate the provider or {@code null} to only
 * attempt to locate an installed provider
 * @return  a new file system
 * @throws IllegalArgumentExceptionif the pre-conditions for the {@code uri} parameter are not met,
 * or the {@code env} parameter does not contain properties required
 * by the provider, or a property value is invalid
 * @throws FileSystemAlreadyExistsExceptionif the URI scheme identifies an installed provider and the file
 * system has already been created
 * @throws ProviderNotFoundExceptionif a provider supporting the URI scheme is not found
 * @throws ServiceConfigurationErrorwhen an error occurs while loading a service provider
 * @throws IOExceptionan I/O error occurs creating the file system
 * @throws SecurityExceptionif a security manager is installed and it denies an unspecified
 * permission required by the file system provider implementation
 */
  public static FileSystem newFileSystem(  URI uri,  Map<String,?> env,  ClassLoader loader) throws IOException {
    String scheme=uri.getScheme();
    for (    FileSystemProvider provider : FileSystemProvider.installedProviders()) {
      if (scheme.equalsIgnoreCase(provider.getScheme())) {
        return provider.newFileSystem(uri,env);
      }
    }
    if (loader != null) {
      ServiceLoader<FileSystemProvider> sl=ServiceLoader.load(FileSystemProvider.class,loader);
      for (      FileSystemProvider provider : sl) {
        if (scheme.equalsIgnoreCase(provider.getScheme())) {
          return provider.newFileSystem(uri,env);
        }
      }
    }
    throw new ProviderNotFoundException("Provider \"" + scheme + "\" not found");
  }
  /** 
 * Constructs a new {@code FileSystem} to access the contents of a file as a
 * file system.
 * <p> This method makes use of specialized providers that create pseudo file
 * systems where the contents of one or more files is treated as a file
 * system.
 * <p> This method iterates over the {@link FileSystemProvider#installedProviders()installed} providers. It invokes, in turn, each provider's {@link FileSystemProvider#newFileSystem(Path,Map) newFileSystem(Path,Map)} method
 * with an empty map. If a provider returns a file system then the iteration
 * terminates and the file system is returned. If none of the installed
 * providers return a {@code FileSystem} then an attempt is made to locate
 * the provider using the given class loader. If a provider returns a file
 * system then the lookup terminates and the file system is returned.
 * @param paththe path to the file
 * @param loaderthe class loader to locate the provider or {@code null} to only
 * attempt to locate an installed provider
 * @return  a new file system
 * @throws ProviderNotFoundExceptionif a provider supporting this file type cannot be located
 * @throws ServiceConfigurationErrorwhen an error occurs while loading a service provider
 * @throws IOExceptionif an I/O error occurs
 * @throws SecurityExceptionif a security manager is installed and it denies an unspecified
 * permission
 */
  public static FileSystem newFileSystem(  Path path,  ClassLoader loader) throws IOException {
    if (path == null)     throw new NullPointerException();
    Map<String,?> env=Collections.emptyMap();
    for (    FileSystemProvider provider : FileSystemProvider.installedProviders()) {
      try {
        return provider.newFileSystem(path,env);
      }
 catch (      UnsupportedOperationException uoe) {
      }
    }
    if (loader != null) {
      ServiceLoader<FileSystemProvider> sl=ServiceLoader.load(FileSystemProvider.class,loader);
      for (      FileSystemProvider provider : sl) {
        try {
          return provider.newFileSystem(path,env);
        }
 catch (        UnsupportedOperationException uoe) {
        }
      }
    }
    throw new ProviderNotFoundException("Provider not found");
  }
}