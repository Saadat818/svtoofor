Set oWS = WScript.CreateObject("WScript.Shell")
sLinkFile = oWS.SpecialFolders("Desktop") & "\Светофор.lnk"
Set oLink = oWS.CreateShortcut(sLinkFile)
oLink.TargetPath = oWS.CurrentDirectory & "\run-svetoofor.bat"
oLink.WorkingDirectory = oWS.CurrentDirectory
oLink.IconLocation = "C:\Users\Admin\OneDrive\Picture\svetofor.ico"
oLink.Description = "Приложение Светофор"
oLink.Save
WScript.Echo "Ярлык создан на рабочем столе!"
