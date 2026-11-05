' Silent windowless desktop launcher for Smart Hostel Management System
Set fso = CreateObject("Scripting.FileSystemObject")
Set shell = CreateObject("WScript.Shell")
baseDir = fso.GetParentFolderName(WScript.ScriptFullName)

exePath = baseDir & "\dist\Smart-Hostel-Management-System\Smart-Hostel-Management-System.exe"
jarPath = baseDir & "\target\Smart-Hostel-Management-System-jar-with-dependencies.jar"

If fso.FileExists(exePath) Then
    shell.CurrentDirectory = fso.GetParentFolderName(exePath)
    shell.Run Chr(34) & exePath & Chr(34), 1, False
ElseIf fso.FileExists(jarPath) Then
    shell.CurrentDirectory = baseDir
    shell.Run "javaw -jar " & Chr(34) & jarPath & Chr(34), 0, False
Else
    shell.Run "cmd /c """ & baseDir & "\run.bat""", 1, False
End If
