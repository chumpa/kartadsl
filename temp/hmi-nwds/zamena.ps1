
Get-ChildItem -Path "." -Filter "*.log" | ForEach-Object {
    $content = Get-Content $_.FullName -Raw
    $content -replace 'realuser', 'poduser' | Set-Content $_.FullName
}
