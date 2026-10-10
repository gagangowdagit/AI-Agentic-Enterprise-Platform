$ErrorActionPreference = 'Stop'
Set-Location -LiteralPath $PSScriptRoot

$envFilePath = Join-Path $PSScriptRoot '.env'
if ([string]::IsNullOrWhiteSpace($env:JWT_SECRET)) {
    $envFileContent = ''
    if (Test-Path -LiteralPath $envFilePath) {
        $envFileContent = [System.IO.File]::ReadAllText($envFilePath)
    }

    $secretMatch = [regex]::Match($envFileContent, '(?m)^\s*JWT_SECRET\s*=\s*([^\r\n]*)')
    $existingSecret = if ($secretMatch.Success) { $secretMatch.Groups[1].Value.Trim() } else { '' }

    if (-not [string]::IsNullOrWhiteSpace($existingSecret)) {
        $env:JWT_SECRET = $existingSecret
    }
    else {
        $randomBytes = New-Object byte[] 32
        $randomGenerator = [System.Security.Cryptography.RandomNumberGenerator]::Create()
        try {
            $randomGenerator.GetBytes($randomBytes)
        }
        finally {
            $randomGenerator.Dispose()
        }

        $generatedSecret = [Convert]::ToBase64String($randomBytes)
        $lineEnding = if ($envFileContent.Length -gt 0 -and -not $envFileContent.EndsWith("`n")) { "`r`n" } else { '' }
        $updatedEnvFileContent = $envFileContent + $lineEnding + "JWT_SECRET=$generatedSecret`r`n"
        $utf8WithoutBom = New-Object System.Text.UTF8Encoding($false)
        [System.IO.File]::WriteAllText($envFilePath, $updatedEnvFileContent, $utf8WithoutBom)
        $env:JWT_SECRET = $generatedSecret
        Write-Host 'Created a persistent local JWT signing key in rag-backend\.env.'
    }
}

& .\mvnw.cmd spring-boot:run
exit $LASTEXITCODE
