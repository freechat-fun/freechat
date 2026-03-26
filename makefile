build:
	mvn --version
	mvn -U clean package -DskipTests
# Analyze code for errors, potential issues, and coding standard violations.
# Reports problems but does not modify the code.
lint:
	mvn -T12C -Pspotless spotless:check
# Automatically format the code to conform to a style guide.
# Modifies the code to ensure consistent formatting.
format:
	mvn -T12C -Pspotless spotless:apply
# Check all files regardless of changes from origin/main.
lint-all:
	mvn -T12C -Pspotless-all spotless:check
# Format all files regardless of changes from origin/main.
format-all:
	mvn -T12C -Pspotless-all spotless:apply
