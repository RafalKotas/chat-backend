# run-sonar-analysis.ps1

# Check if SONAR_TOKEN is set
if (-not $env:SONAR_TOKEN) {
    Write-Host "Error: SONAR_TOKEN environment variable is not set!" -ForegroundColor Red
    Write-Host "To set the SONAR_TOKEN, use the following command in PowerShell:" -ForegroundColor Yellow
    Write-Host "`$env:SONAR_TOKEN='YourSonarTokenHere'" -ForegroundColor Yellow
    exit 1
}

# Define Maven command arguments
$mvnCommand = @(
    "clean",
    "test",
    "jacoco:report",
    "sonar:sonar",
    "-Dsonar.projectKey=chat-backend",
    "-Dsonar.projectName=Chat Backend",
    "-Dsonar.host.url=http://localhost:9000",
    "-Dsonar.token=$env:SONAR_TOKEN",
    "-Dsonar.coverage.jacoco.xmlReportPaths=target/site/jacoco/jacoco.xml"
)


# Inform the user that the analysis is starting
Write-Host "Executing SonarQube analysis..." -ForegroundColor Cyan

# Execute Maven with the defined command
mvn $mvnCommand
