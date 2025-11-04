# SummerVeld Hound Resort

A comprehensive dog resort management system with Android mobile app and RESTful API backend.

## CI/CD Pipeline Status

[![CI/CD Pipeline](https://github.com/ST10336767/SummerVeld-Hound-Resort/workflows/CI/CD%20Pipeline/badge.svg)](https://github.com/ST10336767/SummerVeld-Hound-Resort/actions)
[![Security Scan](https://github.com/ST10336767/SummerVeld-Hound-Resort/workflows/Security%20Scan/badge.svg)](https://github.com/ST10336767/SummerVeld-Hound-Resort/actions)
[![CodeQL Analysis](https://github.com/ST10336767/SummerVeld-Hound-Resort/workflows/CodeQL%20Analysis/badge.svg)](https://github.com/ST10336767/SummerVeld-Hound-Resort/actions)

## Pipeline Features

### Implemented
- **GitHub Actions CI/CD Pipeline**
- **Backend Unit Tests** with Jest and Supertest
- **Android Unit Tests** with JUnit and Mockito
- **Static Code Analysis** with SonarQube and CodeQL
- **Security Scanning** with Snyk, OWASP, Trivy, and Semgrep
- **Docker Containerization** for backend
- **Cloud Deployment** configuration for multiple providers
- **Code Coverage** reporting
- **Automated Testing** on every commit

### Pipeline Components

#### 1. **Continuous Integration**
- **Backend Testing**: Node.js unit tests with Jest
- **Android Testing**: Kotlin unit tests with JUnit
- **Code Quality**: ESLint for JavaScript, static analysis
- **Build Verification**: Automated builds for both platforms

#### 2. **Static Code Analysis**
- **SonarQube**: Code quality and maintainability analysis
- **CodeQL**: GitHub's semantic code analysis
- **Coverage Reports**: Test coverage tracking and reporting

#### 3. **Security Scanning**
- **Snyk**: Dependency vulnerability scanning
- **OWASP Dependency Check**: Open source security analysis
- **Trivy**: Container and filesystem security scanning
- **Semgrep**: SAST (Static Application Security Testing)

#### 4. **Containerization**
- **Docker**: Backend API containerization
- **Docker Compose**: Multi-service orchestration
- **Security**: Non-root user, health checks, minimal base images

#### 5. **Deployment**
- **Render**: Primary deployment platform
- **AWS**: Alternative cloud deployment
- **Google Cloud**: Alternative cloud deployment
- **Azure**: Alternative cloud deployment

## Project Structure

```
SummerVeld-Hound-Resort/
├── .github/workflows/          # GitHub Actions CI/CD
│   ├── ci-cd.yml              # Main CI/CD pipeline
│   ├── codeql.yml             # CodeQL security analysis
│   ├── security-scan.yml       # Security scanning
│   └── deploy.yml              # Deployment workflows
├── android-app/                # Android mobile application
│   ├── app/src/test/          # Android unit tests
│   └── build.gradle.kts       # Build configuration
├── backend/                    # Node.js RESTful API
│   ├── tests/                 # Backend unit tests
│   ├── Dockerfile             # Container configuration
│   └── package.json           # Dependencies and scripts
├── docker-compose.yml          # Multi-service orchestration
├── sonar-project.properties    # SonarQube configuration
└── README.md                   # This file
```

## Testing Strategy

### Backend Tests
- **Unit Tests**: Individual function and module testing
- **Integration Tests**: API endpoint testing with Supertest
- **Coverage**: Jest coverage reporting with LCOV format
- **Mocking**: Comprehensive mocking for external dependencies

### Android Tests
- **Unit Tests**: Business logic and ViewModel testing
- **Instrumented Tests**: UI and integration testing
- **Mocking**: Mockito for dependency injection
- **Robolectric**: Android framework testing

## Security Features

### Automated Security Scanning
- **Dependency Scanning**: Automated vulnerability detection
- **SAST**: Static code security analysis
- **Container Security**: Docker image vulnerability scanning
- **Secrets Detection**: API keys and credentials scanning

### Security Tools Integration
- **Snyk**: Real-time vulnerability monitoring
- **OWASP**: Industry-standard security practices
- **Trivy**: Container and filesystem security
- **Semgrep**: Custom security rule detection

## Deployment

### Production Deployment
1. **Automatic**: Deploys on push to main branch
2. **Manual**: Triggered via GitHub Actions
3. **Multi-Cloud**: Support for Render, AWS, GCP, Azure

### Environment Configuration
- **Development**: Local development with hot reload
- **Staging**: Pre-production testing environment
- **Production**: Live production environment

## Quality Metrics

### Code Quality
- **Maintainability**: SonarQube maintainability rating
- **Reliability**: Bug detection and prevention
- **Security**: Vulnerability scanning and remediation
- **Coverage**: Test coverage percentage

### Performance
- **Build Time**: CI/CD pipeline execution time
- **Test Execution**: Unit test execution time
- **Deployment**: Application deployment time

## Development Workflow

### 1. **Code Commit**
```bash
git add .
git commit -m "feat: add new feature"
git push origin feature-branch
```

### 2. **Automated Pipeline**
- Code quality checks
- Unit test execution
- Security scanning
- Build verification

### 3. **Pull Request**
- Code review required
- All checks must pass
- Security scan approval

### 4. **Merge to Main**
- Automatic deployment
- Production deployment
- Health check verification

## Monitoring and Alerting

### Pipeline Monitoring
- **GitHub Actions**: Build status and execution time
- **SonarQube**: Code quality trends
- **Security**: Vulnerability alerts
- **Deployment**: Deployment success/failure

### Quality Gates
- **Test Coverage**: Minimum 80% coverage required
- **Security**: No high-severity vulnerabilities
- **Quality**: Maintainability rating A
- **Performance**: Build time under 10 minutes

## Local Development

### Prerequisites
- Node.js 18+
- Java 11+
- Android Studio
- Docker (optional)

### Setup
```bash
# Backend setup
cd backend
npm install
npm run dev

# Android setup
cd android-app
./gradlew build
```

### Running Tests
```bash
# Backend tests
cd backend
npm test
npm run test:coverage

# Android tests
cd android-app
./gradlew test
./gradlew connectedAndroidTest
```

## Documentation

- **API Documentation**: Backend API endpoints
- **Android Documentation**: Mobile app features
- **Pipeline Documentation**: CI/CD configuration
- **Security Documentation**: Security practices

## Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Run tests locally
5. Submit a pull request

The pipeline will automatically:
- Run all tests
- Perform security scans
- Check code quality
- Build the application

## Support

For questions about the CI/CD pipeline or development workflow, please:
- Check the GitHub Actions logs
- Review the SonarQube dashboard
- Consult the security scan results
- Create an issue for bugs or feature requests

---

**Pipeline Status**: Fully Operational  
**Last Updated**: January 2025  
**Maintainer**: Development Team