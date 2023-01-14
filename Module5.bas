'add to module 3
Sub EnterNotes(commentInput As String, teamNum As String)

'First, go to that teams tab
    Sheets(teamNum).Select
'Second, find the word COMMENTS in column A
For iloop = 8 To 50
    CommentCell = Range("A" & Format(iloop)).value
    If CommentCell = "COMMENTS" Then
'Third, insert the comment at row
     Range("A" & Format(iloop + 1)).Select
     Selection.Insert
     ActiveCell.FormulaR1C1 = commentInput
    End If
Next

Sheets("Data Entry").Select
    End Sub


'module 5
Sub process1QRCodeInput()
    saveData (getInput())
End Sub

Sub process6QRCodeInput()
    saveData getInput(), "4"
    saveData getInput(), "5"
    saveData getInput(), "6"
    saveData getInput(), "7"
    saveData getInput(), "8"
    saveData getInput(), "9"
End Sub
Public Function getInput()
    getInput = InputBox("Scan QR Code", "Match Scouting Input")
End Function

Public Function ArrayLen(arr As Variant) As Integer
    ArrayLen = UBound(arr) - LBound(arr) + 1
End Function

Sub saveData(ByVal inp As String, Optional ByVal rowNum As String = "6")
    'CHANGE THIS FOR EVERY ENTRY FROM ROW 4-9 DEPENDING ON THE DATA ENTRY SHEET
    Dim fields
    Dim par
    Dim value
    Dim key
    Dim mapper
    Set mapper = CreateObject("Scripting.Dictionary")
    Set data = CreateObject("Scripting.Dictionary")
    Dim tableName As String
    tableName = "ScoutingData"

    'Set up map fields for every year
    mapper.Add "rp", "rp" 'robot field pos
    mapper.Add "cp", "cp" 'cargo preload?
    mapper.Add "ta", "ta" 'taxied? auton
    mapper.Add "aca", "aca" 'auton cargo acquired
    mapper.Add "ucsa", "ucsa" 'upper cargo scored auton
    mapper.Add "lcsa", "lcsa" 'lower cargo scored auton
    mapper.Add "cmda", "cmda" 'cargo missed/dropped auton
    mapper.Add "tca", "tca" 'teleop cargo acquired
    mapper.Add "ucst", "ucst" 'upper cargo scored teleop
    mapper.Add "lcst", "lcst" 'lower cargo scored teleop
    mapper.Add "cmdt", "cmdt" 'cargo missed/dropped teleop
    mapper.Add "dp", "dp" 'defensive performance
    mapper.Add "de", "de" 'defensive evasion
    mapper.Add "cla", "cla" 'climb attempted?
    mapper.Add "cl", "cl" 'climb level
    mapper.Add "f", "f" 'fouls
    mapper.Add "tf", "tf" 'tech fouls
    mapper.Add "ml", "ml" 'match level
    mapper.Add "mn", "mn" 'match number
    
    mapper.Add "ran", "ran" 'robot alliance number
    mapper.Add "co", "co" 'comments
    mapper.Add "dt", "dt" 'died/tipped?
    
    'terminates if there is no input
    If inp = "" Then
        Exit Sub
    End If

    'splits into pairs
    fields = Split(inp, ";")
    If ArrayLen(fields) > 0 Then
        Dim str

        'splits pairs
        For Each str In fields
            par = Split(str, "=")
            key = par(0)
            value = par(1)
            If mapper.Exists(key) Then
                key = mapper(key)
            End If
            data.Add key, value
        Next
                
        'manual bash to put data where it needs to be in data entry sheet
        'note the switch in 1st and 2nd line, caused by accident in qr output, don't change
        Worksheets("Data Entry").Range("C" + rowNum) = data.Item("rp")
        Worksheets("Data Entry").Range("D" + rowNum) = data.Item("cp")
        Worksheets("Data Entry").Range("E" + rowNum) = data.Item("ta")
        Worksheets("Data Entry").Range("F" + rowNum) = data.Item("aca")
        Worksheets("Data Entry").Range("G" + rowNum) = data.Item("ucsa")
        Worksheets("Data Entry").Range("H" + rowNum) = data.Item("lcsa")
        Worksheets("Data Entry").Range("I" + rowNum) = data.Item("cmda")
        Worksheets("Data Entry").Range("J" + rowNum) = data.Item("tca")
        Worksheets("Data Entry").Range("K" + rowNum) = data.Item("ucst")
        Worksheets("Data Entry").Range("L" + rowNum) = data.Item("lcst")
        Worksheets("Data Entry").Range("M" + rowNum) = data.Item("cmdt")
        Worksheets("Data Entry").Range("N" + rowNum) = data.Item("dp")
        Worksheets("Data Entry").Range("O" + rowNum) = data.Item("de")
        Worksheets("Data Entry").Range("P" + rowNum) = data.Item("cla")
        Worksheets("Data Entry").Range("Q" + rowNum) = data.Item("cl")
        Worksheets("Data Entry").Range("R" + rowNum) = data.Item("f")
        Worksheets("Data Entry").Range("S" + rowNum) = data.Item("tf")
        
        If data.Item("dt") = 1 Then
            Call EnterNotes(data.Item("sln") & "-" & data.Item("ran") & "-" & data.Item("ml") & data.Item("mn") & "-died/tipped; " & data.Item("co"), Range("A" & rowNum).value)
        Else
           Call EnterNotes(data.Item("sln") & "-" & data.Item("ran") & "-" & data.Item("ml") & data.Item("mn") & "-" & data.Item("co"), Range("A" & rowNum).value)
        End If
    End If
End Sub

