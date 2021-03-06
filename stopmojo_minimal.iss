[Setup]
AppName=StopMojo 0.2
AppVerName=StopMojo 0.2
DefaultDirName={pf}\StopMojo
DefaultGroupName=StopMojo
AppCopyright=Copyright (c) 2005 Derone Bryson
ChangesAssociations=Yes
PrivilegesRequired=admin
LicenseFile=COPYING
RestartIfNeededByRun=Yes
AppId=STOPMOJO
OutputDir=\temp
OutputBaseFilename=stopmojo_min_setup

[Components]
Name: "capture"; Description: "StopMojo Capture"; Types: full compact custom; Flags: fixed


[Tasks]

[Files]
Source: "stopmojo_capture.ico"; DestDir: "{app}"; Flags: ignoreversion
Source: "\temp\stopmojo_capture.jar"; DestDir: "{app}"; Flags: ignoreversion
Source: "\workspace\stopmojo\plugins\com.mondobeyondo.stopmojo.capture.JMFCapturePlugin.JMFCapturePlugin.jar"; DestDir: "{app}\plugins"; Flags: ignoreversion
Source: "\workspace\stopmojo\plugins\com.mondobeyondo.stopmojo.capture.QTCapturePlugin.QTCapturePlugin.jar"; DestDir: "{app}\plugins"; Flags: ignoreversion
Source: "license.txt"; DestDir: "{app}"; Flags: ignoreversion
Source: "COPYING"; DestDir: "{app}"; DestName: "COPYING.txt"; Flags: ignoreversion

[Dirs]

[Registry]
Root: HKCR; Subkey: ".smp"; ValueType: string; ValueName: ""; ValueData: "StopMojoProject"; Flags: uninsdeletevalue
Root: HKCR; Subkey: "StopMojoProject"; ValueType: string; ValueName: ""; ValueData: "StopMojo Project"; Flags: uninsdeletekey
Root: HKCR; Subkey: "StopMojoProject\DefaultIcon"; ValueType: string; ValueName: ""; ValueData: "{app}\stopmojo_capture.ico"
Root: HKCR; Subkey: "StopMojoProject\shell\open\command"; ValueType: string; ValueName: ""; ValueData: """javaw.exe"" ""-jar"" ""{app}\stopmojo_capture.jar"" ""%1"""

[Icons]
Name: "{group}\StopMojo"; Filename: "javaw"; WorkingDir: "{app}"; Parameters: "-jar stopmojo_capture.jar"; IconFilename: "{app}\stopmojo_capture.ico"; Components: capture
Name: "{group}\Uninstall StopMojo"; Filename: "{app}\unins000.exe"; WorkingDir: "{app}";  Components: capture
Name: "{userdesktop}\StopMojo Capture"; Filename: "javaw"; WorkingDir: "{app}"; Parameters: "-jar stopmojo_capture.jar"; IconFilename: "{app}\stopmojo_capture.ico"; Components: capture
Name: "{group}\View StopMojo License"; Filename: "{app}\license.txt"; WorkingDir: "{app}"
Name: "{group}\View COPYING"; Filename: "{app}\COPYING.txt"; WorkingDir: "{app}"

[Registry]

[UninstallDelete]

[Run]

