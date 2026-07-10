
Get-ChildItem -Path "." -Filter "*.log" | ForEach-Object {
    $content = Get-Content $_.FullName -Raw
    $content -replace ':company:', ':rsugio:' | Set-Content $_.FullName
}
