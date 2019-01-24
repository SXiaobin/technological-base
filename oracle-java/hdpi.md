# [Java disable dpi-aware not working](http://stackoverflow.com/questions/30555401/java-disable-dpi-aware-not-working)

- Create a windows regedit new      DWORD

- 1. Press Windows       Button + R, type “regedit”, and then click OK.
  2. Navigate to the following       registry subkey:
                  HKEY_LOCAL_MACHINE > SOFTWARE > Microsoft > Windows >       CurrentVersion > SideBySide
  3. Right-click, select NEW       > DWORD (32 bit) Value
  4. Type       PreferExternalManifest, and then press ENTER.
  5. Right-click       PreferExternalManifest, and then click Modify.
  6. Enter Value Data 1 and       select Decimal.
  7. Click OK.

- Create the two .manifest      file (**JDK**)

- 1. Go to your java       JDK installation folder and open the bin directory
  2. Create a first file called       java.exe.manifest (add the code at the end of this post).
  3. Create a second one called       javaw.exe.manifest (add the code at the end of this post).

- Create the two .manifest      file (**JRE**)

- 1. Go to your java       JRE installation folder and open the bin directory
  2. Create a first file called       java.exe.manifest (add the code at the end of this post).
  3. Create a second one called       javaw.exe.manifest (add the code at the end of this post).

- Restart your java      application.

**Code to Paste into the .manifest files**

<?xml version="1.0" encoding="UTF-8" standalone="yes"?>

<assembly xmlns="urn:schemas-microsoft-com:asm.v1" manifestVersion="1.0" xmlns:asmv3="urn:schemas-microsoft-com:asm.v3">

<dependency>

  <dependentAssembly>

    <assemblyIdentity

      type="win32"


name="Microsoft.Windows.Common-Controls"

      version="6.0.0.0"
processorArchitecture="*"


publicKeyToken="6595b64144ccf1df"


language="*">

    </assemblyIdentity>

  </dependentAssembly>

</dependency>

<dependency>

  <dependentAssembly>

    <assemblyIdentity

      type="win32"


name="Microsoft.VC90.CRT"


version="9.0.21022.8"


processorArchitecture="amd64"


publicKeyToken="1fc8b3b9a1e18e3b">

    </assemblyIdentity>

  </dependentAssembly>

</dependency>

<trustInfo
xmlns="urn:schemas-microsoft-com:asm.v3">

  <security>


<requestedPrivileges>


<requestedExecutionLevel


level="asInvoker"


uiAccess="false"/>

    </requestedPrivileges>


</security>

</trustInfo>

<asmv3:application>
   <asmv3:windowsSettings xmlns="http://schemas.microsoft.com/SMI/2005/WindowsSettings">
     <ms_windowsSettings:dpiAware xmlns:ms_windowsSettings="http://schemas.microsoft.com/SMI/2005/WindowsSettings">false</ms_windowsSettings:dpiAware>
   </asmv3:windowsSettings>
 </asmv3:application>

</assembly>

It also works with other applications.

If you need to fix the DPI for a JNLP application launcher, you have to add the following key to the resources section inside the .jnlp file :

<property name="sun.java2d.dpiaware" value="false"/>

**After Upgrading Windows (e.g from win10 to win10 1607), you should apply this fix again if it doesn't work anymore.**

***\* Afer Updating Java, you should copy&paste .manifest files into new Java's directory.**