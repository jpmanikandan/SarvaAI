$source = "C:\Users\jp manikandan\.gemini\antigravity\brain\6a0b86ea-5851-4f83-96f0-820c064bd820"
$dest = "Sarva_Documentation"
$zip = "Sarva_Documentation.zip"

if (Test-Path $dest) { Remove-Item $dest -Recurse -Force }
if (Test-Path $zip) { Remove-Item $zip -Force }

New-Item -ItemType Directory -Force -Path $dest | Out-Null

Write-Host "Copying files..."
Copy-Item "$source\architecture_document.md" -Destination "$dest\1_Architecture_Document.md"
Copy-Item "$source\functional_document.md" -Destination "$dest\2_Functional_Document.md"
Copy-Item "$source\technical_document.md" -Destination "$dest\3_Technical_Document.md"
Copy-Item "$source\product_documentation.md" -Destination "$dest\4_Product_Documentation.md"

Write-Host "Zipping files..."
Compress-Archive -Path "$dest\*" -DestinationPath $zip -Force

Write-Host "Documentation packaged successfully into $zip"
