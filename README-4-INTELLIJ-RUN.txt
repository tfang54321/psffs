Step 1:
 - File > Settings > Build, Execution, Deployment > Build Tools > Gradle
   - Choose the "Use default gradle wrapper (recommended)" radio button
   - Set the "Gradle JVM" to "Use Project JDK"
   - Uncheck the "auto-import" checkbox

Step 2:
 - File > Settings > Build, Execution, Deployment > Build Tools > Gradle > Runner
   - Check the "Delegate IDE build/run actions to gradle" checkbox

Step 3:
 - Open the "Gradle" tab on the right side of IntelliJ
   - Click the "Refresh all gradle projects" button (looks like refresh icon)
   - Click the "Execute gradle task" button (looks like an elephant)
     - Type "clean compileJava" into the "Command line" input box and click "Ok"

Step 5:
 - In the "Project" explorer on the left, find trunk/src/main/generated
   - Right click the "generated" folder
   - Mouse over the "Mark Directory as" option
   - Choose "Generated sources root"

Step 5:
 - Create a run configuration that is a "Spring Boot Application"

Step 6:
 - Click the "Run" or "Debug" button next to the run configuration to build/start the application.