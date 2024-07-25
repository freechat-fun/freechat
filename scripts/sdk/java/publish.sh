mvn deploy -P sign-artifacts -DskipTests \
  -DargLine="--add-opens java.base/java.util=ALL-UNNAMED" \
  -Dgpg.executable=gpg \
  -Dgpg.keyname=3BB1C678 \
  -Dgpg.passphrase=${MAVEN_GPG_PASSPHRASE}
