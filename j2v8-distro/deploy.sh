upload() {
  VERSION=4.6.0
  POM=META-INF/maven/com.eclipsesource.j2v8/$NAME/pom.xml
  FILE=$NAME-$VERSION
  cd target
  rm -rf *
  mvn dependency:get -Dartifact=com.eclipsesource.j2v8:$NAME:$VERSION -Ddest=$FILE.jar
  mvn dependency:get -Dartifact=com.eclipsesource.j2v8:$NAME:$VERSION -Dclassifier=sources -Ddest=$FILE-sources.jar
  unzip -o $FILE.jar META-INF/*
  echo "Automatic-Module-Name: $MODULE" >> META-INF/MANIFEST.MF
  zip $FILE.jar META-INF/MANIFEST.MF
  cp $POM .
  sed -i.bak 's|<groupId>com.eclipsesource.j2v8</groupId>|<groupId>guru.nidi.com.eclipsesource.j2v8</groupId>|' pom.xml
  #sed -i.bak 's|<version>4.6.0</version>|<version>4.6.0</version>|' pom.xml
  sed -i.bak 's|<plugins>|<plugins><plugin><artifactId>maven-antrun-plugin</artifactId><executions><execution><phase>verify</phase><goals><goal>run</goal></goals><configuration><tasks><copy file="${project.build.finalName}.jar" todir="target" overwrite="true"/><copy file="${project.build.finalName}-sources.jar" todir="target" overwrite="true"/></tasks></configuration></execution></executions></plugin>|' pom.xml
  mvn deploy -Prelease
  #mvn nexus-staging:release -Prelease
  cd ..
}

mkdir -p target
#NAME=j2v8_macosx_x86_64 MODULE=j2v8_macosx_x64 upload
NAME=j2v8_linux_x86_64 MODULE=j2v8_linux_x64 upload
NAME=j2v8_win32_x86 MODULE=j2v8_win32_x86 upload
NAME=j2v8_win32_x86_64 MODULE=j2v8_win32_x64 upload
